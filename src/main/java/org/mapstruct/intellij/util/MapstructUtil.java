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

import java.beans.Introspector;
import java.util.function.Function;
import java.util.stream.Stream;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiFormatUtil;
import com.intellij.psi.util.PsiFormatUtilBase;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

import static com.intellij.codeInsight.AnnotationUtil.findAnnotation;
import static com.intellij.codeInsight.AnnotationUtil.isAnnotated;

/**
 * @author Filip Hrisafov
 */
public final class MapstructUtil {

    /**
     * The FQN of the {@link Mapper} annotation.
     */
    public static final String MAPPER_ANNOTATION_FQN = Mapper.class.getName();
    /**
     * The FQN of the {@link MapperConfig} annotation.
     */
    public static final String MAPPER_CONFIG_ANNOTATION_FQN = MapperConfig.class.getName();
    /**
     * The FQN of the {@link Mapping} annotation.
     */
    public static final String MAPPING_ANNOTATION_FQN = Mapping.class.getName();
    static final String MAPPINGS_ANNOTATION_FQN = Mappings.class.getName();
    static final String VALUE_MAPPING_ANNOTATION_FQN = ValueMapping.class.getName();
    static final String VALUE_MAPPINGS_ANNOTATION_FQN = ValueMappings.class.getName();
    private static final String MAPPING_TARGET_ANNOTATION_FQN = MappingTarget.class.getName();
    //TODO maybe we need to include the 1.2.0-RC1 here
    private static final String CONTEXT_ANNOTATION_FQN = "org.mapstruct.Context";
    private static final String INHERIT_INVERSE_CONFIGURATION = InheritInverseConfiguration.class.getName();

    /**
     * Hide constructor.
     */
    private MapstructUtil() {
    }

    public static LookupElement asLookup(@NotNull Pair<PsiMethod, PsiSubstitutor> pair,
        Function<PsiMethod, PsiType> typeMapper) {
        PsiMethod method = pair.getFirst();
        PsiSubstitutor substitutor = pair.getSecond();

        String propertyName = getPropertyName( method );
        LookupElementBuilder builder = LookupElementBuilder.create( method, propertyName )
            .withIcon( PlatformIcons.VARIABLE_ICON )
            .withPresentableText( propertyName )
            .withTailText( PsiFormatUtil.formatMethod( method, substitutor,
                0,
                PsiFormatUtilBase.SHOW_NAME | PsiFormatUtilBase.SHOW_TYPE
            ) );
        final PsiType type = typeMapper.apply( method );
        if ( type != null ) {
            builder = builder.withTypeText( substitutor.substitute( type ).getPresentableText() );
        }

        return builder;
    }

    public static boolean isPublic(@NotNull PsiMethod method) {
        return method.hasModifierProperty( PsiModifier.PUBLIC );
    }

    public static boolean isSetter(@NotNull PsiMethod method) {
        if ( method.getParameterList().getParametersCount() != 1 ) {
            return false;
        }
        //TODO if we can use the AccessorNamingStrategy it would be awesome
        String methodName = method.getName();
        return methodName.startsWith( "set" );
    }

    public static boolean isGetter(@NotNull PsiMethod method) {
        if ( method.getParameterList().getParametersCount() != 0 ) {
            return false;
        }
        //TODO if we can use the AccessorNamingStrategy it would be awesome
        String methodName = method.getName();
        return ( methodName.startsWith( "get" ) && !methodName.equals( "getClass" )) || methodName.startsWith( "is" );
    }

    @NotNull
    @NonNls
    public static String getPropertyName(@NotNull PsiMethod method) {
        //TODO if we can use the AccessorNamingStrategy it would be awesome
        String methodName = method.getName();
        return getPropertyName( methodName );
    }

    @NotNull
    @NonNls
    public static String getPropertyName(@NotNull String methodName) {
        String name = "";
        if ( methodName.startsWith( "is" ) ) {
            name = methodName.substring( 2 );
        }
        else if ( methodName.length() > 2 ) {
            name = methodName.substring( 3 );
        }
        return Introspector.decapitalize( name );
    }

