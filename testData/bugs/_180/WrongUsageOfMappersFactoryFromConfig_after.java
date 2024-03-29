/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(config = DefaultConfigComponentModelMapper.DefaultConfig.class)
interface DefaultConfigComponentModelMapper {

    @MapperConfig(componentModel = "default")
    interface  DefaultConfig {

    }

    DefaultConfigComponentModelMapper INSTANCE = Mappers.getMapper( DefaultConfigComponentModelMapper.class );

    Target map(Source source);
}

@Mapper(config = SpringConfigComponentModelOverrideMapper.DefaultConfig.class, componentModel = "spring")
interface SpringConfigComponentModelOverrideMapper {

    @MapperConfig(componentModel = "default")
    interface  DefaultConfig {

    }

    Target map(Source source);
}

@Mapper(config = SpringConfigComponentModelMapper.SpringConfig.class)
interface SpringConfigComponentModelMapper {

    @MapperConfig(componentModel = "spring")
    interface SpringConfig {

    }

    Target map(Source source);
}

@Mapper(config = Jsr330ConfigComponentModelMapper.Jsr33Config.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
interface Jsr330ConfigComponentModelMapper {

    @MapperConfig(componentModel = "jsr330")
    interface Jsr33Config {

    }

    Target map(Source source);
}

@Mapper(config = CustomConfigComponentModelMapper.CustomConfig.class)
interface CustomConfigComponentModelMapper {

    @MapperConfig(componentModel = "custom")
    interface CustomConfig {

    }

    Target map(Source source);
}

@Mapper(config = DefaultConfigComponentModelOverrideMapper.SpringConfig.class, componentModel = "default")
interface DefaultConfigComponentModelOverrideMapper {

    @MapperConfig(componentModel = "spring")
    interface SpringConfig {

    }

    DefaultConfigComponentModelOverrideMapper INSTANCE = Mappers.getMapper( DefaultConfigComponentModelOverrideMapper.class );

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