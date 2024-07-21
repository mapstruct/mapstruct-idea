/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiRecordComponent;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.util.MapstructUtil;

import java.util.Objects;
import java.util.stream.Stream;

import static org.mapstruct.intellij.util.MapstructUtil.asLookup;
import static org.mapstruct.intellij.util.MapstructUtil.findRecordComponent;
import static org.mapstruct.intellij.util.MapstructUtil.isPublicNonStatic;
import static org.mapstruct.intellij.util.SourceUtils.getParameterType;
import static org.mapstruct.intellij.util.SourceUtils.publicReadAccessors;

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
     * @param value the matched value (useful when {@code rangeInElement} is empty)
     */
    private MapstructSourceReference(PsiElement element, MapstructSourceReference previousReference,
        TextRange rangeInElement, String value) {
        super( element, previousReference, rangeInElement, value );
    }

    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiType psiType) {
        PsiClass psiClass = PsiUtil.resolveClassInType( psiType );
        if ( psiClass == null ) {
            return null;
        }

        PsiRecordComponent recordComponent = findRecordComponent( value, psiClass );
        if ( recordComponent != null ) {
            return recordComponent;
        }

        PsiMethod[] methods = psiClass.findMethodsByName( "get" + MapstructUtil.capitalize( value ), true );

        if ( methods.length == 0 ) {
            methods = psiClass.findMethodsByName( "is" + MapstructUtil.capitalize( value ), true );
        }
        if ( methods.length > 0 && isPublicNonStatic( methods[0] ) ) {
            return methods[0];
        }

        PsiField field = psiClass.findFieldByName( value, true );
        if ( field != null && isPublicNonStatic( field ) ) {
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
        return asLookup( publicReadAccessors( psiType ), MapstructSourceReference::memberPsiType );
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod) {
        PsiParameter[] sourceParameters = MapstructUtil.getSourceParameters( mappingMethod );
        if ( sourceParameters.length == 1 ) {
            PsiType parameterType = getParameterType( sourceParameters[0] );
            return parameterType == null ? LookupElement.EMPTY_ARRAY : getVariantsInternal( parameterType );
        }

        return Stream.of( sourceParameters )
            .map( MapstructUtil::asLookup )
            .toArray( LookupElement[]::new );
    }

    @Nullable
    @Override
    PsiType resolvedType() {
        PsiElement element = resolve();

        if ( element instanceof PsiMethod psiMethod ) {
            return psiMethod.getReturnType();
        }
        else if ( element instanceof PsiParameter psiParameter ) {
            return psiParameter.getType();
        }
        else if ( element instanceof PsiRecordComponent psiRecordComponent ) {
            return psiRecordComponent.getType();
        }
        else if ( element instanceof PsiField psiField ) {
            return psiField.getType();
        }

        return null;
    }

    /**
     * @param psiElement the literal for which references need to be created
     *
     * @return the references for the given {@code psiLiteral}
     */
    static PsiReference[] create(PsiElement psiElement) {
        return MapstructBaseReference.create( psiElement, MapstructSourceReference::new, true );
    }

    /**
     * @param psiElement the literal for which references need to be created
     *
     * @return the references for the given {@code psiLiteral}
     */
    static PsiReference[] createNonNested(PsiElement psiElement) {
        return MapstructBaseReference.create( psiElement, MapstructSourceReference::new, false );
    }

    private static PsiType memberPsiType(PsiElement psiMember) {
        if ( psiMember instanceof PsiMethod psiMemberMethod ) {
            return psiMemberMethod.getReturnType();
        }
        else if ( psiMember instanceof PsiVariable psiMemberVariable ) {
            return psiMemberVariable.getType();
        }
        else {
            return null;
        }

    }
}