    /**
     * Check if the parameter is a Mapping Target parameter.
     *
     * @param psiParameter to be checked
     *
     * @return {@code true} if the parameter is a MappingTarget, {@code false} otherwise
     */
    public static boolean isMappingTarget(PsiParameter psiParameter) {
        return hasAnnotation( psiParameter, MAPPING_TARGET_ANNOTATION_FQN );
    }

    /**
     * Checks of the {@code psiClass} is annotated with {@link Mapper}.
     *
     * @param psiClass the class that needs to be checked
     *
     * @return {@code true} if the {@code psiClass} is annotated with {@link Mapper}, {@code false} otherwise
     */
    public static boolean isMapper(PsiClass psiClass) {
        return isAnnotated( psiClass, MAPPER_ANNOTATION_FQN, false );
    }

    /**
     * Checks of the {@code psiClass} is annotated with {@link MapperConfig}.
     *
     * @param psiClass the class that needs to be checked
     *
     * @return {@code true} if the {@code psiClass} is annotated with {@link MapperConfig}, {@code false} otherwise
     */
    public static boolean isMapperConfig(PsiClass psiClass) {
        return isAnnotated( psiClass, MAPPER_CONFIG_ANNOTATION_FQN, false );
    }

    /**
     * Checks if the {@code psiMethod} is a mapping method. A mapping method is a method that is annotated with one of:
     * <ul>
     * <li>{@link Mapping}</li>
     * <li>{@link Mappings}</li>
     * <li>{@link ValueMapping}</li>
     * <li>{@link ValueMappings}</li>
     * </ul>
     *
     * @param psiMethod
     *
     * @return
     */
    public static boolean isMappingMethod(PsiMethod psiMethod) {
        return isAnnotated( psiMethod, MAPPING_ANNOTATION_FQN, false )
            || isAnnotated( psiMethod, MAPPINGS_ANNOTATION_FQN, false )
            || isAnnotated( psiMethod, VALUE_MAPPING_ANNOTATION_FQN, false )
            || isAnnotated( psiMethod, VALUE_MAPPINGS_ANNOTATION_FQN, false );
    }

    /**
     * Checks if the parameter is a valid source parameter. A valid source parameter is a paremeter that is not a
     * {@code MappingTarget} or a {@code Context}.
     *
     * @param psiParameter to be checked
     *
     * @return {@code true} if the parameter is a valid source parameter, {@code false} otherwise
     */
    public static boolean isValidSourceParameter(PsiParameter psiParameter) {
        return !isMappingTarget( psiParameter ) && !isContextParameter( psiParameter );
    }

    /**
     * Checks if the parameter is a Context parameter.
     *
     * @param psiParameter to be checked
     *
     * @return {@code true} if the parameter is a Context parameter, {@code false} otherwise
     */
    private static boolean isContextParameter(PsiParameter psiParameter) {
        return hasAnnotation( psiParameter, CONTEXT_ANNOTATION_FQN );
    }

    /**
     * Checks if the parameter is annotated with the provided {@code annotation}.
     *
     * @param psiParameter the parameter on which we need to check for the annotation
     * @param annotation the annotation that we need to find
     *
     * @return {@code true} if the {@code psiParameter} is annotated with the {@code annotation}, {@code false}
     * otherwise
     */
    private static boolean hasAnnotation(PsiParameter psiParameter, String annotation) {
        PsiModifierList modifierList = psiParameter.getModifierList();
        return modifierList != null && modifierList.findAnnotation( annotation ) != null;
    }

    /**
     * Extract all valid source parameters from the provided {@code mappingMethod}
     *
     * @param mappingMethod the mapping method
     *
     * @return all source parameters from the provided {@code mappingMethod}
     */
    @NotNull
    public static PsiParameter[] getSourceParameters(@NotNull PsiMethod mappingMethod) {
        if ( mappingMethod.getParameterList().getParametersCount() == 0 ) {
            return PsiParameter.EMPTY_ARRAY;
        }
        return Stream.of( mappingMethod.getParameterList().getParameters() )
            .filter( MapstructUtil::isValidSourceParameter )
            .toArray( PsiParameter[]::new );
    }

