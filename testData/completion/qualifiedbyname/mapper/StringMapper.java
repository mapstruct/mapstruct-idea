/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.helper.qualifiedbyname;

import org.mapstruct.Named;

public interface StringMapper {

    @Named("trimString")
    default String trim(String input) {
        return input.trim();
    }

}
