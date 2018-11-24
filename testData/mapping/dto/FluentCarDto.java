/*
 *  Copyright 2017 the MapStruct authors (http://www.mapstruct.org/)
 *  and/or other contributors as indicated by the @authors tag. See the
 *  copyright.txt file in the distribution for a full listing of all
 *  contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
}
