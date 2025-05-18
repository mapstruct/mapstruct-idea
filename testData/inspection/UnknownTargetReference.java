/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

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

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "<error descr="Unknown property 'name'">name</error>", source="name")
    Target map(Source source);
}

@Mapper
interface SingleMappingsMapper {

    @Mappings({
        @Mapping(target = "<error descr="Unknown property 'name'">name</error>", source="name")
    })
    Target map(Source source);
}

@Mapper
interface EmptyMappingMapper {

    @Mapping(target = <error descr="Unknown property ''">""</error>, source="name")
    Target map(Source source);
}
