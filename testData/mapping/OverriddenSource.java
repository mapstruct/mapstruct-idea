/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OverriddenSourceMapper {

    @Mapping(source = "<caret>i")
    Target mapSource(Source source);
}

class Target {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

interface Id {
    String getId();
}

interface Source extends Id {

    @Override
    String getId();

}
