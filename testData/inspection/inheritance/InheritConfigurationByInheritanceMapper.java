/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.example.dto.Person;
import org.example.dto.PersonDto;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface InheritConfigurationByInheritanceMapper {

    @Mapping(target = "fullName", source = "name")
    PersonDto map(Person person);

    @InheritConfiguration
    @Mapping(target = "ageInYears", source = "age")
    NaturalPersonDto map(NaturalPerson naturalPerson);

}

class NaturalPerson extends Person {

    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}

class NaturalPersonDto extends PersonDto {

    private Long ageInYears;

    public Long getAgeInYears() {
        return ageInYears;
    }

    public void setAgeInYears(Long ageInYears) {
        this.ageInYears = ageInYears;
    }

}
