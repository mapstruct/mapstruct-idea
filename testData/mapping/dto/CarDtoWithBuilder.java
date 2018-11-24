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

import org.example.dto.PersonDtoWithBuilder;

public class CarDtoWithBuilder {

    private String make;
    private int seatCount;
    private String manufacturingYear;
    private PersonDtoWithBuilder myDriver;
    private List<PersonDtoWithBuilder> passengers;
    private Long price;
    private String category;
    private boolean available;

    public CarDtoWithBuilder(CarDtoWithBuilder.Builder builder) {
        this.make = builder.make;
        this.seatCount = builder.seatCount;
        this.manufacturingYear = builder.manufacturingYear;
        this.myDriver = builder.myDriver;
        this.passengers = builder.passengers;
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

    public PersonDtoWithBuilder getMyDriver() {
        return myDriver;
    }

    public List<PersonDtoWithBuilder> getPassengers() {
        return passengers;
    }

    public Long getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    protected String getPrivateField() {
        return null;
    }

    public boolean isAvailable() {
        return available;
    }

    public static CarDtoWithBuilder.Builder builder() {
        return new Builder();
    }

    public static void dummyStaticMethod() {

    }

    public static CarDtoWithBuilder create() {
        return null;
    }

    public static class Builder {
        private String make;
        private int seatCount;
        private String manufacturingYear;
        private PersonDtoWithBuilder myDriver;
        private List<PersonDtoWithBuilder> passengers;
        private Long price;
        private String category;
        private boolean available;

        public String getMake() {
            return make;
        }

        public Builder make(String make) {
            this.make = make;
            return this;
        }

        public int getSeatCount() {
            return seatCount;
        }

        public Builder seatCount(int seatCount) {
            this.seatCount = seatCount;
            return this;
        }

        public String getManufacturingYear() {
            return manufacturingYear;
        }

        public Builder manufacturingYear(String manufacturingYear) {
            this.manufacturingYear = manufacturingYear;
            return this;
        }

        public PersonDtoWithBuilder getMyDriver() {
            return myDriver;
        }

        public Builder myDriver(PersonDtoWithBuilder myDriver) {
            this.myDriver = myDriver;
            return this;
        }

        public List<PersonDtoWithBuilder> getPassengers() {
            return passengers;
        }

        public Builder passengers(List<PersonDtoWithBuilder> passengers) {
            this.passengers = passengers;
            return this;
        }

        public Builder addPassenger(PersonDtoWithBuilder passenger) {
            if ( this.passengers == null ) {
                this.passengers = new ArrayList<>();
            }
            this.passengers.add( passenger );
            return this;
        }

        public Long getPrice() {
            return price;
        }

        public Builder price(Long price) {
            this.price = price;
            return this;
        }

        public String getCategory() {
            return category;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public boolean isAvailable() {
            return available;
        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        protected CarDtoWithBuilder nonPublicCreate() {
            return new CarDtoWithBuilder( this );
        }

        public CarDtoWithBuilder create() {
            return new CarDtoWithBuilder( this );
        }
    }
}
