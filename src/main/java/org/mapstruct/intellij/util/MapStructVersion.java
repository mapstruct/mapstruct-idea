/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

/**
 * @author Filip Hrisafov
 */
public enum MapStructVersion {

    V1_2_O( false, false ),
    V1_3_O( true, false ),
    V1_4_O( true, true );

    private final boolean builderSupported;
    private final boolean constructorSupported;

    MapStructVersion(boolean builderSupported, boolean constructorSupported) {
        this.builderSupported = builderSupported;
        this.constructorSupported = constructorSupported;
    }

    public boolean isBuilderSupported() {
        return builderSupported;
    }

    public boolean isConstructorSupported() {
        return constructorSupported;
    }
}
