/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import java.util.List;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;

@Mapper(config = CarMapper.MapperConfigDisabledBuilder.class, builder = @Builder(disableBuilder = false))
public interface CarMapper {

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

    @MapperConfig(builder = @Builder(disableBuilder = true))
    interface MapperConfigDisabledBuilder {}

}
