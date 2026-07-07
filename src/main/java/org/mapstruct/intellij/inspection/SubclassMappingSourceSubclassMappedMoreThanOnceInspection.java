/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
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
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapStructVersion;

import static org.mapstruct.intellij.util.MapstructAnnotationUtils.extractSubclassMappingAnnotations;
import static org.mapstruct.intellij.util.MapstructAnnotationUtils.findAllDefinedSubclassMappingAnnotations;
import static org.mapstruct.intellij.util.MapstructUtil.SUBCLASS_MAPPINGS_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.SUBCLASS_MAPPING_ANNOTATION_FQN;

/**
 * @author hduelme
 */
public class SubclassMappingSourceSubclassMappedMoreThanOnceInspection
        extends MoreThanOnceMappedAnnotationInspectionBase<PsiClass> {

    @NotNull
    @Override
    protected String getSingleMappingAnnotationFqn() {
        return SUBCLASS_MAPPING_ANNOTATION_FQN;
    }

    @NotNull
    @Override
    protected String getRepeatableMappingsAnnotationFqn() {
        return SUBCLASS_MAPPINGS_ANNOTATION_FQN;
    }

    @NotNull
    @Override
    protected String getAttributeName() {
        return "source";
    }

    @NotNull
    @Override
    protected Stream<PsiAnnotation> findAllDefinedMappings(@NotNull PsiModifierListOwner owner,
                                                           @NotNull MapStructVersion mapStructVersion) {
        return findAllDefinedSubclassMappingAnnotations( owner, true );
    }

    @Override
    protected Optional<PsiClass> extractCompareKeyFromAnnotationMember(
            @NotNull PsiAnnotationMemberValue annotationMemberValue) {
        if ( !( annotationMemberValue instanceof PsiClassObjectAccessExpression sourceClass ) ) {
            return Optional.empty();
        }
        PsiType sourceType = sourceClass.getOperand().getType();
        if ( sourceType instanceof PsiClassType classType ) {
            return Optional.ofNullable( classType.resolve() );
        }
        return Optional.empty();
    }

    @Override
    protected LocalQuickFix getChangeTargetQuickFix(@NotNull PsiAnnotationMemberValue problemPsiAnnotationMemberValue) {
        return new ChangeTargetQuickFix( problemPsiAnnotationMemberValue );
    }

    @Override
    protected String getProblemDescription(@NotNull PsiClass problemKey) {
        return MapStructBundle.message( "inspection.subclass.mapping.source.subclass.already.defined",
                problemKey.getQualifiedName() );
    }

    @NotNull
    @Override
    protected Stream<PsiAnnotation> extractAnnotationsFromRepeatableMappingsAnnotation(
            @NotNull PsiAnnotation mappings) {
        return extractSubclassMappingAnnotations( mappings );
    }

    private static class ChangeTargetQuickFix extends LocalQuickFixOnPsiElement {

        private final String myText;
        private final String myFamilyName;

        private ChangeTargetQuickFix(@NotNull PsiAnnotationMemberValue element) {
            super( element );
            myText = MapStructBundle.message( "intention.change.source.property" );
            myFamilyName = MapStructBundle.message( "inspection.subclass.mapping.source.subclass.already.defined",
                    element.getText() );
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

                TextRange textRange = ((PsiClassObjectAccessExpression) psiElement).getOperand().getTextRange();

                editor.getCaretModel().moveToOffset( textRange.getStartOffset() );
                LogicalPosition startPosition = editor.getCaretModel().getLogicalPosition();
                editor.getCaretModel().moveToOffset( textRange.getEndOffset() );
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
    }
}
