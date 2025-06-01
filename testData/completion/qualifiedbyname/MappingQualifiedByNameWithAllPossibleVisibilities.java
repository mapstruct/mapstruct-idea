/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.example.dto.Car;
import org.example.dto.CarDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ap.test.complex.BaseMapper;
import org.mapstruct.ap.test.complex.SamePackageMapper;
import org.mapstruct.helper.qualifiedbyname.external.ExternalMapper;

@Mapper(uses = { ExternalMapper.class, SamePackageMapper.class })
public abstract class CarMapper extends BaseMapper {

    @Mapping(target = "make", qualifiedByName = "<caret>")
    public abstract CarDto carToCarDto(Car car);

    @Named("internalModifierPackagePrivate")
    String internalModifierPackagePrivate(String value) {
        return "";
    }

    @Named("internalModifierPrivate")
    private String internalModifierPrivate(String value) {
        return "";
    }

    @Named("internalModifierProtected")
    protected String internalModifierProtected(String value) {
        return "";
    }

    @Named("internalModifierPublic")
    public String internalModifierPublic(String value) {
        return "";
    }

}
