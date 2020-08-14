/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

/**
 * Creator of {@link MapstructBaseReference}.
 *
 * @param <T> the type of the reference that would be created
 *
 * @author Filip Hrisafov
 */
@FunctionalInterface
interface ReferenceCreator<T extends MapstructBaseReference> {

    /**
     * Create a new reference from the provided parameters.
     *
     * @param psiElement the element that the reference belongs to
     * @param previousReference the previous reference if there is one (in nested properties for example)
     * @param rangeInElement the range that the reference represent in the {@code psiLiteral}
     *
     * @return a new reference created from the provided parameters
     */
    T create(PsiElement psiElement, T previousReference, TextRange rangeInElement);
}
