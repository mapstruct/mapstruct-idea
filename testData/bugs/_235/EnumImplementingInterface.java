/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;

interface DbEnum<T> {
    T getDbValue();
}

@Mapper
abstract class MyMapper {

    enum CheeseType implements DbEnum<Integer> {
        BRIE(1),
        ROQUEFORT(2);

        private final Integer dbValue;

        CheeseType(Integer dbValue) {
            this.dbValue = dbValue;
        }

        @Override
        public Integer getDbValue() {
            return dbValue;
        }
    }

    public enum OtherCheeseType {
        BRIE,
        ROQUEFORT
    }

    public abstract CheeseType map(OtherCheeseType cheese);
}