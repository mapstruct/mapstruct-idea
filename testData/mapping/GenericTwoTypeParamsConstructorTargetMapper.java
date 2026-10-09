/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface GenericTwoTypeParamsConstructorTargetMapper {

    @Mapping(target = "car.winCode", source = "winCode")
    @Mapping(target = "engine.<caret>serial", source = "serial")
    PairWrapperTarget<Car, Engine> toTarget(CarEngineSource source);
}

class CarEngineSource {

    private String winCode;
    private String serial;

    public String getWinCode() {
        return winCode;
    }

    public void setWinCode(String winCode) {
        this.winCode = winCode;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
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

class Engine {

    private String serial;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}

class PairWrapperTarget<A, B> {

    private final A car;
    private final B engine;

    public PairWrapperTarget(A car, B engine) {
        this.car = car;
        this.engine = engine;
    }

    public A getCar() {
        return car;
    }

    public B getEngine() {
        return engine;
    }
}
