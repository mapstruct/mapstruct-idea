/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import java.util.Objects;
import java.util.stream.Stream;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.util.MapstructUtil;

import static org.mapstruct.intellij.util.TargetUtils.getRelevantType;

/**
 * Reference for {@link org.mapstruct.Mapping#target()}.
 *
 * @author Filip Hrisafov
 */
class MapstructTargetReference extends BaseTargetReference {

    /**
     * Create a new {@link MapstructTargetReference} with the provided parameters
     *
     * @param element the element that the reference belongs to
     * @param previousReference the previous reference if there is one (in nested properties for example)
     * @param rangeInElement the range that the reference represent in the {@code element}
     * @param value the matched value (useful when {@code rangeInElement} is empty)
     */
    private MapstructTargetReference(PsiElement element, MapstructTargetReference previousReference,
        TextRange rangeInElement, String value) {
        super( element, previousReference, rangeInElement, value );
    }

    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod) {
        PsiType relevantType = getRelevantType( mappingMethod );
        if ( relevantType == null ) {
            return null;
        }

        PsiElement psiElement = resolveTargetElement(
            value,
            relevantType,
            mappingMethod
        );
        if ( psiElement != null ) {
            return psiElement;
        }

        return Stream.of( mappingMethod.getParameterList().getParameters() )
            .filter( MapstructUtil::isMappingTarget )
            .filter( psiParameter -> Objects.equals( psiParameter.getName(), value ) )
            .findAny()
            .orElse( null );
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod) {
        PsiType targetType = getRelevantType( mappingMethod );
        return targetType == null ? LookupElement.EMPTY_ARRAY : getVariantsInternal( targetType );
    }

    /**
     * @param psiElement the literal for which references need to be created
     *
     * @return the references for the given {@code psiLiteral}
     */
    static PsiReference[] create(PsiElement psiElement) {
        return MapstructBaseReference.create( psiElement, MapstructTargetReference::new, true );
    }

}
