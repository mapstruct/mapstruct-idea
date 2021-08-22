/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CarMapper {

    @Mapping(target = "<caret>")
    Target map(String source);

    class Target {

        public static Builder builder() {
            return null;
        }

        public static class Builder {

            public Builder(String value) {

            }

            public Builder address(String address) {

            }

            public Builder city(String city) {

            }

            public Target build() {
                return null;
            }
        }
    }

}
