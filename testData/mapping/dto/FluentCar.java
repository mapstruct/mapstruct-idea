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

import java.util.Date;
import java.util.List;

import org.example.dto.Person;

public class FluentCar {

    private String make;
    private int numberOfSeats;
    private Date manufacturingDate;
    private Person driver;
    private List<Person> passengers;
    private int price;
    private Category category;
    private boolean free;

    public String getMake() {
        return make;
    }

    public FluentCar make(String make) {
        this.make = make;
        return this;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public FluentCar numberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
        return this;
    }

    public Date getManufacturingDate() {
        return manufacturingDate;
    }

    public FluentCar manufacturingDate(Date manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public Person getDriver() {
        return driver;
    }

    public FluentCar driver(Person driver) {
        this.driver = driver;
        return this;
    }

    public List<Person> getPassengers() {
        return passengers;
    }

    public FluentCar passengers(List<Person> passengers) {
        this.passengers = passengers;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public FluentCar price(int price) {
        this.price = price;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public FluentCar category(Category category) {
        this.category = category;
        return this;
    }

    public boolean isFree() {
        return free;
    }

    public FluentCar free(boolean free) {
        this.free = free;
        return this;
    }
}
