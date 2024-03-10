/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.daemon.impl.quickfix.RemoveUnusedVariableFix;
import com.intellij.codeInsight.intention.AddAnnotationPsiFix;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.QuickFixFactory;
import com.intellij.codeInspection.IntentionWrapper;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.modcommand.ActionContext;
import com.intellij.modcommand.ModChooseAction;
import com.intellij.modcommand.ModCommand;
import com.intellij.modcommand.ModCommandAction;
import com.intellij.modcommand.Presentation;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.PsiUtil;
import com.siyeh.ig.callMatcher.CallMatcher;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapstructUtil;

/**
 * Inspection that checks that Mappers factory is correctly used
 *
 * @author Filip Hrisafov
 */
public class WrongUsageOfMappersFactoryInspection extends InspectionBase {

    private static final CallMatcher MAPPERS_FACTORY_CALL_MATCHER = CallMatcher.staticCall(
        MapstructUtil.MAPPERS_FQN,
        "getMapper"
    ).parameterTypes( CommonClassNames.JAVA_LANG_CLASS );

    private static final Method AS_INTENTION_ACTION_METHOD;
    private static final Method AS_INTENTION;

    static {
        Method asIntentionActionMethod;
        try {
            asIntentionActionMethod = RemoveMappersFix.class.getMethod( "asIntentAction" );
        }
        catch ( NoSuchMethodException e ) {
            asIntentionActionMethod = null;
        }

        AS_INTENTION_ACTION_METHOD = asIntentionActionMethod;

        Method asIntention;
        try {
            asIntention = RemoveMappersFix.class.getMethod( "asIntention" );
        }
        catch ( NoSuchMethodException e ) {
            asIntention = null;
        }

        AS_INTENTION = asIntention;
    }

    @NotNull
    @Override
    PsiElementVisitor buildVisitorInternal(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new MyJavaElementVisitor( holder );
    }

    private static class MyJavaElementVisitor extends JavaElementVisitor {
        private final ProblemsHolder problemsHolder;

        private MyJavaElementVisitor(ProblemsHolder problemsHolder) {
            this.problemsHolder = problemsHolder;
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            if ( MAPPERS_FACTORY_CALL_MATCHER.test( expression ) ) {
                PsiExpression argument = PsiUtil.skipParenthesizedExprDown( expression.getArgumentList()
                    .getExpressions()[0] );
                if ( !( argument instanceof PsiClassObjectAccessExpression ) ) {
                    return;
                }
                PsiClassObjectAccessExpression classObjectAccessExpression = (PsiClassObjectAccessExpression) argument;
                PsiJavaCodeReferenceElement referenceElement = classObjectAccessExpression.getOperand()
                    .getInnermostComponentReferenceElement();

                if ( referenceElement == null ) {
                    return;
                }

                PsiElement mapperElement = referenceElement.resolve();

                if ( !( mapperElement instanceof PsiClass ) ) {
                    return;
                }

                PsiClass mapperClass = (PsiClass) mapperElement;
                PsiAnnotation mapperAnnotation = mapperClass.getAnnotation( MapstructUtil.MAPPER_ANNOTATION_FQN );
                if ( mapperAnnotation == null ) {
                    Collection<LocalQuickFix> fixes = new ArrayList<>( 2 );
                    fixes.add(
                        new AddAnnotationPsiFix(
                            MapstructUtil.MAPPER_ANNOTATION_FQN,
                            mapperClass,
                            PsiNameValuePair.EMPTY_ARRAY
                        )
                    );
                    LocalQuickFix removeMappersFix = createRemoveMappersFix( expression );
                    if ( removeMappersFix != null ) {
                        fixes.add( removeMappersFix );
                    }
                    problemsHolder.registerProblem(
                        expression,
                        MapStructBundle.message( "inspection.wrong.usage.mappers.factory.non.mapstruct" ),
                        fixes.toArray( LocalQuickFix[]::new )
                    );
                }
                else {
                    PsiNameValuePair componentModelAttribute = AnnotationUtil.findDeclaredAttribute(
                        mapperAnnotation,
                        "componentModel"
                    );
                    PsiAnnotationMemberValue memberValue = componentModelAttribute == null ?
                        null :
                        componentModelAttribute.getDetachedValue();
                    String componentModel = memberValue == null ?
                        null :
                        AnnotationUtil.getStringAttributeValue( memberValue );
                    if ( componentModel != null && !componentModel.equals( "default" ) ) {
                        List<LocalQuickFix> fixes = new ArrayList<>( 2 );
                        fixes.add( createRemoveComponentModelFix( componentModelAttribute, mapperClass ) );
                        LocalQuickFix removeMappersFix = createRemoveMappersFix( expression );
                        if ( removeMappersFix != null ) {
                            fixes.add( removeMappersFix );
                        }
                        problemsHolder.registerProblem(
                            expression,
                            MapStructBundle.message( "inspection.wrong.usage.mappers.factory.non.default" ),
                            fixes.toArray( LocalQuickFix[]::new )
                        );
                    }
                }
            }
        }
    }

