/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.test.examples;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SimpleMapper {

    @Mapping(source = "name", target = "newName<caret>")
    Target map(Source source);

    record Source(String name) {}

    record Target(String newName) {}
}
