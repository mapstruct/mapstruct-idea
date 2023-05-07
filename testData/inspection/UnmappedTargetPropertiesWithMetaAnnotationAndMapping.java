/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.data.IgnoreTestName;
import org.example.data.UnmappedTargetPropertiesData.Target;

@Mapper
public interface UnmappedTargetPropertiesWithMetaAnnotationAndMapping {

    @IgnoreTestName
    @Mapping(target = "moreTarget", constant = "some-value")
    @Mapping(target = "matching", constant = "some-value")
    Target map(Source source);

    public static class Source {
        Integer order;
    }

}