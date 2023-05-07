/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

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

@MapperConfig
interface InterfaceWithMappingAnnotations {

    @org.mapstruct.Mapping(source = "name", target = "testName")
    Target map(Source source);
}

@Mapper
abstract class ClassWithMappingAnnotations {

    @org.mapstruct.Mapping(source = "name", target = "testName")
    abstract Target map(Source source);
}
