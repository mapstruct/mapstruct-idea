/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

import java.util.ArrayList;
import java.util.List;

import org.example.dto.PersonDto;

public class FluentCarDto {

    private String make;
    private int seatCount;
    private String manufacturingYear;
    private PersonDto myDriver;
    private List<PersonDto> passengers;
    private Long price;
    private String category;
    private boolean available;

    public String getMake() {
        return make;
    }

    public FluentCarDto make(String make) {
        this.make = make;
        return this;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public FluentCarDto seatCount(int seatCount) {
        this.seatCount = seatCount;
        return this;
    }

    public String getManufacturingYear() {
        return manufacturingYear;
    }

    public FluentCarDto manufacturingYear(String manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
        return this;
    }

    public PersonDto getMyDriver() {
        return myDriver;
    }

    public FluentCarDto myDriver(PersonDto myDriver) {
        this.myDriver = myDriver;
        return this;
    }

    public List<PersonDto> getPassengers() {
        return passengers;
    }

    public FluentCarDto passengers(List<PersonDto> passengers) {
        this.passengers = passengers;
        return this;
    }

    public FluentCarDto addPassenger(PersonDto passenger) {
        if ( this.passengers == null ) {
            this.passengers = new ArrayList<>();
        }

        this.passengers.add( passenger );
        return this;
    }

    public FluentCarDto removePassenger(PersonDto passenger) {
        if ( this.passengers != null ) {
            this.passengers.remove( passenger );
        }
        return this;
    }

    public Long getPrice() {
        return price;
    }

    public FluentCarDto price(Long price) {
        this.price = price;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public FluentCarDto category(String category) {
        this.category = category;
        return this;
    }

    protected FluentCarDto privateField(String privateField) {
        // nothing to do
        return this;
    }

    protected String getPrivateField() {
        return null;
    }

    public boolean isAvailable() {
        return available;
    }

    public FluentCarDto available(boolean available) {
        this.available = available;
        return this;
    }

    public Object getValue(String name) {
        return null;
    }
}
