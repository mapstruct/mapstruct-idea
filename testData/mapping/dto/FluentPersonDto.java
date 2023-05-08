/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

public class FluentPersonDto {

    private String name;
    private String address;

    public String getName() {
        return name;
    }

    public FluentPersonDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public FluentPersonDto address(String address) {
        this.address = address;
    }
}
