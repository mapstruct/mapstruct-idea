/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.data;

import org.mapstruct.Mapping;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Mapping(target = "testName", ignore = true)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface IgnoreTestName { }
