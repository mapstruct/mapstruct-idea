/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

import java.util.ArrayList;
import java.util.List;

import org.example.dto.PersonDtoWithConstructor;

public class CarDtoWithConstructorAndSetters {

    private final String make;
    private final int seatCount;
    private final String manufacturingYear;
    private final PersonDtoWithConstructor myDriver;
    private final List<PersonDtoWithConstructor> passengers;
    private final Long price;
    private String category;
    private boolean available;

    public CarDtoWithConstructorAndSetters(String make, int seatCount, String manufacturingYear, PersonDtoWithConstructor myDriver,
        List<PersonDtoWithConstructor> passengers, Long price) {
        this.make = make;
        this.seatCount = seatCount;
        this.manufacturingYear = manufacturingYear;
        this.myDriver = myDriver;
        this.passengers = passengers;
        this.price = price;
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

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
