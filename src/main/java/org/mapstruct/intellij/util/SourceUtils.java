/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
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
import com.intellij.psi.PsiType;
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
                .map( SourceUtils::getParameterType )
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
        PsiType parameterType = getParameterType( parameter );
        return parameterType != null ? PsiUtil.resolveClassInType( parameterType ) : null;
    }

    /**
     * Find the type for the given {@code parameter}
     *
     * @param parameter the parameter
     *
     * @return the type for the parameter
     */
    @Nullable
    public static PsiType getParameterType(@NotNull PsiParameter parameter) {
        return canDescendIntoType( parameter.getType() ) ? parameter.getType() : null;
    }

    /**
     * Extract all public getters with their psi substitutors from the given {@code psiType}
     *
     * @param psiType to use to extract the getters
     *
     * @return a stream that holds all public getters for the given {@code psiType}
     */
    public static Stream<Pair<PsiMethod, PsiSubstitutor>> publicGetters(@NotNull PsiType psiType) {
        PsiClass psiClass = PsiUtil.resolveClassInType( psiType );
        if ( psiClass == null ) {
            return Stream.empty();
        }
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
