/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A base reference to mapstruct annotations without nested types.
 *
 * @author Oliver Erhart
 */
public abstract class MapstructNonNestedBaseReference extends MapstructBaseReference {

    /**
     * Create a reference.
     *
     * @param element        the literal where the text is
     * @param previous       the previous reference ({@code null} if there is no previous reference)
     * @param rangeInElement the range in the {@code element} for which this reference is valid
     */
    MapstructNonNestedBaseReference(@NotNull PsiElement element,
                                    @Nullable MapstructBaseReference previous,
                                    TextRange rangeInElement, String value) {
        super( element, previous, rangeInElement, value );
    }

    @Override
    final PsiElement resolveInternal(@NotNull String value, @NotNull PsiType psiType) {
        return null; // not needed
    }

    @NotNull
    @Override
    final Object[] getVariantsInternal(@NotNull PsiType psiType) {
        return LookupElement.EMPTY_ARRAY; // not needed
    }

    @Override
    @Nullable
    final PsiType resolvedType() {
        return null; // not needed
    }
}
