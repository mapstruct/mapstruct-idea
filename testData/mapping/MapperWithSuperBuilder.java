/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class CarMapper {

    @Mapping(target = "<caret>baseValue")
    Target map(String source);

    static class Base {

    }

    static class Target extends Base {

        public static TargetBuilder<?, ?> builder() {
            return null;
        }
    }

    public static abstract class BaseBuilder<C extends Base, B extends BaseBuilder<C, B>> {

        protected String baseValue;

        public B baseValue(String baseValue) {
            this.baseValue = baseValue;
            return self();
        }

        protected abstract B self();

        public abstract C build();
    }

    public static abstract class TargetBuilder<C extends Target, B extends TargetBuilder<C, B>> extends BaseBuilder<C, B> {

        protected String value;

        public B value(String value) {
            this.value = value;
            return self();
        }
    }

    private static final class TargetBuilderImpl extends TargetBuilder<Target, TargetBuilderImpl> {

        @Override
        protected TargetBuilderImpl self() {
            return this;
        }

        @Override
        public Target build() {
            return new Target();
        }
    }

}
