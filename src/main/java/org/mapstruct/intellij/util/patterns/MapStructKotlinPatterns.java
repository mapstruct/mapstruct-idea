/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util.patterns;

import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;

/**
 * @author Filip Hrisafov
 */
public class MapStructKotlinPatterns extends StandardPatterns {

    public static KotlinElementPattern.Capture<PsiElement> psiElement() {
        return new KotlinElementPattern.Capture<>( PsiElement.class );
    }

    public static KtValueArgumentPattern ktValueArgument() {
        return KtValueArgumentPattern.KT_VALUE_ARGUMENT_PATTERN;
    }

    public static KtValueArgumentNamePattern ktValueArgumentName() {
        return KtValueArgumentNamePattern.KT_VALUE_ARGUMENT_PATTERN;
    }

    public static KtAnnotationEntryPattern ktAnnotation() {
        return KtAnnotationEntryPattern.KT_ANNOTATION_ENTRY_PATTERN;
    }
}
