/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import com.intellij.patterns.PsiJavaElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

/**
 * Utils for working with MapStruct elements.
 *
 * @author Filip Hrisafov
 */
public final class MapstructElementUtils {

    /**
     * Hide default constructor.
     */
    private MapstructElementUtils() {
    }

    /**
     * @param parameterName the name of the parameter in the {@code @ValueMapping} annotation
     *
     * @return an element pattern for a parameter in the {@code @ValueMapping} annotation
     */
    public static PsiJavaElementPattern.Capture<PsiElement> valueMappingElementPattern(String parameterName) {
        return elementPattern( parameterName, MapstructUtil.VALUE_MAPPING_ANNOTATION_FQN );
    }

    /**
     * @param parameterName the name of the parameter in the {@code @Mapping} annotation
     *
     * @return an element pattern for a parameter in the {@code @Mapping} annotation
     */
    public static PsiJavaElementPattern.Capture<PsiElement> mappingElementPattern(String parameterName) {
        return elementPattern( parameterName, MapstructUtil.MAPPING_ANNOTATION_FQN );
    }

    /**
     * @param parameterName the name of the parameter in the {@code @Mapper} annotation
     *
     * @return an element pattern for a parameter in the {@code @Mapper} annotation
     */
    public static PsiJavaElementPattern.Capture<PsiElement> mapperElementPattern(String parameterName) {
        return elementPattern( parameterName, MapstructUtil.MAPPER_ANNOTATION_FQN );
    }

    /**
     * @param parameterName the name of the parameter in the {@code @MapperConfig} annotation
     *
     * @return an element pattern for a parameter in the {@code @MapperConfig} annotation
     */
    public static PsiJavaElementPattern.Capture<PsiElement> mapperConfigElementPattern(String parameterName) {
        return elementPattern( parameterName, MapstructUtil.MAPPER_CONFIG_ANNOTATION_FQN );
    }

    /**
     * @param parameterName the name of the parameter in the {@code @BeanMapping} annotation
     *
     * @return an element pattern for a parameter in the {@code @BeanMapping} annotation
     */
    public static PsiJavaElementPattern.Capture<PsiElement> beanMappingElementPattern(String parameterName) {
        return elementPattern( parameterName, MapstructUtil.BEAN_MAPPING_FQN );
    }

    private static PsiJavaElementPattern.Capture<PsiElement> elementPattern(String parameterName,
        String annotationFQN) {
        return psiElement()
            .insideAnnotationParam(
                StandardPatterns.string().equalTo( annotationFQN ),
                parameterName
            );
    }
}
