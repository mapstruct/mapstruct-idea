/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface GenericConstructorMappingTargetUpdateMapper {
    @Mapping(target = "car.<caret>winCode", source = "winCode")
    void update(@MappingTarget CarWrapperTarget<Car> target, CarSource source);
}

class CarSource {
    private String winCode;
    public String getWinCode() {
        return winCode;
    }
    public void setWinCode(String winCode) {
        this.winCode = winCode;
    }
}

class Car {
    private String winCode;
    public String getWinCode() {
        return winCode;
    }
    public void setWinCode(String winCode) {
        this.winCode = winCode;
    }
}

class CarWrapperTarget<T> {
    private final T car;
    public CarWrapperTarget(T car) {
        this.car = car;
    }
    public T getCar() {
        return car;
    }
}
