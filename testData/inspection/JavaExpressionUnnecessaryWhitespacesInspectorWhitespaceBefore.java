/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.BeanMapping;
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

    private String name;
    private String lastName;
    private String city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "name", source = "name", <weak_warning descr="Unnecessary whitespaces before conditionExpression">conditionExpression = " java( !source.getName().length > 0 )"</weak_warning>)
    @Mapping(target = "lastName", source = "name", <weak_warning descr="Unnecessary whitespaces before defaultExpression">defaultExpression = " java( \"  \" )"</weak_warning>)
    @Mapping(target = "city", <weak_warning descr="Unnecessary whitespaces before expression">expression = "  java( \"  \" )"</weak_warning>)
    Target map(Source source);
}