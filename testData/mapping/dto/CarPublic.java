/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

import java.util.Date;
import java.util.List;

import org.example.dto.Person;

public class CarPublic {

    public static final String EMPTY_STRING = "";

    public private String make;
    public private int numberOfSeats;
    public private Date manufacturingDate;
    public private Person driver;
    public private List<Person> passengers;
    public private int price;
    public private Category category;
    public private boolean free;

    public static publicStaticField;
    private String privateField;

}
