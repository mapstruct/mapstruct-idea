/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.data;

public class UnmappedSuperBuilderMultiLevelTargetPropertiesData {

    public static class BaseEntity {
        Long id;

        protected BaseEntity(BaseEntityBuilder<?, ?> b) {
            this.id = b.id;
        }

        public static BaseEntityBuilder<?, ?> builder() {
            return new BaseEntityBuilderImpl();
        }

        public static abstract class BaseEntityBuilder<C extends BaseEntity, B extends BaseEntityBuilder<C, B>> {
            private Long id;

            public B id(Long id) {
                this.id = id;
                return self();
            }

            protected abstract B self();

            public abstract C build();

            public String toString() {
                return "BaseEntity.BaseEntityBuilder(id=" + this.id + ")";
            }
        }

        private static final class BaseEntityBuilderImpl extends BaseEntityBuilder<BaseEntity, BaseEntityBuilderImpl> {
            private BaseEntityBuilderImpl() {
            }

            protected BaseEntityBuilderImpl self() {
                return this;
            }

            public BaseEntity build() {
                return new BaseEntity( this );
            }
        }
    }

    public static class NamedEntity extends BaseEntity {
        String name;

        protected NamedEntity(NamedEntityBuilder<?, ?> b) {
            super( b );
            this.name = b.name;
        }

        public static NamedEntityBuilder<?, ?> builder() {
            return new NamedEntityBuilderImpl();
        }

        public static abstract class NamedEntityBuilder<C extends NamedEntity, B extends NamedEntityBuilder<C, B>>
            extends BaseEntityBuilder<C, B> {
            private String name;

            public B name(String name) {
                this.name = name;
                return self();
            }

            protected abstract B self();

            public abstract C build();

            public String toString() {
                return "NamedEntity.NamedEntityBuilder(super=" + super.toString() + ", name=" + this.name + ")";
            }
        }

        private static final class NamedEntityBuilderImpl extends NamedEntityBuilder<NamedEntity, NamedEntityBuilderImpl> {
            private NamedEntityBuilderImpl() {
            }

            protected NamedEntityBuilderImpl self() {
                return this;
            }

            public NamedEntity build() {
                return new NamedEntity( this );
            }
        }
    }

    public static class PersonEntity extends NamedEntity {
        int age;

        protected PersonEntity(PersonEntityBuilder<?, ?> b) {
            super( b );
            this.age = b.age;
        }

        public static PersonEntityBuilder<?, ?> builder() {
            return new PersonEntityBuilderImpl();
        }

        public static abstract class PersonEntityBuilder<C extends PersonEntity, B extends PersonEntityBuilder<C, B>>
            extends NamedEntityBuilder<C, B> {
            private int age;

            public B age(int age) {
                this.age = age;
                return self();
            }

            public C someIrrelevantMethod(int someParameter) {
                return null;
            }

            protected abstract B self();

            public abstract C build();

            public String toString() {
                return "PersonEntity.PersonEntityBuilder(super=" + super.toString() + ", age=" + this.age + ")";
            }
        }

        private static final class PersonEntityBuilderImpl
            extends PersonEntityBuilder<PersonEntity, PersonEntityBuilderImpl> {
            private PersonEntityBuilderImpl() {
            }

            protected PersonEntityBuilderImpl self() {
                return this;
            }

            public PersonEntity build() {
                return new PersonEntity( this );
            }
        }
    }

}
