/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.MapStructBundle;

/**
 * @author Filip Hrisafov
 */
abstract class BaseMappingReference extends MapstructBaseReference {

    BaseMappingReference(@NotNull PsiElement element, @Nullable MapstructBaseReference previousReference,
                         TextRange rangeInElement, String value) {
        super( element, previousReference, rangeInElement, value );
    }

    @NotNull
    @Override
    public String getUnresolvedMessagePattern() {
        //noinspection UnresolvedPropertyKey
        return MapStructBundle.message( "unknown.property" );
    }
}
