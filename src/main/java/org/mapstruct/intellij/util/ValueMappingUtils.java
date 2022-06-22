/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import java.util.Objects;
import java.util.stream.Stream;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import static org.mapstruct.intellij.util.MapstructAnnotationUtils.findAllDefinedValueMappingAnnotations;

/**
 * @author Filip Hrisafov
 */
public class ValueMappingUtils {

    private ValueMappingUtils() {
    }

    /**
     * Find all defined {@link org.mapstruct.ValueMapping#source()} for the given method
     *
     * @param method that needs to be checked
     *
     * @return see description
     */
    public static Stream<String> findAllDefinedValueMappingSources(@NotNull PsiMethod method) {
        return findAllDefinedValueMappingAnnotations( method )
            .map( psiAnnotation -> AnnotationUtil.getDeclaredStringAttributeValue( psiAnnotation, "source" ) )
            .filter( Objects::nonNull )
            .filter( s -> !s.isEmpty() );
    }
}
