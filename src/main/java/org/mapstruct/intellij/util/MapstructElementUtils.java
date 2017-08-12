/*
 *  Copyright 2017 the MapStruct authors (http://www.mapstruct.org/)
 *  and/or other contributors as indicated by the @authors tag. See the
 *  copyright.txt file in the distribution for a full listing of all
 *  contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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

    private static PsiJavaElementPattern.Capture<PsiElement> elementPattern(String parameterName,
        String annotationFQN) {
        return psiElement()
            .insideAnnotationParam(
                StandardPatterns.string().equalTo( annotationFQN ),
                parameterName
            );
    }
}
