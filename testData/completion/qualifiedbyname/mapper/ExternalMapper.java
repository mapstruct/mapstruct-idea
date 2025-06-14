/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.helper.qualifiedbyname.external;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public abstract class ExternalMapper {

    @Named("externalPackageModifierPackagePrivate")
    String externalPackageModifierPackagePrivate(String value) {
        return "";
    }

    @Named("externalPackageModifierPrivate")
    private String externalPackageModifierPrivate(String value) {
        return "";
    }

    @Named("externalPackageModifierProtected")
    protected String externalPackageModifierProtected(String value) {
        return "";
    }

    @Named("externalPackageModifierPublic")
    public String externalPackageModifierPublic(String value) {
        return "";
    }

}
