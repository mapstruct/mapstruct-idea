/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationParamListImpl;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.psi.PsiElementFactory.getInstance;
import static org.mapstruct.intellij.util.MapstructAnnotationUtils.getAnnotatedMethod;
import static org.mapstruct.intellij.util.MapstructUtil.getSourceParameters;

/**
 * @author hduelme
 */
public class ThisUsedAsSourcePropertyInspection extends MappingAnnotationInspectionBase {
    @Override
    void visitMappingAnnotation(@NotNull ProblemsHolder problemsHolder, @NotNull PsiAnnotation psiAnnotation,
                                @NotNull MappingAnnotation mappingAnnotation) {
        PsiNameValuePair sourceProperty = mappingAnnotation.getSourceProperty();
        if (sourceProperty == null || sourceProperty.getValue() == null) {
            return;
        }
        if ( !".".equals( sourceProperty.getLiteralValue() ) ) {
            return;
        }
        List<LocalQuickFix>  fixes = new ArrayList<>();
        PsiMethod annotatedMethod = getAnnotatedMethod( psiAnnotation );
        if (annotatedMethod != null) {
            for (PsiParameter sourceParameter : getSourceParameters( annotatedMethod )) {
                fixes.add( new ReplaceSourceParameterValueQuickFix(sourceProperty, sourceParameter.getName() ) );
            }
        }
        problemsHolder.registerProblem( sourceProperty.getValue(),
                MapStructBundle.message( "inspection.source.property.this.used" ),
                fixes.toArray( new LocalQuickFix[0] ) );
    }

    private static class ReplaceSourceParameterValueQuickFix extends LocalQuickFixOnPsiElement {

        private final String targetMethodeParameterName;
        private final String text;
        private final String family;

        private ReplaceSourceParameterValueQuickFix(@NotNull PsiNameValuePair element,
                                                    @NotNull String targetMethodeParameterName) {
            super( element );
            this.targetMethodeParameterName = targetMethodeParameterName;
            this.text = MapStructBundle.message( "intention.replace.source.property", targetMethodeParameterName );
            this.family = MapStructBundle.message( "inspection.source.property.this.used" );
        }

        @Override
        public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement,
                                   @NotNull PsiElement endElement ) {
            if ( !endElement.isValid() ) {
                return false;
            }
            PsiElement parent = endElement.getParent();
            return parent.isValid() && parent instanceof PsiAnnotationParamListImpl;
        }

        @Override
        public @IntentionName @NotNull String getText() {
            return  text;
        }

        @Override
        public void invoke( @NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement,
                            @NotNull PsiElement endElement ) {
            if (endElement instanceof PsiNameValuePair end) {
                PsiAnnotationParamListImpl parent = (PsiAnnotationParamListImpl) end.getParent();
                PsiElement parent1 = parent.getParent();

                // don't replace inside of strings. Only the constant value name
                String annotationText = parent1.getText().replaceFirst( "(?<!\")\\s*,?\\s*source\\s*=\\s*\"\\.\"",
                        "source = \"" + targetMethodeParameterName + "\"" );
                parent1.replace( getInstance( project ).createAnnotationFromText( annotationText, parent1 ) );
            }
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return family;
        }
    }
}
