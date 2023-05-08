/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import com.intellij.psi.PsiType;

/**
 * @author Filip Hrisafov
 */
public class TargetType {

    private final PsiType type;
    private final boolean builder;

    private TargetType(PsiType type, boolean builder) {
        this.type = type;
        this.builder = builder;
    }

    public PsiType type() {
        return type;
    }

    public boolean builder() {
        return builder;
    }

    public static TargetType builder(PsiType type) {
        return new TargetType( type, true );
    }

    public static TargetType defaultType(PsiType type) {
        return new TargetType( type, false );
    }
}