    private static class RemoveMappersFix extends RemoveUnusedVariableFix {

        private final String myText;
        private final String myFamilyName;

        private RemoveMappersFix(@NotNull PsiVariable element) {
            super( element );
            this.myFamilyName = MapStructBundle.message( "inspection.wrong.usage.mappers.factory" );
            this.myText = MapStructBundle.message( "inspection.wrong.usage.mappers.factory.remove.mappers.usage" );
        }

        // This method is there for prior to 2023.3
        @NotNull
        public String getText() {
            return myText;
        }

        // This method is there for after to 2023.3
        @NotNull
        protected String getText(@NotNull PsiVariable variable) {
            return myText;
        }

        // This method is there for after to 2023.3
        protected @Nullable Presentation getPresentation( @NotNull ActionContext context,
                                                          @NotNull PsiVariable variable) {
            return Presentation.of( myText );
        }

        // This method is there for after to 2023.3
        protected @NotNull ModCommand perform(@NotNull ActionContext context, @NotNull PsiVariable variable) {
            ModCommand modCommand = super.perform( context, variable );
            if ( modCommand instanceof ModChooseAction ) {
                ModChooseAction modChooseAction = (ModChooseAction) modCommand;
                List<? extends @NotNull ModCommandAction> actions = modChooseAction.actions();
                return (actions.size() > 1 ? actions.get( 1 ) : actions.get( 0 )).perform( context );
            }
            return modCommand;
        }

        @Nls
        @NotNull
        @Override
        public String getFamilyName() {
            return myFamilyName;
        }
    }

    private static LocalQuickFix createRemoveMappersFix(@NotNull PsiMethodCallExpression methodCallExpression) {
        PsiElement parent = methodCallExpression.getParent();
        if ( parent instanceof PsiVariable ) {
            RemoveMappersFix fix = new RemoveMappersFix( (PsiVariable) parent );
            IntentionAction action = null;
            if ( fix instanceof IntentionAction ) {
                action = (IntentionAction) fix;
            }
            else if ( AS_INTENTION_ACTION_METHOD != null ) {
                try {
                    Object intentionAction = AS_INTENTION_ACTION_METHOD.invoke( fix );
                    if ( intentionAction instanceof IntentionAction ) {
                        action = (IntentionAction) intentionAction;
                    }
                }
                catch ( IllegalAccessException | InvocationTargetException e ) {
                    action = null;
                }
            }
            else if ( AS_INTENTION != null ) {
                try {
                    Object intentionAction = AS_INTENTION.invoke( fix );
                    if ( intentionAction instanceof IntentionAction ) {
                        action = (IntentionAction) intentionAction;
                        if ( !action.isAvailable( methodCallExpression.getProject(), null,
                                methodCallExpression.getContainingFile() ) ) {
                            action = null;
                        }
                    }
                }
                catch ( IllegalAccessException | InvocationTargetException e ) {
                    action = null;
                }
            }

            if ( action == null ) {
                return null;
            }
            return IntentionWrapper.wrapToQuickFix(
                action,
                methodCallExpression.getContainingFile()
            );
        }

        return null;
    }

    private static LocalQuickFix createRemoveComponentModelFix(@NotNull PsiNameValuePair componentModelPair,
        @NotNull PsiClass psiClass) {
        return QuickFixFactory.getInstance().createDeleteFix(
            componentModelPair,
            MapStructBundle.message(
                "inspection.wrong.usage.mappers.factory.remove.component.model",
                componentModelPair.getLiteralValue(),
                psiClass.getName()
            )
        );
    }

}
