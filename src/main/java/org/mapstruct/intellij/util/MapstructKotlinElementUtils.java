/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import org.mapstruct.intellij.util.patterns.KotlinElementPattern;

import static org.mapstruct.intellij.util.patterns.MapStructKotlinPatterns.psiElement;

/**
 * Utils for working with MapStruct kotlin elements.
 *
 * @author Filip Hrisafov
 */
public final class MapstructKotlinElementUtils {

    /**
     * Hide default constructor.
     */
    private MapstructKotlinElementUtils() {
    }

    /**
     * @param parameterName the name of the parameter in the {@code @ValueMapping} annotation
     *
     * @return an element pattern for a parameter in the {@code @ValueMapping} annotation
     */
    public static KotlinElementPattern.Capture<? extends PsiElement> valueMappingElementPattern(String parameterName) {
        return elementPattern(
            parameterName,
            MapstructUtil.VALUE_MAPPING_ANNOTATION_FQN,
            MapstructUtil.VALUE_MAPPINGS_ANNOTATION_FQN
        );
    }

    /**
     * @param parameterName the name of the parameter in the {@code @Mapping} annotation
     *
     * @return an element pattern for a parameter in the {@code @Mapping} annotation
     */
    public static KotlinElementPattern.Capture<? extends PsiElement> mappingElementPattern(String parameterName) {
        return elementPattern(
            parameterName,
            MapstructUtil.MAPPING_ANNOTATION_FQN,
            MapstructUtil.MAPPINGS_ANNOTATION_FQN
        );
    }

    private static KotlinElementPattern.Capture<? extends PsiElement> elementPattern(String parameterName,
        String annotationFQN,
        String annotationHolderFQN
    ) {
        return psiElement()
            .insideRepeatableAnnotationParam(
                StandardPatterns.string().equalTo( annotationFQN ),
                StandardPatterns.string().equalTo( annotationHolderFQN ),
                parameterName
            );
    }
}
