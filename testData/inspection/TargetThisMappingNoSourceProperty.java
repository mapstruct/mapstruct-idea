/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

class Source {

    private Target innerTarget;

    public Target getInnerTarget() {
        return innerTarget;
    }

    public void setInnerTarget(Target innerTarget) {
        this.innerTarget = innerTarget;
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

    <error descr="Using @Mapping( target = \".\") requires a source property. Expression or constant cannot be used as a source">@Mapping(target = ".", expression = "java(source.getInnerTarget())")</error>
    Target map(Source source);
}

@Mapper
interface SingleMappingsMapper {

    @Mappings({
            <error descr="Using @Mapping( target = \".\") requires a source property. Expression or constant cannot be used as a source">@Mapping(target = ".", expression = "java(source.getInnerTarget())")</error>
            })
    Target map(Source source);
}

