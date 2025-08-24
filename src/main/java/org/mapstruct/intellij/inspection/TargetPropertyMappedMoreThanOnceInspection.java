/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

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
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationImpl;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapStructVersion;
import org.mapstruct.intellij.util.MapstructUtil;
import org.mapstruct.intellij.util.TargetUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.intellij.codeInsight.AnnotationUtil.getStringAttributeValue;
import static org.mapstruct.intellij.util.MapstructAnnotationUtils.extractMappingAnnotationsFromMappings;
import static org.mapstruct.intellij.util.MapstructUtil.MAPPINGS_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.MAPPING_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.TargetUtils.getTargetType;

/**
 * @author hduelme
 */
public class TargetPropertyMappedMoreThanOnceInspection extends InspectionBase {
    @NotNull
    @Override
    PsiElementVisitor buildVisitorInternal(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new TargetPropertyMappedMoreThanOnceInspection.MyJavaElementVisitor( holder,
                MapstructUtil.resolveMapStructProjectVersion( holder.getFile() ) );
    }

    private static class MyJavaElementVisitor extends JavaElementVisitor {
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
            Map<String, List<PsiElement>> problemMap = new HashMap<>();
            for (PsiAnnotation psiAnnotation : method.getAnnotations()) {
                String qualifiedName = psiAnnotation.getQualifiedName();
                if ( MAPPING_ANNOTATION_FQN.equals( qualifiedName ) ) {
                    handleMappingAnnotation( psiAnnotation, problemMap );
                }
                else if (MAPPINGS_ANNOTATION_FQN.equals( qualifiedName )) {
                    extractMappingAnnotationsFromMappings( psiAnnotation )
                            .forEach( a -> handleMappingAnnotation( a, problemMap ) );
                }
                else {
                    // Handle annotations containing at least one Mapping annotation
                    handleAnnotationWithMappingAnnotation( psiAnnotation, problemMap );
                }
            }
            QuickFixFactory quickFixFactory = QuickFixFactory.getInstance();
            for (Map.Entry<String, List<PsiElement>> problem : problemMap.entrySet()) {
                List<PsiElement> problemElements = problem.getValue();
                if (problemElements.size() > 1) {
                    for (PsiElement problemElement : problemElements) {
                        LocalQuickFix[] quickFixes = getLocalQuickFixes( problemElement, quickFixFactory );
                        holder.registerProblem( problemElement,
                               MapStructBundle.message( "inspection.target.property.mapped.more.than.once",
                                       problem.getKey() ), quickFixes );
                    }
                }
            }
        }

        private static @NotNull  LocalQuickFix[] getLocalQuickFixes(PsiElement problemElement,
                                                                       QuickFixFactory quickFixFactory) {
            List<LocalQuickFix> quickFixes = new ArrayList<>(2);
            if (problemElement instanceof PsiAnnotation) {
                quickFixes.add( getDeleteFix( problemElement, quickFixFactory ) );
            }
            else if (problemElement instanceof PsiAnnotationMemberValue problemPsiAnnotationMemberValue) {
                Optional.ofNullable( problemElement.getParent() ).map( PsiElement::getParent )
                        .map( PsiElement::getParent ).filter( PsiAnnotation.class::isInstance )
                        .ifPresent( annotation -> quickFixes.add(
                                getDeleteFix( annotation, quickFixFactory ) ) );
                quickFixes.add( new ChangeTargetQuickFix( problemPsiAnnotationMemberValue ) );
            }
            return quickFixes.toArray( new LocalQuickFix[]{} );
        }

        private static @NotNull LocalQuickFixAndIntentionActionOnPsiElement getDeleteFix(
                @NotNull PsiElement problemElement, @NotNull QuickFixFactory quickFixFactory) {

            String annotationName = PsiAnnotationImpl.getAnnotationShortName( problemElement.getText() );
            return quickFixFactory.createDeleteFix( problemElement,
                    MapStructBundle.message( "intention.remove.annotation", annotationName ) );
        }

        private void handleAnnotationWithMappingAnnotation(PsiAnnotation psiAnnotation,
                                                           Map<String, List<PsiElement>> problemMap) {
            PsiClass annotationClass = psiAnnotation.resolveAnnotationType();
            if (annotationClass == null) {
                return;
            }
            TargetUtils.findAllDefinedMappingTargets( annotationClass, mapStructVersion )
                    .forEach( target ->
                            problemMap.computeIfAbsent( target, k -> new ArrayList<>() ).add( psiAnnotation ) );
        }

        private static void handleMappingAnnotation(PsiAnnotation psiAnnotation,
                                                    Map<String, List<PsiElement>> problemMap) {
            PsiAnnotationMemberValue value = psiAnnotation.findDeclaredAttributeValue( "target" );
            if (value != null) {
                String target = getStringAttributeValue( value );
                if (target != null && !target.equals( "." )) {
                    problemMap.computeIfAbsent( target, k -> new ArrayList<>() ).add( value );
                }
            }
        }

        private static class ChangeTargetQuickFix extends LocalQuickFixOnPsiElement {

            private final String myText;
            private final String myFamilyName;

            private ChangeTargetQuickFix(@NotNull PsiAnnotationMemberValue element) {
                super( element );
                myText = MapStructBundle.message( "intention.change.target.property" );
                myFamilyName = MapStructBundle.message( "inspection.target.property.mapped.more.than.once",
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
                if ( selectedEditor instanceof TextEditor textEditor) {
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
        }
    }
}
