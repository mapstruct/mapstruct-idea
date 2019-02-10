/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base Reference for {@link org.mapstruct.ValueMapping}(s).
 *
 * @author Filip Hrisafov
 */
abstract class BaseValueMappingReference extends BaseReference {

    /**
     * @param element the element for which a reference should be found
     */
    BaseValueMappingReference(@NotNull PsiLiteral element) {
        super( element );
    }

    @Nullable
    @Override
    public final PsiElement resolve() {
        String value = getValue();
        if ( value.isEmpty() ) {
            return null;
        }

        PsiMethod mappingMethod = getMappingMethod();
        if ( isNotValueMapping( mappingMethod ) ) {
            return null;
        }

        return resolveInternal( value, mappingMethod );
    }

    @Nullable
    abstract PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod);

    @NotNull
    @Override
    public final Object[] getVariants() {
        PsiMethod mappingMethod = getMappingMethod();
        if ( isNotValueMapping( mappingMethod ) ) {
            return LookupElement.EMPTY_ARRAY;
        }
        return getVariantsInternal( mappingMethod );
    }

    @NotNull
    abstract Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod);

    private static boolean isNotValueMapping(@Nullable PsiMethod mappingMethod) {
        return mappingMethod == null || mappingMethod.getParameterList().getParametersCount() != 1;
    }
}
