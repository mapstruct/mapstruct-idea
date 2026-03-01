/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.mapstruct.Ignored;
import org.mapstruct.Mapper;
import org.example.dto.Garage;
import org.example.dto.GarageDto;

@Mapper
public interface IgnoredTargetsWithPrefix {

    @Ignored(prefix = "car", targets = { "<caret>" })
    GarageDto garageToGarageDto(Garage garage);
}
