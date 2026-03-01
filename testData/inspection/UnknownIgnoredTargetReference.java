/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Ignored;
import org.mapstruct.Mapper;

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
interface SingleIgnoredMapper {

    @Ignored(targets = { "<error descr="Unknown property 'name'">name</error>" })
    Target map(Source source);
}

@Mapper
interface ValidIgnoredMapper {

    @Ignored(targets = { "testName" })
    Target map(Source source);
}

@Mapper
interface EmptyIgnoredMapper {

    @Ignored(targets = { <error descr="Unknown property ''">""</error> })
    Target map(Source source);
}

@Mapper
interface MultipleIgnoredMapper {

    @Ignored(targets = { "testName", "<error descr="Unknown property 'unknown'">unknown</error>" })
    Target map(Source source);
}

@Mapper
interface IgnoredWithPrefixMapper {

    @Ignored(prefix = "target", targets = { "<error descr="Unknown property 'unknown'">unknown</error>" })
    Inner map(Source source);
}

@Mapper
interface ValidIgnoredWithPrefixMapper {

    @Ignored(prefix = "target", targets = { "testName" })
    Inner map(Source source);
}

@Mapper
interface IgnoredNestedTargetMapper {

    @Ignored(targets = { "target.<error descr="Unknown property 'unknown'">unknown</error>" })
    Inner map(Source source);
}

@Mapper
interface ValidIgnoredNestedTargetMapper {

    @Ignored(targets = { "target.testName" })
    Inner map(Source source);
}

@Mapper
interface SingleNoArrayIgnoredMapper {

    @Ignored(targets = "<error descr="Unknown property 'name'">name</error>")
    Target map(Source source);
}

@Mapper
interface IgnoredWithInvalidPrefixMapper {

    @Ignored(prefix = "<error descr="Unknown property 'nonExistent'">nonExistent</error>", targets = { "<error descr="Unknown property 'foo'">foo</error>" })
    Inner map(Source source);
}
