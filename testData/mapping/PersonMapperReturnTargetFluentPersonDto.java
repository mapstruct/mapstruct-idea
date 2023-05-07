/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.FluentPersonDto;
import org.example.dto.Person;

@Mapper
public interface PersonMapper {

    @Mapping(target = "<caret>name")
    FluentPersonDto personToPersonDto(Person person);
}
