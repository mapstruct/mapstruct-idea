/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util.patterns;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PsiElementPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtValueArgument;

/**
 * @author Filip Hrisafov
 */
public class KtValueArgumentPattern extends PsiElementPattern<KtValueArgument, KtValueArgumentPattern> {

    static final KtValueArgumentPattern KT_VALUE_ARGUMENT_PATTERN = new KtValueArgumentPattern();

    private KtValueArgumentPattern() {
        super( KtValueArgument.class );
    }

    @NotNull
    @Override
    public KtValueArgumentPattern withName(@NotNull ElementPattern<String> name) {
        return withChild( MapStructKotlinPatterns.ktValueArgumentName().withName( name ) );
    }
}
