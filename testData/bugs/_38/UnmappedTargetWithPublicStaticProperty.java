/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface Issue38Mapper {

    @Mapping(target = "<caret>")
    BookDto map(String isbn);
}

public class BookDto {

    protected String isbn;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public static BookDto fromIsbn(String isbn) {
        BookDto dto = new BookDto();
        dto.setIsbn( isbn );
        return dto;
    }
}

