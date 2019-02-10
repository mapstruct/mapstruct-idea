/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

interface NotMapStructMapper {

    Target map(Source source);
}

@Mapper
interface NotMapStructMapper2 {

    NotMapStructMapper2 INSTANCE = Mappers.getMapper( NotMapStructMapper2.class );

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

    Target map(Source source);
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface Jsr330ComponentModelMapper {

    Jsr330ComponentModelMapper INSTANCE = Mappers.getMapper( Jsr330ComponentModelMapper.class );

    Target map(Source source);
}

@Mapper()
interface CustomComponentModelMapper {

    CustomComponentModelMapper INSTANCE = Mappers.getMapper( CustomComponentModelMapper.class );

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