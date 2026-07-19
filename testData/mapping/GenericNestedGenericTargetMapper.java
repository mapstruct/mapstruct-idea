/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GenericNestedGenericTargetMapper {

    @Mapping(target = "wrapper.car.<caret>winCode", source = "winCode")
    OuterWrapper<CarWrapper<Car>> toTarget(CarSource source);
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

class CarWrapper<T> {

    private final T car;

    public CarWrapper(T car) {
        this.car = car;
    }

    public T getCar() {
        return car;
    }
}

class OuterWrapper<T> {

    private final T wrapper;

    public OuterWrapper(T wrapper) {
        this.wrapper = wrapper;
    }

    public T getWrapper() {
        return wrapper;
    }
}
