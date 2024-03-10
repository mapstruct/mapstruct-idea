/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiAnnotation;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;

import java.util.ArrayList;

/**
 * Inspection that checks if inside a @Mapping annotation more than one source property is defined
 *
 * @author hduelme
 */
public class MoreThanOneSourcePropertyDefinedInspection extends MappingAnnotationInspectionBase {

    @Override
    void visitMappingAnnotation( @NotNull ProblemsHolder problemsHolder, @NotNull PsiAnnotation psiAnnotation,
                                 @NotNull MappingAnnotation mappingAnnotation ) {
        if ((mappingAnnotation.getConstantProperty() != null && mappingAnnotation.getSourceProperty() != null)
            || (mappingAnnotation.getConstantProperty() != null && mappingAnnotation.getExpressionProperty() != null)
            || (mappingAnnotation.getSourceProperty() != null && mappingAnnotation.getExpressionProperty() != null)) {
            ArrayList<LocalQuickFix> quickFixes = new ArrayList<>( 5 );
            String family = MapStructBundle.message( "intention.more.than.one.source.property" );
            if (mappingAnnotation.getSourceProperty() != null && mappingAnnotation.isNotThisTarget()) {
                quickFixes.add( createRemoveAnnotationAttributeQuickFix( mappingAnnotation.getSourceProperty(),
                        "Remove source value", family ) );
            }
            if (mappingAnnotation.getConstantProperty() != null) {
                quickFixes.add( createRemoveAnnotationAttributeQuickFix( mappingAnnotation.getConstantProperty(),
                        "Remove constant value", family ) );

                if (mappingAnnotation.hasNoDefaultProperties() && mappingAnnotation.getSourceProperty() != null
                        && mappingAnnotation.isNotThisTarget() ) {
                    quickFixes.add( createReplaceAsDefaultValueQuickFix(
                            mappingAnnotation.getConstantProperty(), "constant", "defaultValue",
                            "Use constant value as default value", family ) );
                }
            }
            if (mappingAnnotation.getExpressionProperty() != null) {
                quickFixes.add( createRemoveAnnotationAttributeQuickFix( mappingAnnotation.getExpressionProperty(),
                        "Remove expression", family ) );
                if (mappingAnnotation.hasNoDefaultProperties() && mappingAnnotation.getSourceProperty() != null
                        && mappingAnnotation.isNotThisTarget() ) {
                    quickFixes.add( createReplaceAsDefaultValueQuickFix(
                            mappingAnnotation.getExpressionProperty(), "expression", "defaultExpression",
                            "Use expression as default expression", family ) );
                }
            }

            problemsHolder.registerProblem( psiAnnotation,
                    MapStructBundle.message( "inspection.more.than.one.source.property" ),
                    quickFixes.toArray( new LocalQuickFix[]{} ) );
        }
    }

}
