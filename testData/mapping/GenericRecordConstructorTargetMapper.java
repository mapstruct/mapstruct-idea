/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GenericRecordConstructorTargetMapper {
    @Mapping(target = "car.<caret>winCode", source = "winCode")
    CarWrapperTarget<Car> toCarTarget(CarSource source);
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

record Car(String winCode) {
}

record CarWrapperTarget<T>(T car) {
}
