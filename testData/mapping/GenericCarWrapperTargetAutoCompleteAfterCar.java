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
public interface GenericCarWrapperTargetMapper {

    @Mapping(source = "id", target = "wrapper.car.<caret>winCode")
    void update(@MappingTarget CarWrapper<Car> wrapper, CarEntity entity);
}

class CarEntity {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

class CarWrapper<T> {

    private T car;

    public T getCar() {
        return car;
    }

    public void setCar(T car) {
        this.car = car;
    }
}
