/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

/**
 * @author Filip Hrisafov
 */
public enum MapStructVersion {

    V1_2_O( false, false, false ),
    V1_3_O( true, false, false ),
    V1_4_O( true, true, false ),
    V1_7_O( true, true, true ),;

    private final boolean builderSupported;
    private final boolean constructorSupported;
    private final boolean ignoringRemovers;

    MapStructVersion(boolean builderSupported, boolean constructorSupported, boolean ignoringRemovers) {
        this.builderSupported = builderSupported;
        this.constructorSupported = constructorSupported;
        this.ignoringRemovers = ignoringRemovers;
    }

    public boolean isBuilderSupported() {
        return builderSupported;
    }

    public boolean isConstructorSupported() {
        return constructorSupported;
    }

    public boolean isIgnoringRemovers() {
        return ignoringRemovers;
    }
}
