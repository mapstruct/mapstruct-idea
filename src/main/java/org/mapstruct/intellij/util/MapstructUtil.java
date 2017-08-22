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
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiFormatUtil;
import com.intellij.psi.util.PsiFormatUtilBase;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ValueMapping;

/**
 * @author Filip Hrisafov
 */
public final class MapstructUtil {

    static final String MAPPER_ANNOTATION_FQN = Mapper.class.getName();
    static final String MAPPER_CONFIG_ANNOTATION_FQN = MapperConfig.class.getName();
    static final String MAPPING_ANNOTATION_FQN = Mapping.class.getName();
    static final String VALUE_MAPPING_ANNOTATION_FQN = ValueMapping.class.getName();
    private static final String MAPPING_TARGET_ANNOTATION_FQN = MappingTarget.class.getName();
    //TODO maybe we need to include the 1.2.0-RC1 here
    private static final String CONTEXT_ANNOTATION_FQN = "org.mapstruct.Context";

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
        return Introspector.decapitalize( methodName.substring( methodName.startsWith( "is" ) ? 2 : 3 ) );
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
    public static boolean canDescendIntoType(PsiType psiType) {
        if ( psiType instanceof PsiArrayType ) {
            return false;
        }
        //TODO add checks for Iterable and Map

        return true;
    }
}
