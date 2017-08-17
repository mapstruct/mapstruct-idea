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

import java.util.stream.Stream;

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
 * Reference for {@link org.mapstruct.Mapping#target()}.
 *
 * @author Filip Hrisafov
 */
class MapstructTargetReference extends MapstructBaseReference {

    /**
     * Create a new {@link MapstructTargetReference} with the provided parameters
     *
     * @param element the element that the reference belongs to
     * @param previousReference the previous reference if there is one (in nested properties for example)
     * @param rangeInElement the range that the reference represent in the {@code element}
     */
    private MapstructTargetReference(PsiLiteral element, MapstructTargetReference previousReference,
        TextRange rangeInElement) {
        super( element, previousReference, rangeInElement );
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiClass returnClass = getRelevantClass();
        String value = getValue();
        if ( returnClass == null || value.isEmpty() ) {
            return null;
        }
        PsiMethod[] methods = returnClass.findMethodsByName( "set" + Strings.capitalize( value ), true );

        return methods.length == 0 ? null : methods[0];
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        PsiClass returnClass = getRelevantClass();
        if ( returnClass == null ) {
            return new Object[0];
        }

        return returnClass.getAllMethodsAndTheirSubstitutors().stream()
            .filter( pair -> MapstructUtil.isSetter( pair.getFirst() ) )
            .filter( pair -> MapstructUtil.isPublic( pair.getFirst() ) )
            .map( pair -> MapstructUtil.asLookup(
                pair,
                MapstructTargetReference::firstParameterPsiType
            ) )
            .toArray();
    }

    @Override
    PsiClass getRelevantClass(@NotNull PsiMethod mappingMethod) {
        //TODO here we need to take into consideration both with @MappingTarget and return,
        // returning an interface etc.
        PsiClass psiClass = PsiUtil.resolveClassInType( mappingMethod.getReturnType() );
        if ( psiClass == null ) {
            psiClass = Stream.of( mappingMethod.getParameterList().getParameters() )
                .filter( MapstructUtil::isMappingTarget )
                .findAny()
                .map( PsiParameter::getType )
                .map( PsiUtil::resolveClassInType )
                .orElse( null );
        }
        return psiClass;
    }

    @Nullable
    @Override
    PsiType resolvedType() {
        PsiElement element = resolve();

        if ( element instanceof PsiMethod ) {
            return firstParameterPsiType( (PsiMethod) element );
        }
        return null;
    }

    /**
     * @param psiLiteral the literal for which references need to be created
     *
     * @return the references for the given {@code psiLiteral}
     */
    static PsiReference[] create(PsiLiteral psiLiteral) {
        return MapstructBaseReference.create( psiLiteral, MapstructTargetReference::new );
    }

    /**
     * Util function for extracting the type of the first parameter of a method.
     *
     * @param psiMethod the method to extract the parameter from
     *
     * @return the type of the first parameter of the method
     */
    private static PsiType firstParameterPsiType(PsiMethod psiMethod) {
        return psiMethod.getParameterList().getParameters()[0].getType();
    }
}
