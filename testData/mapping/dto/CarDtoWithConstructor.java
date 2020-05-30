/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

import java.util.ArrayList;
import java.util.List;

import org.example.dto.PersonDtoWithConstructor;

public class CarDtoWithConstructor {

    private final String make;
    private final int seatCount;
    private final String manufacturingYear;
    private final PersonDtoWithConstructor myDriver;
    private final List<PersonDtoWithConstructor> passengers;
    private final Long price;
    private final String category;
    private final boolean available;

    public CarDtoWithConstructor(String make, int seatCount, String manufacturingYear, PersonDtoWithConstructor myDriver,
        List<PersonDtoWithConstructor> passengers, Long price, String category, boolean available) {
        this.make = make;
        this.seatCount = seatCount;
        this.manufacturingYear = manufacturingYear;
        this.myDriver = myDriver;
        this.passengers = passengers;
        this.price = price;
        this.category = category;
        this.available = available;
    }

    public String getMake() {
        return make;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public String getManufacturingYear() {
        return manufacturingYear;
    }

    public PersonDtoWithConstructor getMyDriver() {
        return myDriver;
    }

    public List<PersonDtoWithConstructor> getPassengers() {
        return passengers;
    }

    public Long getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    protected String getPrivateField() {
        return null;
    }

    public boolean isAvailable() {
        return available;
    }
}
