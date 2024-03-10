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
 * @author hduelme
 */
public class TargetThisMappingNoSourcePropertyInspection extends MappingAnnotationInspectionBase {

    @Override
    void visitMappingAnnotation(@NotNull ProblemsHolder problemsHolder, @NotNull PsiAnnotation psiAnnotation,
                                @NotNull MappingAnnotation mappingAnnotation) {
        if ( mappingAnnotation.isNotThisTarget() || mappingAnnotation.getIgnoreProperty() != null)  {
            return;
        }
        if (mappingAnnotation.getSourceProperty() == null ) {
            problemsHolder.registerProblem( psiAnnotation,
                    MapStructBundle.message( "inspection.this.target.mapping.no.source.property" ) );
        }
    }
}
