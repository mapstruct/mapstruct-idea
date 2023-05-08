/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(builder = @Builder(disableBuilder = true))
public interface CarMapper {

    @BeanMapping(qualifiedByName = "test")
    @Mapping(target = "<caret>")
    Target map(String source);

    class Target {

        private String targetValue;

        public String getTargetValue() {
            return targetValue;
        }

        public void setTargetValue(String targetValue) {
            this.targetValue = targetValue;
        }

        public static Builder builder() {
            return null;
        }

        public static class Builder {

            public Builder builderValue(String address) {

            }

            public Target build() {
                return null;
            }
        }
    }

}
