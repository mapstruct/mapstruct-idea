/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TestMapper {

    @Mapping(target = "<caret>myMap")
    Target carToCarDto(Object value);

    public class Target {
        public Map<String, String> getMyMap() {
            return null;
        }

        public String getName() {
            return null;
        }

        public Object getValue() {
            return null;
        }
    }
}
