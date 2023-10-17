/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiAnnotation;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;

/**
 * Inspection that checks if inside a @Mapping annotation more than one default source property is defined
 *
 * @author hduelme
 */
public class MoreThanOneDefaultSourcePropertyDefinedInspection extends MappingAnnotationInspectionBase {

    @Override
    void visitMappingAnnotation( @NotNull ProblemsHolder problemsHolder, @NotNull PsiAnnotation psiAnnotation,
                                 @NotNull MappingAnnotation mappingAnnotation ) {
        // only apply if source property is defined
        if (mappingAnnotation.getSourceProperty() != null && mappingAnnotation.getDefaultValueProperty() != null
                && mappingAnnotation.getDefaultExpressionProperty() != null) {
            String family = MapStructBundle.message( "intention.more.than.one.default.source.property" );
            problemsHolder.registerProblem( psiAnnotation,
                    MapStructBundle.message( "inspection.more.than.one.default.source.property" ),
                    createRemoveAnnotationAttributeQuickFix( mappingAnnotation.getDefaultValueProperty(),
                            "Remove default value", family ),
                    createRemoveAnnotationAttributeQuickFix(  mappingAnnotation.getDefaultExpressionProperty(),
                            "Remove default expression", family ) );

        }
    }

}
