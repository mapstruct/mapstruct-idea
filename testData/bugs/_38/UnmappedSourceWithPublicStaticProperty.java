/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface Issue38Mapper {

    @Mapping(source = "<caret>")
    Book map(BookDto isbn);
}

public class Book {

    public String isbn;

}

public class BookDto {

    protected static Integer inStock = 10;

    protected String isbn;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public static Integer getInStock() {
        return inStock;
    }
}

