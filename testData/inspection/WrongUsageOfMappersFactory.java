/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

interface NotMapStructMapper {

    NotMapStructMapper INSTANCE = <warning descr="Using mappers factory for non mapstruct @Mapper">Mappers.getMapper( NotMapStructMapper.class )</warning>;

    Target map(Source source);
}

interface NotMapStructMapper2 {

    NotMapStructMapper2 INSTANCE = <warning descr="Using mappers factory for non mapstruct @Mapper">Mappers.getMapper( NotMapStructMapper2.class )</warning>;

    Target map(Source source);
}

@Mapper
interface DefaultComponentModelMapper {

    DefaultComponentModelMapper INSTANCE = Mappers.getMapper( DefaultComponentModelMapper.class );

    Target map(Source source);
}

@Mapper(componentModel = "default")
interface DefaultComponentModelMapper2 {

    DefaultComponentModelMapper2 INSTANCE = Mappers.getMapper( DefaultComponentModelMapper2.class );

    Target map(Source source);
}

@Mapper(componentModel = "spring")
interface SpringComponentModelMapper {

    SpringComponentModelMapper INSTANCE = <warning descr="Using Mappers factory with non default component model">Mappers.getMapper( SpringComponentModelMapper.class )</warning>;

    Target map(Source source);
}

@Mapper(componentModel = "jsr330", unmappedTargetPolicy = ReportingPolicy.ERROR)
interface Jsr330ComponentModelMapper {

    Jsr330ComponentModelMapper INSTANCE = <warning descr="Using Mappers factory with non default component model">Mappers.getMapper( Jsr330ComponentModelMapper.class )</warning>;

    Target map(Source source);
}

@Mapper(componentModel = "custom")
interface CustomComponentModelMapper {

    CustomComponentModelMapper INSTANCE = <warning descr="Using Mappers factory with non default component model">Mappers.getMapper( CustomComponentModelMapper.class )</warning>;

    Target map(Source source);
}

class Source {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

class Target {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}