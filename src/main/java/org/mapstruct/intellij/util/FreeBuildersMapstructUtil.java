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
 * Mapstruct util for FreeBuilder.
 * FreeBuilder adds a lot of other methods that can be considered as fluent setters. Such as:
 * <ul>
 *     <li>{@code from(Target)}</li>
 *     <li>{@code mapXXX(UnaryOperator)}</li>
 *     <li>{@code mutateXXX(Consumer)}</li>
 *     <li>{@code mergeFrom(Target)}</li>
 *     <li>{@code mergeFrom(Target.Builder)}</li>
 * </ul>
 * <p>
 * When the JavaBean convention is not used with FreeBuilder then the getters are non-standard and MapStruct
 * won't recognize them. Therefore, one needs to use the JavaBean convention in which the fluent setters
 * start with {@code set}.
 */
public class FreeBuildersMapstructUtil extends MapstructUtil {

    static final MapstructUtil INSTANCE = new FreeBuildersMapstructUtil();

    /**
     * Hide constructor.
     */
    private FreeBuildersMapstructUtil() {
    }

    @Override
    public boolean isFluentSetter(@NotNull PsiMethod method, PsiType psiType, @NotNull PsiSubstitutor substitutor) {
        // When using FreeBuilder one needs to use the JavaBean convention,
        // which means that all setters will start with set
        return false;
    }
}
