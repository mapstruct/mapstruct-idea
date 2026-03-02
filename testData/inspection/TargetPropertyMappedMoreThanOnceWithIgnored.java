/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Ignored;
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

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
}

class Inner {

    private Target target;

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }
}

@Mapper
interface MappingAndIgnoredSameTargetMapper {

    @Mapping(target = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, source = "name")
    @Ignored(targets = { <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error> })
    Target map(Source source);
}

@Mapper
interface IgnoredAndIgnoredSameTargetMapper {

    @Ignored(targets = { <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error> })
    @Ignored(targets = { <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error> })
    Target map(Source source);
}

@Mapper
interface IgnoredWithPrefixAndMappingSameTargetMapper {

    @Mapping(target = <error descr="Target property 'target.testName' must not be mapped more than once.">"target.testName"</error>, source = "name")
    @Ignored(prefix = "target", targets = { <error descr="Target property 'target.testName' must not be mapped more than once.">"testName"</error> })
    Inner map(Source source);
}

@Mapper
interface IgnoredNoConflictMapper {

    @Mapping(target = "testName", source = "name")
    @Ignored(targets = { "other" })
    Target map(Source source);
}

@Mapper
interface IgnoredWithPrefixNoConflictMapper {

    @Mapping(target = "testName", source = "name")
    @Ignored(prefix = "target", targets = { "testName" })
    Inner map(Source source);
}

@Mapper
interface IgnoredSingleValueAndMappingMapper {

    @Mapping(target = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, source = "name")
    @Ignored(targets = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>)
    Target map(Source source);
}

@Mapper
interface MultipleTargetsInIgnoredMapper {

    @Mapping(target = <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, source = "name")
    @Ignored(targets = { <error descr="Target property 'testName' must not be mapped more than once.">"testName"</error>, "other" })
    Target map(Source source);
}
