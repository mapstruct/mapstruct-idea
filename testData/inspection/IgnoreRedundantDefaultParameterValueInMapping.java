/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.data.UnmappedFluentTargetPropertiesData.Target;
import org.example.data.UnmappedFluentTargetPropertiesData.Source;

@Mapper
interface MyMapper {

    @Mapping(target = "name", constant = "")
    @Mapping(target = "name", defaultValue = "")
    @Mapping(target = "age", ignore = <warning descr="Redundant default parameter value assignment">false</warning>)
    Target map(String source);

    class Target {

        private String name;
        private String age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }
}
