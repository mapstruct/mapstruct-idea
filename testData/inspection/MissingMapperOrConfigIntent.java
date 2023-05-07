/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

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

interface <error descr="@Mapper or @MapperConfig annotation missing">InterfaceWithMappingAnnotations</error> {

    @org.mapstruct.Mapping(source = "name", target = "testName")
    Target map(Source source);
}

abstract class <error descr="@Mapper or @MapperConfig annotation missing">ClassWithMappingAnnotations</error> {

    @org.mapstruct.Mapping(source = "name", target = "testName")
    abstract Target map(Source source);
}
