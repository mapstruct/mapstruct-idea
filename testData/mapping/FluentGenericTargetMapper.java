/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.test.complex;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface FluentGenericTargetMapper {

    @Mapping(source = "sourceId", target = "<caret>id")
    @Mapping(source = "sourceValue", target = "vlue")
    Target map(Source source);
}

class Base<T extends Base<T>> {

    private String id;

    public String getId() {
        return id;
    }

    public T setId(String id) {
        this.id = id;
        return (T) this;
    }
}

class Target extends Base<Target> {

    private String value;

    public String getValue() {
        return value;
    }

    public Target setValue(String value) {
        this.value = value;
    }
}

class Source {

    private String sourceId;
    private String sourceValue;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }
}