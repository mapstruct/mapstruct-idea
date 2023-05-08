/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

import java.util.Date;
import java.util.List;

import org.example.dto.Person;

public record CarRecord(String make, int numberOfSeats, Date manufacturingDate, Person driver,
        List<Person> passengers, int price, Category category, boolean free) {
}
