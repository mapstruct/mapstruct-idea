/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.dto

import java.util.List
import org.example.dto.PersonDtoKt

data class CarDtoKt(
    var make: String? = null,
    var seatCount: Int = 0,
    var manufacturingYear: String? = null,
    var myDriver: PersonDtoKt? = null,
    var passengers: List<PersonDtoKt>? = null,
    var price: Long? = null,
    var category: String? = null,
    var available: Boolean = false
)
