/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

import java.util.ArrayList;
import java.util.List;

import org.example.dto.Default;

public class CarDtoWithMultipleConstructorsAndAnnotatedWithDefault {

    private String make;
    private int seatCount;
    private String manufacturingYear;
    private Long price;

    public CarDtoWithMultipleConstructorsAndAnnotatedWithDefault(String make, int seatCount) {
    }

    @Default
    public CarDtoWithMultipleConstructorsAndAnnotatedWithDefault(String make, int seatCount, String manufacturingYear,
        Long price) {
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

    public Long getPrice() {
        return price;
    }
}
