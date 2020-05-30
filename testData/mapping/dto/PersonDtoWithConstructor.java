/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

public class PersonDtoWithConstructor {

    private final String name;

    public PersonDtoWithConstructor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
