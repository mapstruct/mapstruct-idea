/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection.inheritance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.intellij.util.MapStructVersion;

import static com.intellij.codeInsight.AnnotationUtil.findAnnotation;
import static com.intellij.codeInsight.AnnotationUtil.getStringAttributeValue;
import static org.mapstruct.intellij.util.MapstructAnnotationUtils.findMapperConfigClass;
import static org.mapstruct.intellij.util.MapstructUtil.INHERIT_CONFIGURATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.INHERIT_INVERSE_CONFIGURATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.MAPPER_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.MAPPER_CONFIG_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.SourceUtils.findAllDefinedMappingSources;
import static org.mapstruct.intellij.util.TargetUtils.findAllDefinedMappingTargets;
import static org.mapstruct.intellij.util.TargetUtils.getRelevantType;

/**
 * Utils for working with inherited configurations based on {@link org.mapstruct.InheritConfiguration}
 */
public class InheritConfigurationUtils {

    private InheritConfigurationUtils() {
    }

    public static Stream<String> findInheritedTargetProperties(@NotNull PsiMethod mappingMethod,
                                                               MapStructVersion mapStructVersion) {

        PsiClass containingClass = mappingMethod.getContainingClass();

        if ( containingClass == null ) {
            return Stream.empty();
        }

        PsiAnnotation mapperAnnotation = findAnnotation( containingClass, MAPPER_ANNOTATION_FQN );

        if ( mapperAnnotation == null ) {
            return Stream.empty();
        }

        List<SourceMethod> availableMethods = findMappingMethodsFromInheritScope( containingClass, mapperAnnotation )
            .map( SourceMethod::new )
            .collect( Collectors.toList() );
        List<SourceMethod> prototypeMethods = findPrototypeMappingMethods( mapperAnnotation )
            .map( SourceMethod::new )
            .collect( Collectors.toList() );
        MappingInheritanceStrategy inheritanceStrategy = findAnnotatedMappingInheritanceStrategy( mapperAnnotation );

        InheritConfigurationContext ctx = new InheritConfigurationContext(
            mapStructVersion,
            availableMethods,
            prototypeMethods,
            new ArrayList<>(),
            inheritanceStrategy
        );

        mergeInheritedOptions( new SourceMethod( mappingMethod ), ctx );

        return ctx.mappedTargets.stream();
    }

    /**
     * Merges inherited properties in a recursive way.
     *
     * @param method method to merge properties by inheritance
     * @param ctx context that helps with inheritance
     * @see "org.mapstruct.ap.internal.processor.MapperCreationProcessor#mergeInheritedOptions"
     */
    private static void mergeInheritedOptions(@NotNull SourceMethod method, @NotNull InheritConfigurationContext ctx) {

        if ( ctx.initializingMethods.contains( method ) ) {
            // cycle detected
            ctx.initializingMethods.add( method );
            return;
        }

        ctx.initializingMethods.add( method );

        PsiType targetType = getRelevantType( method.method );

        Set<SourceMethod> applicablePrototypeMethods = getApplicablePrototypeMethods(
            method,
            targetType,
            ctx.prototypeMethods
        );

        SourceMethod forwardTemplateMethod =
            getForwardTemplateMethod(
                join( ctx.availableMethods, applicablePrototypeMethods ),
                method,
                targetType,
                ctx
            );

        Set<SourceMethod> applicableReversePrototypeMethods = getApplicableReversePrototypeMethods(
            method,
            targetType,
            ctx.prototypeMethods
        );

        SourceMethod inverseTemplateMethod =
            getInverseTemplateMethod(
                join( ctx.availableMethods, applicableReversePrototypeMethods ),
                method,
                targetType,
                ctx
            );

        // apply defined (@InheritConfiguration, @InheritInverseConfiguration) mappings
        if ( forwardTemplateMethod != null ) {
            findAllDefinedMappingTargets( forwardTemplateMethod.method, ctx.mapStructVersion )
                .forEach( ctx.mappedTargets::add );
        }
        if ( inverseTemplateMethod != null ) {
            findAllDefinedMappingSources( inverseTemplateMethod.method, ctx.mapStructVersion )
                .forEach( ctx.mappedTargets::add );
        }

        // apply auto inherited options
        if ( ctx.inheritanceStrategy != MappingInheritanceStrategy.EXPLICIT ) {

            // but... there should not be an @InheritedConfiguration
            boolean applyForward = ctx.inheritanceStrategy == MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG ||
                ctx.inheritanceStrategy == MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG;
            if ( forwardTemplateMethod == null && applyForward ) {
                if ( applicablePrototypeMethods.size() == 1 ) {
                    findAllDefinedMappingTargets( first( applicablePrototypeMethods ).method, ctx.mapStructVersion )
                        .forEach( ctx.mappedTargets::add );
                }
            }

            // or no @InheritInverseConfiguration
            boolean applyReverse = ctx.inheritanceStrategy == MappingInheritanceStrategy.AUTO_INHERIT_ALL_FROM_CONFIG ||
                ctx.inheritanceStrategy == MappingInheritanceStrategy.AUTO_INHERIT_REVERSE_FROM_CONFIG;
            if ( inverseTemplateMethod == null && applyReverse ) {
                if ( applicableReversePrototypeMethods.size() == 1 ) {
                    findAllDefinedMappingSources(
                            first( applicableReversePrototypeMethods ).method,
                            ctx.mapStructVersion
                        )
                        .forEach( ctx.mappedTargets::add );
                }
            }
        }

        method.fullyInitialized = true;
    }

