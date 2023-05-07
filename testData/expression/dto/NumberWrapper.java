/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto;

public class NumberWrapper<T extends Number> {

    private final T value;

    public NumberWrapper(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

}
