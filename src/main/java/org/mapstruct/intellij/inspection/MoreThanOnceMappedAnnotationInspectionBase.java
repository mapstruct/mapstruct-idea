/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.intellij.codeInsight.intention.QuickFixFactory;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationImpl;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapStructVersion;
import org.mapstruct.intellij.util.MapstructUtil;

import static org.mapstruct.intellij.util.TargetUtils.getTargetType;

/**
 * @author hduelme
 * @param <K> type of the problemElement
 */
public abstract class MoreThanOnceMappedAnnotationInspectionBase<K> extends InspectionBase {

    @Override
    @NotNull PsiElementVisitor buildVisitorInternal(@NotNull ProblemsHolder holder, boolean isOnTheFly ) {
        return new MyJavaElementVisitor( holder,
                MapstructUtil.resolveMapStructProjectVersion( holder.getFile() ) );
    }

    private class MyJavaElementVisitor extends JavaElementVisitor {
        private final ProblemsHolder holder;
        private final MapStructVersion mapStructVersion;

        private MyJavaElementVisitor(ProblemsHolder holder, MapStructVersion mapStructVersion) {
            this.holder = holder;
            this.mapStructVersion = mapStructVersion;
        }

        @Override
        public void visitMethod(PsiMethod method) {
            if ( !MapstructUtil.isMapper( method.getContainingClass() ) ) {
                return;
            }
            PsiType targetType = getTargetType( method );
            if ( targetType == null ) {
                return;
            }
            Map<K, List<PsiElement>> problemMap = new HashMap<>();
            for ( PsiAnnotation psiAnnotation : method.getAnnotations() ) {
                String qualifiedName = psiAnnotation.getQualifiedName();
                if ( getSingleMappingAnnotationFqn().equals( qualifiedName ) ) {
                    handleSingleMappingAnnotation( psiAnnotation, problemMap );
                }
                else if ( getRepeatableMappingsAnnotationFqn().equals( qualifiedName ) ) {
                    extractAnnotationsFromRepeatableMappingsAnnotation( psiAnnotation )
                            .forEach( a -> handleSingleMappingAnnotation( a, problemMap ) );
                }
                else {
                    // Handle annotations containing at least one Mapping annotation
                    handleAnnotationWithMappingAnnotations( psiAnnotation, problemMap );
                }
            }
            QuickFixFactory quickFixFactory = QuickFixFactory.getInstance();
            for ( Map.Entry<K, List<PsiElement>> problem : problemMap.entrySet() ) {
                List<PsiElement> problemElements = problem.getValue();
                if ( problemElements.size() > 1 ) {
                    for ( PsiElement problemElement : problemElements ) {
                        LocalQuickFix[] quickFixes = getLocalQuickFixes( problemElement, quickFixFactory );
                        holder.registerProblem( problemElement,
                                getProblemDescription( problem.getKey() ), quickFixes );
                    }
                }
            }
        }

        private @NotNull  LocalQuickFix[] getLocalQuickFixes(PsiElement problemElement,
                                                                    QuickFixFactory quickFixFactory) {
            List<LocalQuickFix> quickFixes = new ArrayList<>(2);
            if ( problemElement instanceof PsiAnnotation ) {
                quickFixes.add( getDeleteFix( problemElement, quickFixFactory ) );
                // Todo Goto quick Fix
            }
            else if ( problemElement instanceof PsiAnnotationMemberValue problemPsiAnnotationMemberValue ) {
                Optional.ofNullable( problemElement.getParent() ).map( PsiElement::getParent )
                        .map( PsiElement::getParent ).filter( PsiAnnotation.class::isInstance )
                        .ifPresent( annotation -> quickFixes.add(
                                getDeleteFix( annotation, quickFixFactory ) ) );
                quickFixes.add( getChangeTargetQuickFix( problemPsiAnnotationMemberValue ) );
            }
            return quickFixes.toArray( new LocalQuickFix[]{} );
        }

        private static @NotNull LocalQuickFixAndIntentionActionOnPsiElement getDeleteFix(
                @NotNull PsiElement problemElement, @NotNull QuickFixFactory quickFixFactory) {

            String annotationName = PsiAnnotationImpl.getAnnotationShortName( problemElement.getText() );
            return quickFixFactory.createDeleteFix( problemElement,
                    MapStructBundle.message( "intention.remove.annotation", annotationName ) );
        }

