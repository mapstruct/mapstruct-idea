/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
@Mapping(source = "<caret>numberOfSeats", target = "seatCount"),
public interface CarMapper {
}
