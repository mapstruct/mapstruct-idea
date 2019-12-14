/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface Issue33Mapper {

    @Mapping(target = "<caret>")
    BookDto map(Boolean isAdded);
}

public class BookDto {

    protected Boolean isAdded;

    public BookDto isAdded(Boolean isAdded) {
        this.isAdded = isAdded;
        return this;
    }

    public Boolean getIsAdded() {
        return isAdded;
    }

    public void setIsAdded(Boolean isAdded) {
        this.isAdded = isAdded;
    }
}

