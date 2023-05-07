/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

import java.util.ArrayList;
import java.util.List;

import org.example.dto.PersonDto;

public class CarDtoPublic {

    public static final String EMPTY_STRING = "";

    public String make;
    public int seatCount;
    public String manufacturingYear;
    public PersonDto myDriver;
    public List<PersonDto> passengers;
    public Long price;
    public String category;
    public boolean available;
    public static publicStaticField;
    private String privateField;
}
