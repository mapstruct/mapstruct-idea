/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public abstract class BaseMapper {

    @Named("superClassModifierPackagePrivate")
    String superClassModifierPackagePrivate(String value) {
        return "";
    }

    @Named("superClassModifierPrivate")
    private String superClassModifierPrivate(String value) {
        return "";
    }

    @Named("superClassModifierProtected")
    protected String superClassModifierProtected(String value) {
        return "";
    }

    @Named("superClassModifierPublic")
    public String superClassModifierPublic(String value) {
        return "";
    }

}