    /**
     * Checks if MapStruct can descend into a type. MapStruct, cannot descend into following types:
     * <ul>
     * <li>An Array</li>
     * <li>An Iterable</li>
     * <li>A Map</li>
     * </ul>
     *
     * @param psiType the type to be checked
     *
     * @return {@code true} if MapStruct can descend into type
     */
    public static boolean canDescendIntoType(@Nullable PsiType psiType) {
        if ( psiType == null || psiType instanceof PsiArrayType ) {
            return false;
        }

        GlobalSearchScope resolveScope = psiType.getResolveScope();
        if ( resolveScope == null || resolveScope.getProject() == null ) {
            return true;
        }

        if ( psiType instanceof PsiClassType ) {
            return !PsiType.getTypeByName(
                "java.lang.Iterable",
                resolveScope.getProject(),
                resolveScope
            ).isAssignableFrom( psiType )
                && !PsiType.getTypeByName(
                "java.util.Map",
                resolveScope.getProject(),
                resolveScope
            ).isAssignableFrom( psiType );
        }

        return true;
    }

    public static String capitalize(String string) {
        return string == null ? null : string.substring( 0, 1 ).toUpperCase() + string.substring( 1 );
    }

    /**
     * Checks if the {@code psiFile} is located within a module that contains MapStruct.
     *
     * @param psiFile the file for which the check needs to be done
     *
     * @return {@code true} if MapStruct is in the module of the given {@code psiFile}, {@code false} otherwise
     */
    public static boolean isMapStructPresent(@NotNull PsiFile psiFile) {
        Module module = ModuleUtilCore.findModuleForFile( psiFile.getVirtualFile(), psiFile.getProject() );
        return module != null && isMapStructPresent( module );
    }

    /**
     * Checks if MapStruct is withing the provided module.
     *
     * @param module that needs to be checked
     *
     * @return {@code true} if MapStruct is present within the {@code module}, {@code false} otherwise
     */
    private static boolean isMapStructPresent(@NotNull Module module) {
        return CachedValuesManager.getManager( module.getProject() ).getCachedValue( module, () -> {
            boolean foundMarkerClass = JavaPsiFacade.getInstance( module.getProject() )
                .findClass( MAPPER_ANNOTATION_FQN, module.getModuleRuntimeScope( false ) ) != null;
            return CachedValueProvider.Result.createSingleDependency(
                foundMarkerClass,
                ProjectRootManager.getInstance( module.getProject() )
            );
        } );
    }

    /**
     * Checks if MapStruct jdk8 is within the provided module. The MapStruct JDK 8 module is present when the
     * {@link Mapping} annotation is annotated with {@link java.lang.annotation.Repeatable}
     *
     * @param module that needs to be checked
     *
     * @return {@code true} if MapStruct jdk8 is present within the {@code module}, {@code false} otherwise
     */
    static boolean isMapStructJdk8Present(@NotNull Module module) {
        return CachedValuesManager.getManager( module.getProject() ).getCachedValue( module, () -> {
            PsiClass mappingAnnotation = JavaPsiFacade.getInstance( module.getProject() )
                .findClass( MAPPING_ANNOTATION_FQN, module.getModuleRuntimeScope( false ) );
            boolean mapstructJdk8Present = findAnnotation(
                mappingAnnotation,
                true,
                CommonClassNames.JAVA_LANG_ANNOTATION_REPEATABLE
            ) != null;
            return CachedValueProvider.Result.createSingleDependency(
                mapstructJdk8Present,
                ProjectRootManager.getInstance( module.getProject() )
            );
        } );
    }

    /**
     * Checks if the {@code psiMethod} is annotated with {@link InheritInverseConfiguration}.
     *
     * @param method to be checked
     *
     * @return {@code true} if the {@code method} is annotated with {@link InheritInverseConfiguration},
     * {@code false} otherwise
     */
    public static boolean isInheritInverseConfiguration(PsiMethod method) {
        return isAnnotated( method, INHERIT_INVERSE_CONFIGURATION, false );
    }

}
