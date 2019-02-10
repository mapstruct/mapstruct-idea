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
        Pair<PsiClass, PsiType> classAndType = resolveBuilderOrSelfClass( psiType, builderSupportPresent );
        if ( classAndType == null ) {
            return Stream.empty();
        }

        PsiClass psiClass = classAndType.getFirst();
        PsiType typeToUse = classAndType.getSecond();
        Set<PsiMethod> overriddenMethods = new HashSet<>();
        List<Pair<PsiMethod, PsiSubstitutor>> publicSetters = new ArrayList<>();
        for ( Pair<PsiMethod, PsiSubstitutor> pair : psiClass.getAllMethodsAndTheirSubstitutors() ) {
            PsiMethod method = pair.getFirst();
            boolean isSetter = builderSupportPresent ?
                MapstructUtil.isSetterOrFluentSetter( method, typeToUse ) :
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
     * Resolve the builder or self class for the {@code psiType}.
     *
     * @param psiType the type for which the {@link PsiClass} needs to be resolved
     * @param builderSupportPresent whether MapStruct (1.3) with builder support is present
     *
     * @return the pair containing the {@link PsiClass} and the corresponding {@link PsiType}
     */
    public static Pair<PsiClass, PsiType> resolveBuilderOrSelfClass(@NotNull PsiType psiType,
        boolean builderSupportPresent) {
        PsiClass psiClass = PsiUtil.resolveClassInType( psiType );
        if ( psiClass == null ) {
            return null;
        }
        PsiType typeToUse = psiType;

        if ( builderSupportPresent ) {
            for ( PsiMethod classMethod : psiClass.getMethods() ) {
                if ( MapstructUtil.isPossibleBuilderCreationMethod( classMethod, typeToUse ) &&
                    hasBuildMethod( classMethod.getReturnType(), psiType ) ) {
                    typeToUse = classMethod.getReturnType();
                    break;
                }
            }
        }

        psiClass = PsiUtil.resolveClassInType( typeToUse );
        return psiClass == null ? null : Pair.createNonNull( psiClass, typeToUse );
    }

    /**
     * Check if the {@code builderType} has a build method for the {@code type}
     *
     * @param builderType the type of the builder that should be checked
     * @param type the type for which a build method is searched for
     *
     * @return {@code true} if the builder type has a build method for the type
     */
    private static boolean hasBuildMethod(@Nullable PsiType builderType, @NotNull PsiType type) {
        if ( builderType == null ||
            builderType.getCanonicalText().startsWith( "java." ) ||
            builderType.getCanonicalText().startsWith( "javax." ) ) {
            return false;
        }

        PsiClass builderClass = PsiUtil.resolveClassInType( builderType );
        if ( builderClass == null ) {
            return false;
        }

        for ( PsiMethod buildMethod : builderClass.getAllMethods() ) {
            if ( MapstructUtil.isBuildMethod( buildMethod, type ) ) {
                return true;
            }
        }

        return false;
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
