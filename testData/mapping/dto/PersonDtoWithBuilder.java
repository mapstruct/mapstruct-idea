/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

public class PersonDtoWithBuilder {

    private String name;

    public PersonDtoWithBuilder(PersonDtoWithBuilder.Builder builder) {
        this.name = builder.name;
    }

    public String getName() {
        return name;
    }

    public static PersonDtoWithBuilder.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;

        public String getName() {
            return name;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public PersonDtoWithBuilder build() {
            return new PersonDtoWithBuilder( this );
        }
    }
}