        private void handleSingleMappingAnnotation(@NotNull PsiAnnotation psiAnnotation,
                                                              @NotNull Map<K, List<PsiElement>> problemMap) {
            PsiAnnotationMemberValue value = psiAnnotation.findDeclaredAttributeValue( getAttributeName() );
            if ( value == null ) {
                return;
            }
            extractCompareKeyFromAnnotationMember( value )
                    .ifPresent( key ->  problemMap.computeIfAbsent( key, s -> new ArrayList<>() ).add( value ) );
        }

        private void handleAnnotationWithMappingAnnotations(PsiAnnotation psiAnnotation,
                                                            Map<K, List<PsiElement>> problemMap) {
            PsiClass annotationClass = psiAnnotation.resolveAnnotationType();
            if ( annotationClass == null ) {
                return;
            }
            findAllDefinedMappings( annotationClass, mapStructVersion )
                    .forEach( target -> {
                        PsiAnnotationMemberValue value = target.findDeclaredAttributeValue( getAttributeName() );
                        if ( value == null ) {
                            return;
                        }
                        extractCompareKeyFromAnnotationMember( value )
                                .ifPresent( key ->  problemMap.computeIfAbsent( key, s -> new ArrayList<>() )
                                        .add( psiAnnotation ) );
                    } );
        }

    }

    @NotNull
    protected abstract String getSingleMappingAnnotationFqn();

    @NotNull
    protected abstract String getRepeatableMappingsAnnotationFqn();

    @NotNull
    protected abstract String getAttributeName();

    @NotNull
    protected abstract Stream<PsiAnnotation> findAllDefinedMappings(@NotNull PsiModifierListOwner owner,
                                                                    @NotNull MapStructVersion mapStructVersion);

    protected abstract Optional<K> extractCompareKeyFromAnnotationMember(
            @NotNull PsiAnnotationMemberValue annotationMemberValue );

    protected abstract LocalQuickFix getChangeTargetQuickFix(
            @NotNull PsiAnnotationMemberValue problemPsiAnnotationMemberValue);

    protected abstract String getProblemDescription(@NotNull K problemKey);

    @NotNull
    protected abstract Stream<PsiAnnotation> extractAnnotationsFromRepeatableMappingsAnnotation(
            @NotNull  PsiAnnotation mappings);

    protected static class ChangeTargetStringQuickFixBase extends LocalQuickFixOnPsiElement {

        private final String myText;
        private final String myFamilyName;

        protected ChangeTargetStringQuickFixBase(@NotNull PsiAnnotationMemberValue element,
                                                 @NotNull String myText, @NotNull String myFamilyName) {
            super( element );
            this.myText = myText;
            this.myFamilyName = myFamilyName;
        }

        @Override
        public @IntentionName @NotNull String getText() {
            return myText;
        }

        @Override
        public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull PsiElement psiElement,
                           @NotNull PsiElement psiElement1) {
            FileEditor selectedEditor = FileEditorManager.getInstance( project ).getSelectedEditor();
            if ( selectedEditor instanceof TextEditor textEditor ) {
                Editor editor = textEditor.getEditor();

                TextRange textRange = psiElement.getTextRange();
                String textOfElement = String.valueOf( editor.getDocument()
                        .getCharsSequence()
                        .subSequence( textRange.getStartOffset(), textRange.getEndOffset() ) );
                int targetStart = Strings.indexOf( textOfElement, "\"" ) + 1;
                int targetEnd = textOfElement.lastIndexOf( "\"" );

                editor.getCaretModel().moveToOffset( textRange.getStartOffset() + targetStart );
                LogicalPosition startPosition = editor.getCaretModel().getLogicalPosition();
                editor.getCaretModel().moveToOffset( textRange.getStartOffset() + targetEnd );
                editor.getCaretModel().setCaretsAndSelections(
                        Collections.singletonList( new CaretState(startPosition, startPosition,
                                editor.getCaretModel().getLogicalPosition() ) ) );
                editor.getScrollingModel().scrollToCaret( ScrollType.MAKE_VISIBLE );
            }
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return myFamilyName;
        }

        @Override
        public boolean availableInBatchMode() {
            return false;
        }

        @Override
        public boolean startInWriteAction() {
            return false;
        }
    }

}
