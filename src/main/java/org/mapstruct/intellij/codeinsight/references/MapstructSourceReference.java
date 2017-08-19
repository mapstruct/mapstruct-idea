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
package org.mapstruct.intellij.codeinsight.references;

import java.util.Objects;
import java.util.stream.Stream;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.ap.internal.util.Strings;
import org.mapstruct.intellij.util.MapstructUtil;

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
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiClass psiClass) {
        PsiMethod[] methods = psiClass.findMethodsByName( "get" + Strings.capitalize( value ), true );

        if ( methods.length == 0 ) {
            methods = psiClass.findMethodsByName( "is" + Strings.capitalize( value ), true );
        }
        return methods.length == 0 ? null : methods[0];
    }

    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod) {
        PsiParameter[] sourceParameters = MapstructUtil.getSourceParameters( mappingMethod );
        if ( sourceParameters.length == 0 ) {
            return null;
        }

        if ( sourceParameters.length == 1 ) {
            PsiClass parameterClass = getParameterClass( sourceParameters[0] );
            PsiElement psiElement = parameterClass == null ? null : resolveInternal( value, parameterClass );
            if ( psiElement != null ) {
                return psiElement;
            }
        }

        return Stream.of( sourceParameters )
            .filter( psiParameter -> Objects.equals( psiParameter.getName(), value ) )
            .findAny()
            .orElse( null );
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiClass psiClass) {
        return psiClass.getAllMethodsAndTheirSubstitutors().stream()
            .filter( pair -> MapstructUtil.isGetter( pair.getFirst() ) )
            .filter( pair -> MapstructUtil.isPublic( pair.getFirst() ) )
            .map( pair -> MapstructUtil.asLookup( pair, PsiMethod::getReturnType ) )
            .toArray();
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod) {
        PsiParameter[] sourceParameters = MapstructUtil.getSourceParameters( mappingMethod );
        if ( sourceParameters.length == 1 ) {
            PsiClass parameterClass = getParameterClass( sourceParameters[0] );
            return parameterClass == null ? LookupElement.EMPTY_ARRAY : getVariantsInternal( parameterClass );
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

    /**
     * Find the class for the given {@code parameter}
     *
     * @param parameter the parameter
     *
     * @return the class for the parameter
     */
    @Nullable
    private static PsiClass getParameterClass(@NotNull PsiParameter parameter) {
        return PsiUtil.resolveClassInType( parameter.getType() );
    }
}
