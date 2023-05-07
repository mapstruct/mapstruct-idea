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

    @Mapping(target = "<caret>i")
    Target mapSource(Source source);
}

class Target implements Id {

    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}

interface Id {
    String getId();

    void setId(String id);
}

interface Source {

    String getId();
}
