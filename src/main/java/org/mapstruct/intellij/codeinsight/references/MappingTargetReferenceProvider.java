/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import java.util.function.Function;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * {@link PsiReferenceProvider} for references in target / source properties of {@link org.mapstruct.Mapping}.
 *
 * @author Filip Hrisafov
 */
class MappingTargetReferenceProvider extends PsiReferenceProvider {

    private final Function<PsiLiteral, PsiReference[]> reference;

    /**
     * @param reference the function that can be used to create the references array
     */
    MappingTargetReferenceProvider(Function<PsiLiteral, PsiReference[]> reference) {
        this.reference = reference;
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return reference.apply( (PsiLiteral) element );
    }
}
