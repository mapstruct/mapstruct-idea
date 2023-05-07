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

@org.mapstruct.DecoratedWith(ConverterDecorator.class)
interface <error descr="@Mapper or @MapperConfig annotation missing">Converter</error> {


    @org.mapstruct.Mapping(source = "name", target = "testName")
    Target map(Source source);

    @org.mapstruct.Mappings({
        @org.mapstruct.Mapping(source = "testName", target = "name")
    })
    Source map(Target target);
}

abstract class ConverterDecorator implements Converter {

    private final Converter delegate;

    public ConverterDecorator(Converter delegate){
        this.delegate = delegate;
    }

    @Override
    public Target map(Source source) {
        //do something before or after mapping
        return delegate.map(source);
    }

    @Override
    public Source map(Target target) {
        //do something before or after mapping
        return delegate.map(target);
    }
}

@org.mapstruct.DecoratedWith(ConverterWithValueMappingsDecorator.class)
interface <error descr="@Mapper or @MapperConfig annotation missing">ConverterWithValueMappings</error> {


    @org.mapstruct.ValueMapping(source = "name", target = "testName")
    Target map(Source source);

    @org.mapstruct.ValueMappings({
        @org.mapstruct.ValueMapping(source = "testName", target = "name")
    })
    Source map(Target target);
}

abstract class ConverterWithValueMappingsDecorator implements Converter {

    private final Converter delegate;

    public ConverterWithValueMappingsDecorator(Converter delegate){
        this.delegate = delegate;
    }

    @Override
    public Target map(Source source) {
        //do something before or after mapping
        return delegate.map(source);
    }

    @Override
    public Source map(Target target) {
        //do something before or after mapping
        return delegate.map(target);
    }
}
