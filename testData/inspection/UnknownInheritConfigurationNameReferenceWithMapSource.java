/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import java.util.Map;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

interface InheritConfigurationMapSource {

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
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
interface InheritConfigurationMapSourceMapper {

    @Mapping(target = "value", source = "source.value")
    InheritConfigurationMapSource.Target mapToTarget(Map<String, InheritConfigurationMapSource.Source> source);

    @InheritConfiguration(name = "<error descr="Unknown @InheritConfiguration method 'doesNotExist'">doesNotExist</error>")
    void copyIntoTarget(Map<String, InheritConfigurationMapSource.Source> source, @MappingTarget InheritConfigurationMapSource.Target target);

}
