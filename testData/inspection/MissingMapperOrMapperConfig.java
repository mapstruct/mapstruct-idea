/*
 *  Copyright 2017 the MapStruct authors (http://www.mapstruct.org/)
 *  and/or other contributors as indicated by the @authors tag. See the
 *  copyright.txt file in the distribution for a full listing of all
 *  contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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

interface InterfaceWithoutMethods {
}

abstract class ClassWithoutMethods {
}

interface InterfaceWithoutAnnotations {

    Target map(Source source);
}

abstract class ClassWithoutAnnotations {

    abstract Target map(Source source);
}

interface <error descr="@Mapper or @MapperConfig annotation missing">InterfaceWithMappingAnnotations</error> {

    @org.mapstruct.Mapping(source = "name", target = "testName")
    Target map(Source source);

    Source map(Target target);
}

abstract class <error descr="@Mapper or @MapperConfig annotation missing">ClassWithMappingAnnotations</error> {

    abstract Source map(Target target);

    @org.mapstruct.Mapping(source = "name", target = "testName")
    abstract Target map(Source source);
}

interface <error descr="@Mapper or @MapperConfig annotation missing">InterfaceWithMappingsAnnotations</error> {

    @org.mapstruct.Mappings({
        @org.mapstruct.Mapping(source = "name", target = "testName")
    })
    Target map(Source source);

    Source map(Target target);
}

abstract class <error descr="@Mapper or @MapperConfig annotation missing">ClassWithMappingsAnnotations</error> {

    @org.mapstruct.Mappings({
        @org.mapstruct.Mapping(source = "name", target = "testName")
    })
    abstract Target map(Source source);
}

interface <error descr="@Mapper or @MapperConfig annotation missing">InterfaceWithValueMappingAnnotations</error> {

    @org.mapstruct.ValueMapping(source = "name", target = "testName")
        Target map(Source source);
}

abstract class <error descr="@Mapper or @MapperConfig annotation missing">ClassWithValueMappingAnnotations</error> {

    @org.mapstruct.ValueMapping(source = "name", target = "testName")
    abstract Target map(Source source);
}

interface <error descr="@Mapper or @MapperConfig annotation missing">InterfaceWithValueMappingsAnnotations</error> {

    @org.mapstruct.ValueMappings({
        @org.mapstruct.ValueMapping(source = "name", target = "testName")
    })
    Target map(Source source);
}

abstract class <error descr="@Mapper or @MapperConfig annotation missing">ClassWithValueMappingsAnnotations</error> {

    @org.mapstruct.ValueMappings({
        @org.mapstruct.ValueMapping(source = "name", target = "testName")
    })
    abstract Target map(Source source);
}


// Valid Mappers and MapperConfigs

@org.mapstruct.Mapper
interface MapperWithMappingAnnotations {

    @org.mapstruct.Mapping(source = "name", target = "testName")
    Target map(Source source);
}

@org.mapstruct.MapperConfig
abstract class MapperConfigWithMappingAnnotations {

    @org.mapstruct.Mapping(source = "name", target = "testName")
    abstract Target map(Source source);
}

@org.mapstruct.MapperConfig
interface MapperConfigWithMappingsAnnotations {

    @org.mapstruct.Mappings({
        @org.mapstruct.Mapping(source = "name", target = "testName")
    })
    Target map(Source source);
}

@org.mapstruct.Mapper
abstract class MapperWithMappingsAnnotations {

    @org.mapstruct.Mappings({
        @org.mapstruct.Mapping(source = "name", target = "testName")
    })
    abstract Target map(Source source);
}

@org.mapstruct.MapperConfig
interface MapperConfigWithValueMappingAnnotations {

    @org.mapstruct.ValueMapping(source = "name", target = "testName")
    Target map(Source source);
}

@org.mapstruct.Mapper
abstract class MapperWithValueMappingAnnotations {

    @org.mapstruct.ValueMapping(source = "name", target = "testName")
    abstract Target map(Source source);
}

@org.mapstruct.Mapper
interface MapperWithValueMappingsAnnotations {

    @org.mapstruct.ValueMappings({
        @org.mapstruct.ValueMapping(source = "name", target = "testName")
    })
    Target map(Source source);
}

@org.mapstruct.MapperConfig
abstract class MapperConfigWithValueMappingsAnnotations {

    @org.mapstruct.ValueMappings({
        @org.mapstruct.ValueMapping(source = "name", target = "testName")
    })
    abstract Target map(Source source);
}