    private static SourceMethod getInverseTemplateMethod(
        Set<SourceMethod> rawMethods, SourceMethod mappingMethod,
        PsiType targetType,
        InheritConfigurationContext ctx
    ) {

        SourceMethod resultMethod = null;

        PsiAnnotation inverseConfiguration = findAnnotation( mappingMethod.method, INHERIT_INVERSE_CONFIGURATION_FQN );

        if ( inverseConfiguration != null ) {

            // method is configured as being inverse method, collect candidates
            List<SourceMethod> candidates = new ArrayList<>();
            for ( SourceMethod oneMethod : rawMethods ) {
                if ( mappingMethod.inverses( oneMethod.method, targetType ) ) {
                    candidates.add( oneMethod );
                }
            }

            resultMethod = getResultMethod( inverseConfiguration, candidates );
        }

        return extractInitializedOptions( resultMethod, ctx );
    }

    private static SourceMethod getForwardTemplateMethod(
        Set<SourceMethod> rawMethods,
        SourceMethod mappingMethod,
        PsiType targetType,
        InheritConfigurationContext ctx
    ) {

        SourceMethod resultMethod = null;

        PsiAnnotation inheritConfiguration = findAnnotation( mappingMethod.method, INHERIT_CONFIGURATION_FQN );

        if ( inheritConfiguration != null ) {

            List<SourceMethod> candidates = new ArrayList<>();
            for ( SourceMethod oneMethod : rawMethods ) {
                // method must be similar but not equal
                if ( mappingMethod.canInheritFrom( oneMethod.method, targetType ) &&
                    !( oneMethod.equals( mappingMethod ) ) ) {
                    candidates.add( oneMethod );
                }
            }

            resultMethod = getResultMethod( inheritConfiguration, candidates );
        }

        return extractInitializedOptions( resultMethod, ctx );
    }

    private static SourceMethod getResultMethod(
        PsiAnnotation inheritOrInverseConfigurationAnnotation,
        List<SourceMethod> candidates) {

        String name = getStringAttributeValue( inheritOrInverseConfigurationAnnotation, "name" );
        if ( candidates.size() == 1 ) {
            // no ambiguity: if no configuredBy is specified, or configuredBy specified and match
            SourceMethod method = first( candidates );
            if ( name == null || name.isEmpty() ) {
                return method;
            }
            else if ( method.method.getName().equals( name ) ) {
                return method;
            }
        }
        else if ( candidates.size() > 1 ) {
            // ambiguity: find a matching method that matches configuredBy

            List<SourceMethod> nameFilteredCandidates = new ArrayList<>();
            for ( SourceMethod candidate : candidates ) {
                if ( candidate.method.getName().equals( name ) ) {
                    nameFilteredCandidates.add( candidate );
                }
            }

            if ( nameFilteredCandidates.size() == 1 ) {
                return first( nameFilteredCandidates );
            }
        }
        return null;
    }

    private static SourceMethod extractInitializedOptions(SourceMethod method, InheritConfigurationContext ctx) {
        if ( method != null ) {
            if ( !method.fullyInitialized ) {
                mergeInheritedOptions( method, ctx );
            }
            return method;
        }

        return null;
    }

