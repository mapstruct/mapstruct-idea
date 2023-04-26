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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.codeInsight.AnnotationUtil.getStringAttributeValue;
import static org.mapstruct.intellij.util.MapstructAnnotationUtils.findMapperConfigReference;
import static org.mapstruct.intellij.util.MapstructAnnotationUtils.findReferencedMapperClasses;
import static org.mapstruct.intellij.util.MapstructUtil.isMappingTarget;

/**
 * Utils for working with inherited configurations based on {@link org.mapstruct.InheritConfiguration}
 */
public class InheritConfigurationUtils {

    private InheritConfigurationUtils() {
    }

    /**
     * Find all mapping methods (regardless of their type and parameter) that are in the scope to be inherited from:
     * <ul>
     *     <li>local methods</li>
     *     <li>methods of mappers extending from</li>
     *     <li>methods in referenced mappers by {@link org.mapstruct.Mapper#uses()}</li>
     *     <li>methods from {@link org.mapstruct.MapperConfig}</li>
     *     <li>methods from referenced mappers from {@link org.mapstruct.MapperConfig#uses()}</li>
     * </ul>
     *
     * @param containingClass containing class of the mapping method to check
     * @param mapperAnnotation the mapper annotation of the containing class
     * @return possible mapping methods to be inherited from
     */
    public static Stream<PsiMethod> findMappingMethodsFromInheritScope(@NotNull PsiClass containingClass,
                                                                       @NotNull PsiAnnotation mapperAnnotation) {

        Stream<PsiMethod> localAndParentMethods = findLocalAndParentMethods( containingClass ).stream();

        Stream<PsiMethod> referencedMethods = findReferencedMapperClasses( mapperAnnotation )
            .flatMap( c -> Arrays.stream( c.getMethods() ) );

        Stream<PsiMethod> mapperConfigMethods = findMapperConfigMethods( mapperAnnotation );

        return Stream.concat( Stream.concat( localAndParentMethods, referencedMethods ), mapperConfigMethods );
    }

    private static List<PsiMethod> findLocalAndParentMethods(PsiClass containingClass) {

        List<PsiMethod> result = new ArrayList<>( List.of( containingClass.getMethods() ) );

        for ( PsiClass anInterface : containingClass.getInterfaces() ) {
            result.addAll( findLocalAndParentMethods( anInterface ) );
        }

        return result;
    }

    private static Stream<PsiMethod> findMapperConfigMethods(@NotNull PsiAnnotation mapperAnnotation) {

        PsiModifierListOwner mapperConfigReference = findMapperConfigReference( mapperAnnotation );

        if ( !( mapperConfigReference instanceof PsiClass ) ) {
            return Stream.empty();
        }

        return Arrays.stream( ( (PsiClass) mapperConfigReference ).getMethods() );
    }

    /**
     * Find a candidate that this mapping method can inherit from, only if it is the only one possible.
     *
     * @param mappingMethod the mapping method to find a belonging method
     * @param candidates mapping methods that possibly hold the method to inherit from
     * @param inheritConfigurationAnnotation the {@link org.mapstruct.InheritConfiguration} annotation
     * @return a mapping method to inherit from, only if it is the only possible one found.
     */
    public static Optional<PsiMethod> findSingleMatchingInheritMappingMethod(
        @NotNull PsiMethod mappingMethod,
        @NotNull Stream<PsiMethod> candidates,
        @NotNull PsiAnnotation inheritConfigurationAnnotation) {

        String inheritConfigurationName = getStringAttributeValue( inheritConfigurationAnnotation, "name" );

        List<PsiMethod> matchingCandidates = candidates
            .filter( candidate -> isNotTheSameMethod( mappingMethod, candidate ) )
            .filter( candidate -> matchesNameWhenNameIsDefined( inheritConfigurationName, candidate ) )
            .filter( candidate -> canInheritFrom( mappingMethod, candidate ) )
            .collect( Collectors.toList() );

        if ( matchingCandidates.size() != 1 ) {
            return Optional.empty();
        }

        return matchingCandidates.stream().findFirst();
    }

    private static boolean isNotTheSameMethod(PsiMethod mappingMethod, PsiMethod candidate) {

        return !( mappingMethod.equals( candidate ) );
    }

    private static boolean matchesNameWhenNameIsDefined(String inheritConfigurationName, PsiMethod candidate) {

        if ( inheritConfigurationName == null || inheritConfigurationName.isEmpty() ) {
            return true;
        }

        return candidate.getName().equals( inheritConfigurationName );
    }

    /**
     * simplified version of <code>org.mapstruct.ap.internal.model.source.SourceMethod#canInheritFrom</code>
     */
    public static boolean canInheritFrom(PsiMethod mappingMethod, PsiMethod candidate) {

        PsiType targetType = findTargetTypeOfMappingMethod( mappingMethod );

        return targetType != null &&
            candidate.getBody() == null &&
            candidate.getReturnType() != null && candidate.getReturnType().isAssignableFrom( targetType )
            && allParametersAreAssignable( mappingMethod.getParameterList(), candidate.getParameterList() );
    }

    @Nullable
    private static PsiType findTargetTypeOfMappingMethod(PsiMethod mappingMethod) {

        PsiType targetType = mappingMethod.getReturnType();

        if ( !PsiType.VOID.equals( targetType ) ) {
            return targetType;
        }

        return Stream.of( mappingMethod.getParameterList().getParameters() )
            .filter( MapstructUtil::isMappingTarget )
            .findFirst()
            .map( PsiParameter::getType )
            .orElse( null );
    }

    private static boolean allParametersAreAssignable(PsiParameterList inheritParameters,
                                                      PsiParameterList candidateParameters) {

        if ( inheritParameters == null || candidateParameters == null || inheritParameters.isEmpty() ||
            candidateParameters.isEmpty() ) {
            return false;
        }

        List<PsiParameter> fromParams = Arrays.stream( inheritParameters.getParameters() )
            .filter( p -> !isMappingTarget( p ) )
            .collect( Collectors.toList() );

        List<PsiParameter> toParams = Arrays.asList( candidateParameters.getParameters() );

        return allParametersAreAssignable( fromParams, toParams );
    }

    /**
     * psi-modified copy of <code>org.mapstruct.ap.internal.model.source.SourceMethod#allParametersAreAssignable</code>
     */
    private static boolean allParametersAreAssignable(List<PsiParameter> fromParams, List<PsiParameter> toParams) {
        if ( fromParams.size() == toParams.size() ) {
            Set<PsiParameter> unaccountedToParams = new HashSet<>( toParams );

            for ( PsiParameter fromParam : fromParams ) {
                // each fromParam needs at least one match, and all toParam need to be accounted for at the end
                boolean hasMatch = false;
                for ( PsiParameter toParam : toParams ) {
                    if ( toParam.getType().isAssignableFrom( fromParam.getType() ) ) {
                        unaccountedToParams.remove( toParam );
                        hasMatch = true;
                    }
                }

                if ( !hasMatch ) {
                    return false;
                }
            }

            return unaccountedToParams.isEmpty();
        }

        return false;
    }
}
