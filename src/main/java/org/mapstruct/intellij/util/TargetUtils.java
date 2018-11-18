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
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.codeInsight.AnnotationUtil.findAnnotation;
import static com.intellij.codeInsight.AnnotationUtil.findDeclaredAttribute;
import static org.mapstruct.intellij.util.MapstructUtil.MAPPING_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.canDescendIntoType;

/**
 * Utils for working with target properties (extracting targets  for MapStruct).
 *
 * @author Filip Hrisafov
 */
public class TargetUtils {

    private TargetUtils() {
    }

    /**
     * Get the relevant class for the {@code mappingMethod}. This can be the return of the method, the parameter
     * annotated with {@link org.mapstruct.MappingTarget}, or {@code null}
     *
     * @param mappingMethod the mapping method
     *
     * @return the target class for the given {@code mappingMethod}
     */
    @Nullable
    public static PsiType getRelevantType(@NotNull PsiMethod mappingMethod) {
        //TODO here we need to take into consideration both with @MappingTarget and return,
        // returning an interface etc.
        if ( !canDescendIntoType( mappingMethod.getReturnType() ) ) {
            return null;
        }
        PsiType psiType = mappingMethod.getReturnType();
        if ( psiType == null || PsiType.VOID.equalsToText( psiType.getCanonicalText() ) ) {
            psiType = Stream.of( mappingMethod.getParameterList().getParameters() )
                .filter( MapstructUtil::isMappingTarget )
                .findAny()
                .map( PsiParameter::getType )
                .filter( MapstructUtil::canDescendIntoType )
                .orElse( null );
        }
        return psiType;
    }

    /**
     * Extract all public setters with their psi substitutors from the given {@code psiType}
     *
     * @param psiType to use to extract the setters
     * @param builderSupportPresent whether MapStruct (1.3) with builder suppot is present
     *
     * @return a stream that holds all public setters for the given {@code psiType}
     */
    public static Stream<Pair<PsiMethod, PsiSubstitutor>> publicSetters(@NotNull PsiType psiType,
        boolean builderSupportPresent) {
        PsiClass psiClass = PsiUtil.resolveClassInType( psiType );
        if ( psiClass == null ) {
            return Stream.empty();
        }
        Set<PsiMethod> overriddenMethods = new HashSet<>();
        List<Pair<PsiMethod, PsiSubstitutor>> publicSetters = new ArrayList<>();
        for ( Pair<PsiMethod, PsiSubstitutor> pair : psiClass.getAllMethodsAndTheirSubstitutors() ) {
            PsiMethod method = pair.getFirst();
            boolean isSetter = builderSupportPresent ?
                MapstructUtil.isSetterOrFluentSetter( method, psiType ) :
                MapstructUtil.isSetter( method );
            if ( isSetter && MapstructUtil.isPublic( method ) &&
                !overriddenMethods.contains( method ) ) {
                // If this is a public setter then populate its overridden methods and use it
                overriddenMethods.addAll( Arrays.asList( method.findSuperMethods() ) );
                publicSetters.add( pair );
            }
        }

        return publicSetters.stream();
    }

    /**
     * Find all defined {@link org.mapstruct.Mapping#target()} for the given method
     *
     * @param method that needs to be checked
     *
     * @return see description
     */
    public static Stream<String> findAllDefinedMappingTargets(@NotNull PsiMethod method) {
        //TODO cache
        PsiAnnotation mappings = findAnnotation( method, true, MapstructUtil.MAPPINGS_ANNOTATION_FQN );
        Stream<PsiAnnotation> mappingsAnnotations;
        if ( mappings == null ) {
            mappingsAnnotations = Stream.of( method.getModifierList().getAnnotations() )
                .filter( TargetUtils::isMappingAnnotation );
        }
        else {
            //TODO maybe there is a better way to do this, but currently I don't have that much knowledge
            PsiNameValuePair mappingsValue = findDeclaredAttribute( mappings, null );
            if ( mappingsValue != null && mappingsValue.getValue() instanceof PsiArrayInitializerMemberValue ) {
                mappingsAnnotations = Stream.of( ( (PsiArrayInitializerMemberValue) mappingsValue.getValue() )
                    .getInitializers() )
                    .filter( TargetUtils::isMappingPsiAnnotation )
                    .map( memberValue -> (PsiAnnotation) memberValue );
            }
            else if ( mappingsValue != null && mappingsValue.getValue() instanceof PsiAnnotation ) {
                mappingsAnnotations = Stream.of( (PsiAnnotation) mappingsValue.getValue() );
            }
            else {
                mappingsAnnotations = Stream.empty();
            }
        }

        return mappingsAnnotations
            .map( psiAnnotation -> psiAnnotation.findDeclaredAttributeValue( "target" ) )
            .filter( Objects::nonNull )
            .map( ElementManipulators::getValueText )
            .filter( s -> !s.isEmpty() );
    }

    /**
     * @param memberValue that needs to be checked
     *
     * @return {@code true} if the {@code memberValue} is the {@link org.mapstruct.Mapping} {@link PsiAnnotation},
     * {@code false} otherwise
     */
    private static boolean isMappingPsiAnnotation(PsiAnnotationMemberValue memberValue) {
        return memberValue instanceof PsiAnnotation
            && TargetUtils.isMappingAnnotation( (PsiAnnotation) memberValue );
    }

    /**
     * @param psiAnnotation that needs to be checked
     *
     * @return {@code true} if the {@code psiAnnotation} is the {@link org.mapstruct.Mapping} annotation, {@code
     * false} otherwise
     */
    private static boolean isMappingAnnotation(PsiAnnotation psiAnnotation) {
        return Objects.equals( psiAnnotation.getQualifiedName(), MAPPING_ANNOTATION_FQN );
    }

    /**
     * Find all target properties from the {@code targetClass} that can be used for mapping
     *
     * @param targetType that needs to be used
     * @param builderSupportPresent whether MapStruct (1.3) with builder support is present
     *
     * @return all target properties for the given {@code targetClass}
     */
    public static Stream<String> findAllTargetProperties(@NotNull PsiType targetType, boolean builderSupportPresent) {
        return publicSetters( targetType, builderSupportPresent )
            .map( pair -> pair.getFirst() )
            .map( MapstructUtil::getPropertyName );
    }
}
