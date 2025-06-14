/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public abstract class SamePackageMapper {

    @Named("samePackageModifierPackagePrivate")
    String samePackageModifierPackagePrivate(String value) {
        return "";
    }

    @Named("samePackageModifierPrivate")
    private String samePackageModifierPrivate(String value) {
        return "";
    }

    @Named("samePackageModifierProtected")
    protected String samePackageModifierProtected(String value) {
        return "";
    }

    @Named("samePackageModifierPublic")
    public String samePackageModifierPublic(String value) {
        return "";
    }

}
