/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

import org.example.dto.CarDto;
import org.example.dto.PersonDto;

public class GarageDto {

    private CarDto car;
    private PersonDto owner;

    public CarDto getCar() {
        return car;
    }

    public void setCar(CarDto car) {
        this.car = car;
    }

    public PersonDto getOwner() {
        return owner;
    }

    public void setOwner(PersonDto owner) {
        this.owner = owner;
    }
}
