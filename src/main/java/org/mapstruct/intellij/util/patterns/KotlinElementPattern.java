/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util.patterns;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import static org.mapstruct.intellij.util.patterns.MapStructKotlinPatterns.ktAnnotation;
import static org.mapstruct.intellij.util.patterns.MapStructKotlinPatterns.ktValueArgument;

/**
 * @author Filip Hrisafov
 */
public class KotlinElementPattern<T extends PsiElement, Self extends KotlinElementPattern<T, Self>>
    extends PsiElementPattern<T, Self> {

    public KotlinElementPattern(final Class<T> aClass) {
        super( aClass );
    }

    public Self insideRepeatableAnnotationParam(
        ElementPattern<String> annotationQualifiedName,
        ElementPattern<String> annotationHolderQualifiedName,
        String parameterName) {
        // A repeatable annotation in kotlin has 2 possible ways of PSI structure:
        // 1. Part of the repeatable holder
        // @Mappings(
        //   Mapping(target = "name")
        // )
        // 2. Just the annotation
        // @Mapping(target = "name")

        KtValueArgumentPattern ktValueArgumentPattern = ktValueArgument().withName( parameterName );
        return withElementType( KtStubElementTypes.STRING_TEMPLATE ).andOr(
            withParent(
                ktValueArgumentPattern
                    .withAncestor( 5, ktAnnotation().qName( annotationHolderQualifiedName ) )
            ),

            withParent(
                ktValueArgumentPattern
                    .withSuperParent( 2, ktAnnotation().qName( annotationQualifiedName ) )
            )
        );
    }

    public Self insideAnnotationParam(
        ElementPattern<String> annotationQualifiedName,
        String parameterName) {

        KtValueArgumentPattern ktValueArgumentPattern = ktValueArgument().withName( parameterName );
        return withElementType( KtStubElementTypes.STRING_TEMPLATE ).andOr(
            withAncestor(
                2,
                ktValueArgumentPattern
                    .withSuperParent( 2, ktAnnotation().qName( annotationQualifiedName ) )
            )
        );
    }

    public static class Capture<T extends PsiElement> extends KotlinElementPattern<T, KotlinElementPattern.Capture<T>> {
        public Capture(Class<T> aClass) {
            super( aClass );
        }

    }
}
