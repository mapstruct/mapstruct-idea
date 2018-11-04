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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.mapstruct.intellij.util.MapstructUtil.canDescendIntoType;
import static org.mapstruct.intellij.util.MapstructUtil.getSourceParameters;

/**
 * Utils for working with source properties (extracting sources for MapStruct).
 *
 * @author Filip Hrisafov
 */
public class SourceUtils {

    private SourceUtils() {
    }

    /**
     * Find all source properties for the given {@code method}. If the method has only one source parameter then the
     * properties of the source class are returned. Otherwise the names of all the source parameters are returned
     *
     * @param method to be used
     *
     * @return see description
     */
    public static Stream<String> findAllSourceProperties(@NotNull PsiMethod method) {
        PsiParameter[] sourceParameters = getSourceParameters( method );
        if ( sourceParameters.length == 1 ) {
            return Stream.of( sourceParameters[0] )
                .map( SourceUtils::getParameterClass )
                .filter( Objects::nonNull )
                .flatMap( SourceUtils::publicGetters )
                .map( pair -> pair.getFirst() )
                .map( MapstructUtil::getPropertyName );
        }

        return Stream.of( sourceParameters )
            .map( PsiParameter::getName );
    }

    /**
     * Find the class for the given {@code parameter}
     *
     * @param parameter the parameter
     *
     * @return the class for the parameter
     */
    @Nullable
    public static PsiClass getParameterClass(@NotNull PsiParameter parameter) {
        return canDescendIntoType( parameter.getType() ) ? PsiUtil.resolveClassInType( parameter.getType() ) : null;
    }

    /**
     * Extract all public getters with their psi substitutors from the given {@code psiClass}
     *
     * @param psiClass to use to extract the getters
     *
     * @return a stream that holds all public getters for the given {@code psiClass}
     */
    public static Stream<Pair<PsiMethod, PsiSubstitutor>> publicGetters(@NotNull PsiClass psiClass) {
        Set<PsiMethod> overriddenMethods = new HashSet<>();
        List<Pair<PsiMethod, PsiSubstitutor>> publicGetters = new ArrayList<>();
        for ( Pair<PsiMethod, PsiSubstitutor> pair : psiClass.getAllMethodsAndTheirSubstitutors() ) {
            PsiMethod method = pair.getFirst();
            if ( MapstructUtil.isGetter( method ) && MapstructUtil.isPublic( method ) &&
                !overriddenMethods.contains( method ) ) {
                // If this is a public getter then populate its overridden methods and use it
                overriddenMethods.addAll( Arrays.asList( method.findSuperMethods() ) );
                publicGetters.add( pair );
            }
        }

        return publicGetters.stream();
    }
}
