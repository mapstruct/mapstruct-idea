/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import java.beans.Introspector;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiRecordComponent;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypes;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.mapstruct.intellij.util.MapstructAnnotationUtils.findAllDefinedMappingAnnotations;
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
     * properties of the source class are returned. Otherwise the names of all the source parameters
     * and their properties are returned.
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

        Set<String> sourceProperties = new HashSet<>();
        for ( PsiParameter sourceParameter : sourceParameters ) {
            sourceProperties.add( sourceParameter.getName() );
            PsiType parameterType = SourceUtils.getParameterType( sourceParameter );
            sourceProperties.addAll( SourceUtils.publicReadAccessors( parameterType ).keySet() );
        }

        return sourceProperties;
    }

    /**
     * Find all defined {@link org.mapstruct.Mapping#source()} for the given method
     *
     * @param method that needs to be checked
     * @param mapStructVersion the MapStruct project version
     *
     * @return see description
     */
    public static Stream<String> findAllDefinedMappingSources(@NotNull PsiMethod method,
                                                              @NotNull MapStructVersion mapStructVersion) {
        return findAllDefinedMappingAnnotations( method, mapStructVersion )
            .map( psiAnnotation -> AnnotationUtil.getDeclaredStringAttributeValue( psiAnnotation, "source" ) )
            .filter( Objects::nonNull )
            .filter( s -> !s.isEmpty() );
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

    public static Map<String, Pair<? extends PsiElement, PsiSubstitutor>> publicReadAccessors(
        @Nullable PsiElement psiElement) {
        if ( psiElement instanceof PsiMethod psiMethod ) {
            return publicReadAccessors( psiMethod.getReturnType() );
        }
        else if ( psiElement instanceof PsiParameter psiParameter ) {
            return publicReadAccessors( psiParameter.getType() );
        }

        return Collections.emptyMap();
    }

    /**
     * Extract all public read accessors (public getters and fields)
     * with their psi substitutors from the given {@code psiType}
     *
     * @param psiType to use to extract the accessors
     *
     * @return a stream that holds all public read accessors for the given {@code psiType}
     */
    public static Map<String, Pair<? extends PsiElement, PsiSubstitutor>> publicReadAccessors(
        @Nullable PsiType psiType) {
        PsiClass psiClass = PsiUtil.resolveClassInType( psiType );
        if ( psiClass == null ) {
            return Collections.emptyMap();
        }

        Map<String, Pair<? extends PsiElement, PsiSubstitutor>> publicReadAccessors = new HashMap<>();

        publicReadAccessors.putAll( publicGetters( psiClass ) );
        publicReadAccessors.putAll( publicFields( psiClass ) );
        if ( psiClass.isRecord() ) {
            for ( PsiRecordComponent recordComponent : psiClass.getRecordComponents() ) {
                publicReadAccessors.put(
                    recordComponent.getName(),
                    Pair.create( recordComponent, PsiSubstitutor.EMPTY )
                );
            }
        }

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
        if ( method.getParameterList().getParametersCount() != 0 || !MapstructUtil.isPublicNonStatic( method ) ) {
            return null;
        }
        // This logic is aligned with the DefaultAccessorNamingStrategy

        PsiType returnType = method.getReturnType();
        if ( returnType == null || PsiTypes.voidType().equals( returnType ) ) {
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
            PsiTypes.booleanType().equals( returnType ) ||
                returnType.equalsToText( CommonClassNames.JAVA_LANG_BOOLEAN ) )
        ) {
            // boolean getter
            return Introspector.decapitalize( methodName.substring( 2 ) );
        }
        else {
            return null;
        }

    }

    @Nullable
    public static PsiType[] getGenericTypes(@Nullable PsiParameter fromMapMappingParameter) {
        if (fromMapMappingParameter == null ||
                !(fromMapMappingParameter.getType() instanceof PsiClassReferenceType)) {
            return null;
        }
        return ((PsiClassReferenceType) fromMapMappingParameter.getType()).getParameters();
    }
}
