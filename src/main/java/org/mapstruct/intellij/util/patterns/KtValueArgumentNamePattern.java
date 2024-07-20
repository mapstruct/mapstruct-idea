/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util.patterns;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PropertyPatternCondition;
import com.intellij.patterns.PsiElementPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.KtValueArgumentName;

/**
 * @author Filip Hrisafov
 */
public class KtValueArgumentNamePattern extends PsiElementPattern<KtValueArgumentName, KtValueArgumentNamePattern> {

    static final KtValueArgumentNamePattern KT_VALUE_ARGUMENT_PATTERN = new KtValueArgumentNamePattern();

    private KtValueArgumentNamePattern() {
        super( KtValueArgumentName.class );
    }

    @NotNull
    @Override
    public KtValueArgumentNamePattern withName(@NotNull ElementPattern<String> name) {
        return with( new PropertyPatternCondition<KtValueArgumentName, String>( "withKtValueName", name ) {
            @Nullable
            @Override
            public String getPropertyValue(@NotNull Object o) {
                if ( o instanceof KtValueArgumentName ktValueArgumentName ) {
                    return ktValueArgumentName.getAsName().getIdentifier();
                }
                return null;
            }
        } );
    }
}
