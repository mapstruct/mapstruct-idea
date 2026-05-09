/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

import java.util.ArrayList;
import java.util.List;

import org.example.dto.PersonDto;

public class CarDto {

    private String make;
    private int seatCount;
    private String manufacturingYear;
    private PersonDto myDriver;
    private List<PersonDto> passengers;
    private Long price;
    private String category;
    private boolean available;

    public CarDto() {
    }

    public CarDto(String make, int seatCount, String manufacturingYear, PersonDto myDriver, List<PersonDto> passengers) {
        this.make = make;
        this.seatCount = seatCount;
        this.manufacturingYear = manufacturingYear;
        this.myDriver = myDriver;
        this.passengers = passengers;
    }

    public static CarDto createDefault() {
        return new CarDto();
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    public String getManufacturingYear() {
        return manufacturingYear;
    }

    public void setManufacturingYear(String manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

    public PersonDto getMyDriver() {
        return myDriver;
    }

    public void setMyDriver(PersonDto myDriver) {
        this.myDriver = myDriver;
    }

    public List<PersonDto> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PersonDto> passengers) {
        this.passengers = passengers;
    }

    public void addPassenger(PersonDto passenger) {
        if ( this.passengers == null ) {
            this.passengers = new ArrayList<>();
        }
        this.passengers.add( passenger );
    }

    public void removePassenger(PersonDTO passenger) {
        if ( this.passengers != null ) {
            this.passengers.remove( passenger );
        }
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    protected void setPrivateField(String privateField) {
        // nothing to do
    }

    protected String getPrivateField() {
        return null;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public static void setStaticField(String staticField) {
        // nothing to do
    }

    public static String getStaticField() {
        return null;
    }
}
