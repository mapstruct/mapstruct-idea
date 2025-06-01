/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;

/**
 * Mapstruct util for Immutables.
 * The generated Immutables also have a from that works as a copy. Our default strategy considers this method
 * as a setter with a name {@code from}. Therefore, we are ignoring it.
 */
public class ImmutablesMapstructUtil extends MapstructUtil {

    static final MapstructUtil INSTANCE = new ImmutablesMapstructUtil();

    /**
     * Hide constructor.
     */
    private ImmutablesMapstructUtil() {
    }

    @Override
    public boolean isFluentSetter(@NotNull PsiMethod method, PsiType psiType, @NotNull PsiSubstitutor substitutor) {
        return super.isFluentSetter( method, psiType, substitutor ) && !method.getName().equals( "from" );
    }
}
