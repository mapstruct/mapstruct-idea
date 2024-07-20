/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.daemon.impl.quickfix.SafeDeleteFix;
import com.intellij.codeInsight.intention.AddAnnotationPsiFix;
import com.intellij.codeInsight.intention.QuickFixFactory;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
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
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapstructAnnotationUtils;
import org.mapstruct.intellij.util.MapstructUtil;

import java.util.ArrayList;
import java.util.List;

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
    private static final String COMPONENT_MODEL = "componentModel";

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
                if ( !( argument instanceof PsiClassObjectAccessExpression classObjectAccessExpression ) ) {
                    return;
                }
                PsiJavaCodeReferenceElement referenceElement = classObjectAccessExpression.getOperand()
                    .getInnermostComponentReferenceElement();

                if ( referenceElement == null ) {
                    return;
                }

                PsiElement mapperElement = referenceElement.resolve();

                if ( !( mapperElement instanceof PsiClass mapperClass ) ) {
                    return;
                }

                PsiAnnotation mapperAnnotation = mapperClass.getAnnotation( MapstructUtil.MAPPER_ANNOTATION_FQN );
                if ( mapperAnnotation == null ) {
                    List<LocalQuickFix> fixes = new ArrayList<>(2);
                    fixes.add( new AddAnnotationPsiFix(
                            MapstructUtil.MAPPER_ANNOTATION_FQN,
                            mapperClass,
                            PsiNameValuePair.EMPTY_ARRAY
                    ) );
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
                            COMPONENT_MODEL
                    );
                    PsiAnnotationMemberValue memberValue;

                    if (componentModelAttribute != null) {
                        memberValue = componentModelAttribute.getDetachedValue();
                    }
                    else {
                        memberValue = MapstructAnnotationUtils.findConfigValueFromMapperConfig( mapperAnnotation,
                                COMPONENT_MODEL );
                    }
                    String componentModel = memberValue == null ? null :
                            AnnotationUtil.getStringAttributeValue(  memberValue );
                    if ( componentModel != null && !componentModel.equals( "default" ) ) {
                        List<LocalQuickFix> fixes = new ArrayList<>(2);
                        if (componentModelAttribute != null) {
                            fixes.add(  createRemoveComponentModelFix( componentModelAttribute, mapperClass ) );
                        }
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

    private static class RemoveMappersFix extends SafeDeleteFix {

        private final String myText;
        private final String myFamilyName;

        private RemoveMappersFix(@NotNull PsiVariable element) {
            super( element );
            this.myFamilyName = MapStructBundle.message( "inspection.wrong.usage.mappers.factory" );
            this.myText = MapStructBundle.message( "inspection.wrong.usage.mappers.factory.remove.mappers.usage" );
        }

        @NotNull
        @Override
        public String getText() {
            return myText;
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
        if ( parent instanceof PsiVariable parentPsiVariable ) {

                return new RemoveMappersFix( parentPsiVariable );
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
