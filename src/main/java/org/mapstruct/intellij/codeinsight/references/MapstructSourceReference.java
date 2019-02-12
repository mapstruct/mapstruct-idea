/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.util.MapstructUtil;

import static org.mapstruct.intellij.util.MapstructUtil.asLookup;
import static org.mapstruct.intellij.util.MapstructUtil.isPublic;
import static org.mapstruct.intellij.util.SourceUtils.getParameterType;
import static org.mapstruct.intellij.util.SourceUtils.publicGetters;

/**
 * Reference for {@link org.mapstruct.Mapping#source()}.
 *
 * @author Filip Hrisafov
 */
class MapstructSourceReference extends MapstructBaseReference {

    /**
     * Create a new {@link MapstructSourceReference} with the provided parameters.
     *
     * @param element the element that the reference belongs to
     * @param previousReference the previous reference if there is one (in nested properties for example)
     * @param rangeInElement the range that the reference represent in the {@code element}
     */
    private MapstructSourceReference(PsiLiteral element, MapstructSourceReference previousReference,
        TextRange rangeInElement) {
        super( element, previousReference, rangeInElement );
    }

    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiType psiType) {
        PsiClass psiClass = PsiUtil.resolveClassInType( psiType );
        if ( psiClass == null ) {
            return null;
        }
        PsiMethod[] methods = psiClass.findMethodsByName( "get" + MapstructUtil.capitalize( value ), true );

        if ( methods.length == 0 ) {
            methods = psiClass.findMethodsByName( "is" + MapstructUtil.capitalize( value ), true );
        }
        if ( methods.length > 0 ) {
            return methods[0];
        }

        PsiField field = psiClass.findFieldByName( value, true );
        if ( field != null && isPublic( field ) ) {
            return field;
        }
        return null;
    }

    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod) {
        PsiParameter[] sourceParameters = MapstructUtil.getSourceParameters( mappingMethod );
        if ( sourceParameters.length == 0 ) {
            return null;
        }

        if ( sourceParameters.length == 1 ) {
            PsiType parameterType = getParameterType( sourceParameters[0] );
            PsiElement psiElement = parameterType == null ? null : resolveInternal( value, parameterType );
            if ( psiElement != null ) {
                return psiElement;
            }
        }
        //TODO first do property mapping then parameter

        return Stream.of( sourceParameters )
            .filter( psiParameter -> Objects.equals( psiParameter.getName(), value ) )
            .findAny()
            .orElse( null );
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiType psiType) {
        Set<LookupElement> elements = publicGetters( psiType )
                .map( pair -> MapstructUtil.asLookup( pair, PsiMethod::getReturnType ) )
                .collect( Collectors.toSet() );

        PsiClass psiClass = PsiUtil.resolveClassInType( psiType );
        if ( psiClass != null ) {
            for ( PsiField field : psiClass.getAllFields() ) {
                if ( isPublic( field ) ) {
                    elements.add( asLookup( field ) );
                }
            }
        }

        return elements.toArray();
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod) {
        PsiParameter[] sourceParameters = MapstructUtil.getSourceParameters( mappingMethod );
        if ( sourceParameters.length == 1 ) {
            PsiType parameterType = getParameterType( sourceParameters[0] );
            return parameterType == null ? LookupElement.EMPTY_ARRAY : getVariantsInternal( parameterType );
        }

        return sourceParameters;
    }

    @Nullable
    @Override
    PsiType resolvedType() {
        PsiElement element = resolve();
        if ( element instanceof PsiMethod ) {
            return ( (PsiMethod) element ).getReturnType();
        }
        else if ( element instanceof PsiParameter ) {
            return ( (PsiParameter) element ).getType();
        }

        return null;
    }

    /**
     * @param psiLiteral the literal for which references need to be created
     *
     * @return the references for the given {@code psiLiteral}
     */
    static PsiReference[] create(PsiLiteral psiLiteral) {
        return MapstructBaseReference.create( psiLiteral, MapstructSourceReference::new );
    }
}
