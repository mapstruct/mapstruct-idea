/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import java.util.Objects;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.intellij.util.MapStructVersion;
import org.mapstruct.intellij.util.MapstructUtil;

import static org.mapstruct.intellij.inspection.inheritance.InheritConfigurationUtils.findInheritConfigurationMethods;

/**
 * Reference for {@link InheritConfiguration#name()}.
 *
 * @author Oliver Erhart
 */
class MapstructMappingInheritConfigurationReference extends MapstructNonNestedBaseReference {

    private final MapStructVersion mapStructVersion;

    /**
     * Create a new {@link MapstructMappingInheritConfigurationReference} with the provided parameters
     *
     * @param element           the element that the reference belongs to
     * @param previousReference the previous reference if there is one (in nested properties for example)
     * @param rangeInElement    the range that the reference represent in the {@code element}
     * @param value             the matched value (useful when {@code rangeInElement} is empty)
     */
    private MapstructMappingInheritConfigurationReference(
        PsiElement element,
        MapstructMappingInheritConfigurationReference previousReference,
        TextRange rangeInElement, String value
    ) {
        super( element, previousReference, rangeInElement, value );
        mapStructVersion = MapstructUtil.resolveMapStructProjectVersion( element.getContainingFile()
            .getOriginalFile() );
    }

    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod) {

        return findInheritConfigurationMethods( mappingMethod, mapStructVersion )
            .filter( a -> Objects.equals( a.getName(), value ) )
            .findAny()
            .orElse( null );
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod) {

        return findInheritConfigurationMethods( mappingMethod, mapStructVersion )
            .map( method -> MapstructUtil.asLookup( method, method.getName(), method.getName() ) )
            .filter( Objects::nonNull )
            .toArray();
    }

    /**
     * @param psiElement the literal for which references need to be created
     * @return the references for the given {@code psiLiteral}
     */
    static PsiReference[] create(PsiElement psiElement) {
        return MapstructBaseReference.create( psiElement, MapstructMappingInheritConfigurationReference::new, false );
    }

}
