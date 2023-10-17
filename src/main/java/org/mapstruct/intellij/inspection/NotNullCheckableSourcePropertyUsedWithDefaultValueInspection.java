/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotNullCheckableSourcePropertyUsedWithDefaultValueInspection extends MappingAnnotationInspectionBase {

    @Override
    void visitMappingAnnotation( @NotNull ProblemsHolder problemsHolder, @NotNull PsiAnnotation psiAnnotation,
                                 @NotNull MappingAnnotation mappingAnnotation ) {
        // only apply if only one source is used, the user should decide first
        if (mappingAnnotation.getSourceProperty() == null) {
            if (mappingAnnotation.getConstantProperty() != null && mappingAnnotation.getExpressionProperty() == null) {
                checkForNotNullCheckableSource( mappingAnnotation, problemsHolder, psiAnnotation, "Constant" );
            }
            else if (mappingAnnotation.getConstantProperty() == null
                    && mappingAnnotation.getExpressionProperty() != null) {
                checkForNotNullCheckableSource( mappingAnnotation, problemsHolder, psiAnnotation, "Expression" );
            }
        }
    }

    private static void checkForNotNullCheckableSource( @NotNull MappingAnnotation mappingAnnotation,
                                                        @NotNull ProblemsHolder problemsHolder,
                                                        @NotNull PsiAnnotation psiAnnotation,
                                                        @NotNull String propertyName ) {
        List<PsiNameValuePair> defaultSources = new ArrayList<>( 2 );
        if (mappingAnnotation.getDefaultExpressionProperty() != null) {
            defaultSources.add( mappingAnnotation.getDefaultExpressionProperty() );
        }
        if (mappingAnnotation.getDefaultValueProperty() != null) {
            defaultSources.add( mappingAnnotation.getDefaultValueProperty() );
        }
        if (!defaultSources.isEmpty()) {
            List<LocalQuickFix> quickFixes = new ArrayList<>(defaultSources.size());
            String family = MapStructBundle.message(
                    "intention.not.null.checkable.property.source.used.with.default.property" );
            for (PsiNameValuePair sources : defaultSources) {
                quickFixes.add( createRemoveAnnotationAttributeQuickFix( sources,
                        "Remove " + sources.getAttributeName(), family ) );
            }
            problemsHolder.registerProblem( psiAnnotation, MapStructBundle.message(
                    "inspection.not.null.checkable.property.source.used.with.default.property",
                            propertyName, defaultSources.stream().map( PsiNameValuePair::getAttributeName )
                                    .collect( Collectors.joining( " and " ) ) ),
                    quickFixes.toArray( new LocalQuickFix[]{} ) );
        }
    }
}
