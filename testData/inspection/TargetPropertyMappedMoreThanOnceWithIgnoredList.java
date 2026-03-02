/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Ignored;
import org.mapstruct.IgnoredList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    private String moreTarget;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getMoreTarget() {
        return moreTarget;
    }

    public void setMoreTarget(String moreTarget) {
        this.moreTarget = moreTarget;
    }
}

@Mapper
interface MappingAndIgnoredListSameTargetMapper {

    @Mapping(target = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, source = "name")
    @IgnoredList({
        @Ignored(targets = { <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error> })
    })
    Target map(Source source);
}

@Mapper
interface IgnoredListDuplicateTargetMapper {

    @IgnoredList({
        @Ignored(targets = { <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error> }),
        @Ignored(targets = { <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error> })
    })
    Target map(Source source);
}

@Mapper
interface IgnoredListNoConflictMapper {

    @IgnoredList({
        @Ignored(targets = { "testName" }),
        @Ignored(targets = { "moreTarget" })
    })
    Target map(Source source);
}
