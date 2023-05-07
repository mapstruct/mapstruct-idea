/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.example.dto.*


@Mapper
interface CarMapper {

    @Mappings(
        Mapping(source = "numberOfSeats", target = "<caret>seatCount"),
        Mapping(source = "manufacturingDate", target = "manufacturingYear")
    )
    fun carToCarDto(car: Car): CarDtoKt
}
