/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

import java.util.ArrayList;
import java.util.List;

import org.example.dto.PersonDtoRecord;

public record CarDtoRecord(String make, int seatCount, String manufacturingYear, PersonDtoRecord myDriver,
        List<PersonDto> passengers, Long price, String category, boolean available) {
}
