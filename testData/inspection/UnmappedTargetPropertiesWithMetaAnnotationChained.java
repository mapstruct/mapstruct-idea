/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.example.data.AnnotationChained;
import org.example.data.UnmappedTargetPropertiesData.Target;

@Mapper
public interface UnmappedTargetPropertiesWithMetaAnnotationChained {

    @AnnotationChained
    Target map(Source source);

    public static class Source {
        Integer order;
    }

}