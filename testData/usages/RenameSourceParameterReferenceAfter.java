/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.test.examples;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface SimpleMapper {

    @Mapping(source = "param.name", target = "testName")
    Target map(Source param<caret>);

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

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }
    }
}
