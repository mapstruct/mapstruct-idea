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
 * Inspection that checks if inside a @Mapping at least one source property is defined
 *
 * @author hduelme
 */
public class NoSourcePropertyDefinedInspection extends MappingAnnotationInspectionBase {

    @Override
    void visitMappingAnnotation( @NotNull ProblemsHolder problemsHolder, @NotNull PsiAnnotation psiAnnotation,
                                 @NotNull MappingAnnotation mappingAnnotation ) {
        if (mappingAnnotation.hasNoSourceProperties()) {
            problemsHolder.registerProblem( psiAnnotation,
                    MapStructBundle.message( "inspection.no.source.property" ) );
        }
    }
}
