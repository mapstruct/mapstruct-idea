/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;

import static com.intellij.psi.PsiElementFactory.getInstance;
import static org.mapstruct.intellij.expression.JavaExpressionInjector.JAVA_EXPRESSION;

public class JavaExpressionUnnecessaryWhitespacesInspector extends MappingAnnotationInspectionBase {

    @Override
    void visitMappingAnnotation(@NotNull ProblemsHolder problemsHolder, @NotNull PsiAnnotation psiAnnotation,
                                @NotNull MappingAnnotation mappingAnnotation) {
       inspectUnnecessaryWhitespaces( problemsHolder, mappingAnnotation.getExpressionProperty() );
       inspectUnnecessaryWhitespaces( problemsHolder, mappingAnnotation.getDefaultExpressionProperty() );
       inspectUnnecessaryWhitespaces( problemsHolder, mappingAnnotation.getConditionExpression() );
    }

    private void inspectUnnecessaryWhitespaces(@NotNull ProblemsHolder problemsHolder, PsiNameValuePair property) {
        if ( property == null ) {
            return;
        }
        PsiAnnotationMemberValue value = property.getValue();
        if ( value == null ) {
            return;
        }
        String text = value.getText();
        if ( !JAVA_EXPRESSION.matcher( text ).matches() ) {
            return;
        }
        if ( text.charAt( 1 ) == '"') {
            // Text-Block
            return;
        }
        if ( text.indexOf( "java(" ) > 1 ) {
            problemsHolder.registerProblem( property,
                    MapStructBundle.message( "inspection.java.expression.unnecessary.whitespace",
                            "before", property.getAttributeName() ),
                    ProblemHighlightType.WEAK_WARNING, new RemoveWhitespacesBefore(property) );
        }
        if ( text.lastIndexOf( ')' ) < text.length() - 2) {
            problemsHolder.registerProblem( property,
                    MapStructBundle.message( "inspection.java.expression.unnecessary.whitespace",
                    "after", property.getAttributeName() ),
                    ProblemHighlightType.WEAK_WARNING, new RemoveWhitespacesAfter(property) );
        }
    }

    private static class RemoveWhitespacesBefore extends LocalQuickFixOnPsiElement {

        private final String name;

        private RemoveWhitespacesBefore(@NotNull PsiNameValuePair element) {
            super( element );
            this.name = element.getName();
        }

        @Override
        public @IntentionName @NotNull String getText() {
            return MapStructBundle.message( "inspection.java.expression.remove.unnecessary.whitespace",
                    "before", name );
        }

        @Override
        public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull PsiElement psiElement,
                           @NotNull PsiElement psiElement1) {
            if (psiElement instanceof PsiNameValuePair psiNameValuePair) {
                PsiAnnotationMemberValue value = psiNameValuePair.getValue();
                if (value != null) {
                    String text = value.getText();
                    psiNameValuePair.setValue( getInstance( project )
                            .createExpressionFromText( "\"" + text.substring( text.indexOf( "java(" ) ), value ) );
                }
            }
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return MapStructBundle.message( "intention.java.expression.remove.unnecessary.whitespace" );
        }

        @Override
        public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement,
                                   @NotNull PsiElement endElement) {
            if ( !super.isAvailable( project, file, startElement, endElement ) ) {
                return false;
            }
            if ( !(startElement instanceof PsiNameValuePair startPsiNameValuePair ) ) {
                return false;
            }
            return startPsiNameValuePair.getValue() != null;
        }
    }

    private static class RemoveWhitespacesAfter extends LocalQuickFixOnPsiElement {

        private final String name;

        private RemoveWhitespacesAfter(@NotNull PsiNameValuePair element) {
            super( element );
            this.name = element.getName();
        }

        @Override
        public @IntentionName @NotNull String getText() {
            return MapStructBundle.message( "inspection.java.expression.remove.unnecessary.whitespace", "after", name );
        }

        @Override
        public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull PsiElement psiElement,
                           @NotNull PsiElement psiElement1) {
            if (psiElement instanceof PsiNameValuePair psiNameValuePair) {
                PsiAnnotationMemberValue value = psiNameValuePair.getValue();
                if (value != null) {
                    String text = value.getText();
                    psiNameValuePair.setValue( getInstance( project ).createExpressionFromText(
                            text.substring( 0, text.lastIndexOf( ')' ) + 1 ) + "\"", value ) );
                }
            }
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return MapStructBundle.message( "intention.java.expression.remove.unnecessary.whitespace" );
        }

        @Override
        public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement,
                                   @NotNull PsiElement endElement) {
            if ( !super.isAvailable( project, file, startElement, endElement ) ) {
                return false;
            }
            if ( !(startElement instanceof PsiNameValuePair startPsiNameValuePair ) ) {
                return false;
            }
            return startPsiNameValuePair.getValue() != null;
        }
    }
}
