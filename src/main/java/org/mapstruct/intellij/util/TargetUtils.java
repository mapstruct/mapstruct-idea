/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypes;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.codeInsight.AnnotationUtil.findAnnotation;
import static com.intellij.codeInsight.AnnotationUtil.getBooleanAttributeValue;
import static org.mapstruct.intellij.util.MapstructAnnotationUtils.findAllDefinedMappingAnnotations;
import static org.mapstruct.intellij.util.MapstructAnnotationUtils.findMapperConfigReference;
import static org.mapstruct.intellij.util.MapstructUtil.MAPPER_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.canDescendIntoType;
import static org.mapstruct.intellij.util.MapstructUtil.isInheritInverseConfiguration;
import static org.mapstruct.intellij.util.MapstructUtil.isMapper;
import static org.mapstruct.intellij.util.MapstructUtil.isMapperConfig;
import static org.mapstruct.intellij.util.MapstructUtil.publicFields;

/**
 * Utils for working with target properties (extracting targets for MapStruct).
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
        if ( psiType == null || PsiTypes.voidType().equalsToText( psiType.getCanonicalText() ) ) {
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
     * Extract all public write accessors (public setters and fields)
     * with their psi substitutors from the given {@code psiType}
     *
     * @param psiType to use to extract the accessors
     * @param mapStructVersion the MapStruct project version
     * @param mappingMethod the mapping method
     *
     * @return a stream that holds all public write accessors for the given {@code psiType}
     */
    public static Map<String, Pair<? extends PsiElement, PsiSubstitutor>> publicWriteAccessors(@NotNull PsiType psiType,
        MapStructVersion mapStructVersion, MapstructUtil mapstructUtil, PsiMethod mappingMethod) {
        boolean builderSupportPresent = mapStructVersion.isBuilderSupported();
        Pair<PsiClass, TargetType> classAndType = resolveBuilderOrSelfClass(
            psiType,
            builderSupportPresent && isBuilderEnabled( mappingMethod )
        );
        if ( classAndType == null ) {
            return Collections.emptyMap();
        }

        Map<String, Pair<? extends PsiElement, PsiSubstitutor>> publicWriteAccessors = new LinkedHashMap<>();

        PsiClass psiClass = classAndType.getFirst();
        TargetType targetType = classAndType.getSecond();
        PsiType typeToUse = targetType.type();

        publicWriteAccessors.putAll( publicSetters( psiClass, typeToUse, mapstructUtil,
            builderSupportPresent && isBuilderEnabled( mappingMethod ) ) );
        publicWriteAccessors.putAll( publicFields( psiClass ) );

        if ( mapStructVersion.isConstructorSupported() && !targetType.builder() ) {
            publicWriteAccessors.putAll( constructorParameters( psiClass ) );
        }

        return publicWriteAccessors;
    }

    /**
     * Whether builder is enabled for the mapping method with the mapstruct version
     *
     * @param mapStructVersion the MapStruct project version
     * @param mappingMethod the mapping method
     *
     * @return {@code true} if builder can be used for the mapping method
     */

    public static boolean isBuilderEnabled(MapStructVersion mapStructVersion, PsiMethod mappingMethod) {
        if ( mapStructVersion.isBuilderSupported() ) {
            return isBuilderEnabled( mappingMethod );
        }

        return false;
    }

    /**
     * Whether builder is enabled for the mapping method
     *
     * @param mappingMethod the mapping method
     *
     * @return {@code true} if builder can be used for the mapping method
     */
    public static boolean isBuilderEnabled(@Nullable PsiMethod mappingMethod) {
        Optional<Boolean> disableBuilder = findDisableBuilder( mappingMethod, MapstructUtil.BEAN_MAPPING_FQN );

        if ( disableBuilder.isEmpty() && mappingMethod != null ) {
            PsiAnnotation mapperAnnotation = findAnnotation(
                mappingMethod.getContainingClass(),
                MAPPER_ANNOTATION_FQN
            );
            disableBuilder = findDisabledBuilder( mapperAnnotation );

            if ( disableBuilder.isEmpty() && mapperAnnotation != null ) {
                disableBuilder = findDisableBuilder(
                    findMapperConfigReference( mapperAnnotation ),
                    MapstructUtil.MAPPER_CONFIG_ANNOTATION_FQN
                );
            }
        }

        return !disableBuilder.orElse( false );
    }

    private static Optional<Boolean> findDisableBuilder(@Nullable PsiModifierListOwner listOwner,
                                                        String annotationName) {
        PsiAnnotation requestedAnnotation = findAnnotation( listOwner, true, annotationName );
        return findDisabledBuilder( requestedAnnotation );
    }

    private static Optional<Boolean> findDisabledBuilder(@Nullable PsiAnnotation requestedAnnotation) {
        if ( requestedAnnotation != null ) {
            PsiAnnotationMemberValue builderValue = requestedAnnotation.findDeclaredAttributeValue( "builder" );
            if ( builderValue instanceof PsiAnnotation builderAnnotation ) {
                Boolean disableBuilder = getBooleanAttributeValue( builderAnnotation, "disableBuilder" );
                return Optional.ofNullable( disableBuilder );
            }
        }

        return Optional.empty();
    }

    private static Map<String, Pair<PsiParameter, PsiSubstitutor>> constructorParameters(@NotNull PsiClass psiClass) {
        PsiMethod constructor = resolveMappingConstructor( psiClass );
        if ( constructor == null || !constructor.hasParameters() ) {
            return Collections.emptyMap();
        }

        Map<String, Pair<PsiParameter, PsiSubstitutor>> constructorParameters = new HashMap<>();

        for ( PsiParameter parameter : constructor.getParameterList().getParameters() ) {
            constructorParameters.put( parameter.getName(), Pair.create( parameter, PsiSubstitutor.EMPTY ) );
        }

        return constructorParameters;
    }

    /**
     * Find the constructor that the code generation will use when mapping the psiClass.
     *
     * @param psiClass the class for which the constructor should be found
     * @return the constructor or {@code null} is there is no constructor that the mapping will use
     */
    public static PsiMethod resolveMappingConstructor(@NotNull PsiClass psiClass) {

        PsiMethod[] constructors = psiClass.getConstructors();
        if ( constructors.length == 0 ) {
            return null;
        }

        if ( constructors.length == 1 ) {
            PsiMethod constructor = constructors[0];
            return !constructor.hasModifier( JvmModifier.PRIVATE ) ? constructor : null;
        }

        List<PsiMethod> accessibleConstructors = new ArrayList<>( constructors.length );

        for ( PsiMethod constructor : constructors ) {
            if ( constructor.hasModifier( JvmModifier.PRIVATE ) ) {
                // private constructors are ignored
                continue;
            }
            if ( !constructor.hasParameters() ) {
                // If there is an empty constructor then that constructor should be used
                return constructor;
            }

            accessibleConstructors.add( constructor );
        }

        if ( accessibleConstructors.size() == 1 ) {
            // If there is only one accessible constructor then use that one
            return accessibleConstructors.get( 0 );
        }

        // If there are more accessible constructor then look for one annotated with @Default.
        // Otherwise return null
        for ( PsiMethod constructor : accessibleConstructors ) {
            for ( PsiAnnotation annotation : constructor.getAnnotations() ) {
                PsiJavaCodeReferenceElement nameReferenceElement = annotation.getNameReferenceElement();
                if ( nameReferenceElement != null && "Default".equals( nameReferenceElement.getReferenceName() ) ) {
                    // If there is a constructor annotated with an annotation named @Default
                    // then we should use that one
                    return constructor;
                }
            }
        }

        return null;
    }

    /**
     * Extract all public setters with their psi substitutors from the given {@code psiClass}
     *
     * @param psiClass to use to extract the setters
     * @param typeToUse the type in which the methods are located (needed for fluent setters)
     * @param builderSupportPresent whether MapStruct (1.3) with builder support is present
     *
     * @return a stream that holds all public setters for the given {@code psiType}
     */
    private static Map<String, Pair<? extends PsiMember, PsiSubstitutor>> publicSetters(@NotNull PsiClass psiClass,
        @NotNull PsiType typeToUse, MapstructUtil mapstructUtil,
        boolean builderSupportPresent) {
        Set<PsiMethod> overriddenMethods = new HashSet<>();
        Map<String, Pair<? extends PsiMember, PsiSubstitutor>> publicSetters = new LinkedHashMap<>();
        for ( Pair<PsiMethod, PsiSubstitutor> pair : psiClass.getAllMethodsAndTheirSubstitutors() ) {
            PsiMethod method = pair.getFirst();
            if ( method.isConstructor() ) {
                continue;
            }
            String propertyName = extractPublicSetterPropertyName(
                method,
                typeToUse,
                pair.getSecond(),
                mapstructUtil,
                builderSupportPresent
            );

            if ( propertyName != null &&
                !overriddenMethods.contains( method ) ) {
                // If this is a public setter then populate its overridden methods and use it
                overriddenMethods.addAll( Arrays.asList( method.findSuperMethods() ) );
                publicSetters.put( propertyName, pair );
            }
        }

        return publicSetters;
    }

    public static boolean isMethodReturnTypeAssignableToCollectionOrMap(@NotNull PsiMethod method) {
        PsiType returnType = method.getReturnType();
        if ( returnType == null ) {
            return false;
        }
        if ( getTypeByName( "java.util.Collection", method ).isAssignableFrom( returnType ) ) {
            return true;
        }
        return getTypeByName( "java.util.Map", method ).isAssignableFrom( returnType );
    }

    private static PsiClassType getTypeByName(@NotNull String qName, @NotNull PsiMethod method) {
        return PsiType.getTypeByName(
            qName,
            method.getProject(),
            method.getResolveScope()
        );
    }

    @Nullable
    private static String extractPublicSetterPropertyName(PsiMethod method, @NotNull PsiType typeToUse,
                                                          PsiSubstitutor psiTypeSubstitutor,
                                                          MapstructUtil mapstructUtil, boolean builderSupportPresent) {
        if (!MapstructUtil.isPublicNonStatic( method )) {
            // If the method is not public then there is no property
            return null;
        }
        String methodName = method.getName();
        int parametersCount = method.getParameterList().getParametersCount();

        if ( parametersCount == 0 && methodName.startsWith( "get" ) &&
            isMethodReturnTypeAssignableToCollectionOrMap( method ) ) {

            // If the methode returns a collection
            return Introspector.decapitalize( methodName.substring( 3 ) );
        }
        if (parametersCount != 1) {
            // If the method does not have 1 parameter
            return null;
        }

        // This logic is aligned with the DefaultAccessorNamingStrategy
        if ( builderSupportPresent && mapstructUtil.isFluentSetter( method, typeToUse, psiTypeSubstitutor ) ) {
            if ( methodName.startsWith( "set" )
                && methodName.length() > 3
                && Character.isUpperCase( methodName.charAt( 3 ) ) ) {
                return Introspector.decapitalize( methodName.substring( 3 ) );
            }
            return methodName;
        }
        if ( methodName.startsWith( "set" ) ) {
            return Introspector.decapitalize( methodName.substring( 3 ) );
        }
        return null;
    }

    /**
     * Resolve the builder or self class for the {@code psiType}.
     *
     * @param psiType the type for which the {@link PsiClass} needs to be resolved
     * @param builderEnabled whether MapStruct (1.3) with builder support is present
     *
     * @return the pair containing the {@link PsiClass} and the corresponding {@link PsiType}
     */
    public static Pair<PsiClass, TargetType> resolveBuilderOrSelfClass(@NotNull PsiType psiType,
                                                                       boolean builderEnabled) {
        PsiClass psiClass = PsiUtil.resolveClassInType( psiType );
        if ( psiClass == null ) {
            return null;
        }
        TargetType targetType = TargetType.defaultType( psiType );

        if ( builderEnabled ) {
            for ( PsiMethod classMethod : psiClass.getMethods() ) {
                if ( MapstructUtil.isPossibleBuilderCreationMethod( classMethod, targetType.type() ) &&
                    hasBuildMethod( classMethod.getReturnType(), psiType ) ) {
                    targetType = TargetType.builder( classMethod.getReturnType() );
                    break;
                }
            }
        }

        psiClass = PsiUtil.resolveClassInType( targetType.type() );
        return psiClass == null ? null : Pair.createNonNull( psiClass, targetType );
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

        for ( Pair<PsiMethod, PsiSubstitutor> pair : builderClass.getAllMethodsAndTheirSubstitutors() ) {
            PsiMethod buildMethod = pair.getFirst();
            PsiSubstitutor buildMethodSubstitutor = pair.getSecond();

            if ( MapstructUtil.isBuildMethod( buildMethod, buildMethodSubstitutor, type ) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Find all defined {@link org.mapstruct.Mapping#target()} for the given method
     *
     * @param owner that needs to be checked
     * @param mapStructVersion the MapStruct project version
     *
     * @return see description
     */
    @NotNull
    public static Stream<String> findAllDefinedMappingTargets(@NotNull PsiModifierListOwner owner,
        MapStructVersion mapStructVersion) {
        return findAllDefinedMappingAnnotations( owner, mapStructVersion )
            .map( psiAnnotation -> AnnotationUtil.getDeclaredStringAttributeValue( psiAnnotation, "target" ) )
            .filter( Objects::nonNull )
            .filter( s -> !s.isEmpty() );
    }

    /**
     * Find all implicit source properties for all targets mapping to the current target, i.e. ".".
     *
     * @param method that needs to be checked
     * @param mapStructVersion the MapStruct project version
     *
     * @return see description
     */
    public static Stream<String> findAllSourcePropertiesForCurrentTarget(@NotNull PsiMethod method,
        MapStructVersion mapStructVersion) {
        return findAllDefinedMappingAnnotations( method, mapStructVersion )
            .filter( psiAnnotation -> ".".equals( AnnotationUtil.getDeclaredStringAttributeValue(
                psiAnnotation,
                "target"
            ) ) )
            .map( psiAnnotation -> psiAnnotation.findDeclaredAttributeValue(  "source" ) )
            .filter( Objects::nonNull )
            .map( ReferenceProvidersRegistry::getReferencesFromProviders )
            .filter( references -> references.length > 0 )
            .map( references -> references[references.length - 1].resolve() )
            .flatMap( element -> SourceUtils.publicReadAccessors( element ).keySet().stream() );
    }

    /**
     * Find all target properties from the {@code targetClass} that can be used for mapping
     *
     * @param targetType that needs to be used
     * @param mapStructVersion the MapStruct project version
     * @param mappingMethod that needs to be checked
     *
     * @return all target properties for the given {@code targetClass}
     */
    public static Set<String> findAllTargetProperties(@NotNull PsiType targetType, MapStructVersion mapStructVersion,
                                                      MapstructUtil mapstructUtil, PsiMethod mappingMethod) {
        return publicWriteAccessors( targetType, mapStructVersion, mapstructUtil, mappingMethod ).keySet();
    }

    /**
     * @param method the method to be used
     *
     * @return the target class for the inspection, or {@code null} if no inspection needs to be performed
     */
    @Nullable
    public static PsiType getTargetType( @NotNull PsiMethod method) {
        if ( !method.getModifierList().hasModifierProperty( PsiModifier.ABSTRACT ) ) {
            return null;
        }

        if ( isInheritInverseConfiguration( method ) ) {
            return null;
        }
        PsiClass containingClass = method.getContainingClass();

        if ( containingClass == null
                || method.getNameIdentifier() == null
                || !( isMapper( containingClass ) || isMapperConfig( containingClass ) ) ) {
            return null;
        }
        return getRelevantType( method );
    }

}
