/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.helper.qualifiedbyname;

import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public interface OptionalMapper {

    @Named("unwrapOptional")
    default <T> T unwrapOptional(Optional<T> input) {
        return input.orElse( null );
    }

}
