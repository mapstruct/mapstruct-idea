/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import java.beans.Introspector;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.mapstruct.intellij.util.MapstructUtil.canDescendIntoType;
import static org.mapstruct.intellij.util.MapstructUtil.getSourceParameters;
import static org.mapstruct.intellij.util.MapstructUtil.publicFields;

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
    public static Set<String> findAllSourceProperties(@NotNull PsiMethod method) {
        PsiParameter[] sourceParameters = getSourceParameters( method );
        if ( sourceParameters.length == 1 ) {
            PsiType parameterType = SourceUtils.getParameterType( sourceParameters[0] );
            return SourceUtils.publicReadAccessors( parameterType ).keySet();
        }

        return Stream.of( sourceParameters )
            .map( PsiParameter::getName )
            .collect( Collectors.toSet() );
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
     * Extract all public read accessors (public getters and fields)
     * with their psi substitutors from the given {@code psiType}
     *
     * @param psiType to use to extract the accessors
     *
     * @return a stream that holds all public read accessors for the given {@code psiType}
     */
    public static Map<String, Pair<? extends PsiMember, PsiSubstitutor>> publicReadAccessors(
        @Nullable PsiType psiType) {
        PsiClass psiClass = PsiUtil.resolveClassInType( psiType );
        if ( psiClass == null ) {
            return Collections.emptyMap();
        }

        Map<String, Pair<? extends PsiMember, PsiSubstitutor>> publicReadAccessors = new HashMap<>();

        publicReadAccessors.putAll( publicGetters( psiClass ) );
        publicReadAccessors.putAll( publicFields( psiClass ) );

        return publicReadAccessors;
    }

    /**
     * Extract all public getters with their psi substitutors from the given {@code psiClass}
     *
     * @param psiClass to use to extract the getters
     *
     * @return a list that holds all public getters for the given {@code psiClass}
     */
    private static Map<String, Pair<PsiMethod, PsiSubstitutor>> publicGetters(@NotNull PsiClass psiClass) {
        Set<PsiMethod> overriddenMethods = new HashSet<>();
        Map<String, Pair<PsiMethod, PsiSubstitutor>> publicGetters = new HashMap<>();
        for ( Pair<PsiMethod, PsiSubstitutor> pair : psiClass.getAllMethodsAndTheirSubstitutors() ) {
            PsiMethod method = pair.getFirst();
            String propertyName = extractPublicGetterPropertyName( method );
            if ( propertyName != null &&
                !overriddenMethods.contains( method ) ) {
                // If this is a public getter then populate its overridden methods and use it
                overriddenMethods.addAll( Arrays.asList( method.findSuperMethods() ) );
                publicGetters.put( propertyName, pair );
            }
        }

        return publicGetters;
    }

    @Nullable
    private static String extractPublicGetterPropertyName(PsiMethod method) {
        if ( method.getParameterList().getParametersCount() != 0 || !MapstructUtil.isPublic( method ) ) {
            return null;
        }
        // This logic is aligned with the DefaultAccessorNamingStrategy

        PsiType returnType = method.getReturnType();
        if ( returnType == null || PsiType.VOID.equals( returnType ) ) {
            return null;
        }

        String methodName = method.getName();
        if ( methodName.startsWith( "get" ) ) {
            if ( !methodName.equals( "getClass" ) ) {
                return Introspector.decapitalize( methodName.substring( 3 ) );
            }
            else {
                return null;
            }
        }
        else if ( methodName.startsWith( "is" ) && (
            PsiType.BOOLEAN.equals( returnType ) ||
                returnType.equalsToText( CommonClassNames.JAVA_LANG_BOOLEAN ) )
        ) {
            // boolean getter
            return Introspector.decapitalize( methodName.substring( 2 ) );
        }
        else {
            return null;
        }

    }
}