    private static Set<SourceMethod> getApplicableReversePrototypeMethods(SourceMethod mappingMethod,
                                                                          PsiType targetType,
                                                                          List<SourceMethod> prototypeMethods) {
        return prototypeMethods.stream()
            .filter( candidate -> mappingMethod.inverses( candidate.method, targetType ) )
            .collect( Collectors.toUnmodifiableSet() );
    }

    private static Set<SourceMethod> getApplicablePrototypeMethods(SourceMethod mappingMethod,
                                                                   PsiType targetType,
                                                                   List<SourceMethod> prototypeMethods) {

        return prototypeMethods.stream()
            .filter( candidate -> mappingMethod.canInheritFrom( candidate.method, targetType ) )
            .collect( Collectors.toUnmodifiableSet() );
    }

    /**
     * Find all mapping methods (regardless of their type and parameter) that are in the scope to be inherited from:
     * <ul>
     *     <li>local methods</li>
     *     <li>methods of mappers extending from</li>
     *     <li>methods from {@link org.mapstruct.MapperConfig}</li>
     * </ul>
     *
     * @param containingClass containing class of the mapping method to check
     * @param mapperAnnotation the mapper annotation of the containing class
     * @return possible mapping methods to be inherited from
     */
    private static Stream<PsiMethod> findMappingMethodsFromInheritScope(@NotNull PsiClass containingClass,
                                                                        @NotNull PsiAnnotation mapperAnnotation) {

        Stream<PsiMethod> localAndParentMethods = Arrays.stream( containingClass.getAllMethods() );

        Stream<PsiMethod> mapperConfigMethods = findPrototypeMappingMethods( mapperAnnotation );

        return Stream.concat( localAndParentMethods, mapperConfigMethods );
    }

    private static Stream<PsiMethod> findPrototypeMappingMethods(@NotNull PsiAnnotation mapperAnnotation) {

        return findMapperConfigClass( mapperAnnotation )
            .map( mapperConfigClass -> Arrays.stream( mapperConfigClass.getMethods() ) )
            .orElseGet( Stream::empty );
    }

    public static MappingInheritanceStrategy findAnnotatedMappingInheritanceStrategy(
        @NotNull PsiAnnotation mapperAnnotation
    ) {

        return findMapperConfigClass( mapperAnnotation )
            .map( mapperConfigClass -> findAnnotation( mapperConfigClass, MAPPER_CONFIG_ANNOTATION_FQN ) )
            .map( psiAnnotation -> psiAnnotation.findDeclaredAttributeValue( "mappingInheritanceStrategy" ) )
            .filter( PsiReferenceExpression.class::isInstance )
            .map( PsiReferenceExpression.class::cast )
            .map( PsiReference::resolve )
            .filter( PsiEnumConstant.class::isInstance )
            .map( PsiEnumConstant.class::cast )
            .map( PsiField::getName )
            .map( InheritConfigurationUtils::tryParseMappingInheritanceStrategy )
            .orElse( MappingInheritanceStrategy.EXPLICIT );
    }

    private static MappingInheritanceStrategy tryParseMappingInheritanceStrategy(String name) {
        try {
            return MappingInheritanceStrategy.valueOf( name );
        }
        catch ( IllegalArgumentException e ) {
            return null;
        }
    }

    @NotNull
    private static <T> Set<T> join(Collection<T> a, Collection<T> b) {
        return Stream.concat( a.stream(), b.stream() ).collect( Collectors.toUnmodifiableSet() );
    }

    private static <T> T first(Collection<T> collection) {
        return collection.stream().findFirst().orElse( null );
    }

    private static class InheritConfigurationContext {

        private final MapStructVersion mapStructVersion;
        @NotNull
        private final List<SourceMethod> availableMethods;
        @NotNull
        private final List<SourceMethod> prototypeMethods;
        @NotNull
        private final List<SourceMethod> initializingMethods;
        private final MappingInheritanceStrategy inheritanceStrategy;
        private final Set<String> mappedTargets = new HashSet<>();

        private InheritConfigurationContext(MapStructVersion mapStructVersion,
                                            @NotNull List<SourceMethod> availableMethods,
                                            @NotNull List<SourceMethod> prototypeMethods,
                                            @NotNull List<SourceMethod> initializingMethods,
                                            MappingInheritanceStrategy inheritanceStrategy) {
            this.mapStructVersion = mapStructVersion;
            this.availableMethods = availableMethods;
            this.prototypeMethods = prototypeMethods;
            this.initializingMethods = initializingMethods;
            this.inheritanceStrategy = inheritanceStrategy;
        }
    }
}
