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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "name", source = "name", conditionExpression = """
           java( !source.getName().length > 0 ) """)
    @Mapping(target = "lastName", source = "name",defaultExpression = """
           java( \"  \" ) """)
    @Mapping(target = "city", expression = """
           java( \"  \" )  """)
    Target map(Source source);
}