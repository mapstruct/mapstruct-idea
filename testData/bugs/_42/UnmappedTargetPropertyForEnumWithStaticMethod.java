/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;

@Mapper
public interface UnmappedTargetPropertyForEnumWithStaticMethod {

    CheeseType map(OtherCheeseType cheese);

    enum CheeseType {
        BRIE,
        ROQUEFORT;

        public String getValue() {
            return null;
        }

        public static CheeseType fromString(String value) {
            return null;
        }
    }

    enum OtherCheeseType {
        BRIE,
        ROQUEFORT
    }
}
