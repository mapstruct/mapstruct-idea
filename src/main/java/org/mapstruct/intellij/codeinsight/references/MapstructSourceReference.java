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

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
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

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiClass sourceClass = getRelevantClass();
        String value = getValue();
        if ( sourceClass == null || value.isEmpty()) {
            return null;
        }
        PsiMethod[] methods = sourceClass.findMethodsByName( "get" + Strings.capitalize( value ), true );

        if ( methods.length == 0 ) {
            methods = sourceClass.findMethodsByName( "is" + Strings.capitalize( value ), true );
        }

        //If instead of doing the above we replace with the below highlighting, renaming, Find Usages works correctly
        //PsiMethod[] methods = sourceClass.findMethodsByName(getValue(), true);

        if ( methods.length == 0 ) {
            PsiMethod mappingMethod = getMappingMethod();
            if ( mappingMethod == null || mappingMethod.getParameterList().getParametersCount() == 0 ) {
                return null;
            }
            return Stream.of( mappingMethod.getParameterList().getParameters() )
                .filter( MapstructUtil::isValidSourceParameter )
                .filter( psiParameter -> Objects.equals( psiParameter.getName(), value ) )
                .findAny()
                .orElse( null );
        }

        return methods[0];
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        PsiClass sourceClass = getRelevantClass();
        if ( sourceClass == null ) {
            return new Object[0];
        }

        return sourceClass.getAllMethodsAndTheirSubstitutors().stream()
            .filter( pair -> MapstructUtil.isGetter( pair.getFirst() ) )
            .filter( pair -> MapstructUtil.isPublic( pair.getFirst() ) )
            .map( pair -> MapstructUtil.asLookup( pair, PsiMethod::getReturnType ) )
            .toArray();
    }

    @Override
    PsiClass getRelevantClass(@NotNull PsiMethod mappingMethod) {
        PsiParameterList parameters = mappingMethod.getParameterList();

        if ( parameters.getParametersCount() == 0 ) {
            return null;
        }

        //TODO this is not really correct, we need to adapt with @MappingTarget and return, multiple sources,
        // result type etc
        return Stream.of( parameters.getParameters() )
            .filter( MapstructUtil::isValidSourceParameter )
            .findAny()
            .map( PsiParameter::getType )
            .map( PsiUtil::resolveClassInType )
            .orElse( null );
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
