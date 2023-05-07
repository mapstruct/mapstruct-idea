/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.test.examples;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SimpleMapper {

    @Mapping(source = "name", target = "testName")
    Target map(Source source);

    class Source {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    class Target {

        private String testName;

        public Target(Target.Builder builder) {
            this.testName = builder.testName;
        }

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public static Target.Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private String testName;

            public String getTestName() {
                return testName;
            }

            public Target.Builder testName<caret>(String testName) {
                this.testName = testName;
                return this;
            }

            public Target build() {
                return new Target( this );
            }

        }
    }
}
