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

    @Mapping(target = ".", source = "innerTarget")
    Target map(Source source);
}

@Mapper
interface SingleMappingsMapper {

    @Mappings({
            @Mapping(target = ".", source = "innerTarget")
            })
    Target map(Source source);
}

