/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiRecordComponent;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapping;
import org.mapstruct.intellij.util.MapStructVersion;
import org.mapstruct.intellij.util.MapstructUtil;
import org.mapstruct.intellij.util.TargetType;
import org.mapstruct.intellij.util.TargetUtils;

import static org.mapstruct.intellij.util.MapstructAnnotationUtils.findAllDefinedMappingAnnotations;
import static org.mapstruct.intellij.util.MapstructUtil.asLookup;
import static org.mapstruct.intellij.util.MapstructUtil.findRecordComponent;
import static org.mapstruct.intellij.util.MapstructUtil.isPublicModifiable;
import static org.mapstruct.intellij.util.MapstructUtil.isPublicNonStatic;
import static org.mapstruct.intellij.util.TargetUtils.getRelevantType;
import static org.mapstruct.intellij.util.TargetUtils.isBuilderEnabled;
import static org.mapstruct.intellij.util.TargetUtils.publicWriteAccessors;
import static org.mapstruct.intellij.util.TargetUtils.resolveBuilderOrSelfClass;
import static org.mapstruct.intellij.util.TypeUtils.firstParameterPsiType;

/**
 * Reference for {@link org.mapstruct.Mapping#target()}.
 *
 * @author Filip Hrisafov
 */
class MapstructTargetReference extends BaseMappingReference {

    private final MapStructVersion mapStructVersion;

    /**
     * Create a new {@link MapstructTargetReference} with the provided parameters
     *
     * @param element the element that the reference belongs to
     * @param previousReference the previous reference if there is one (in nested properties for example)
     * @param rangeInElement the range that the reference represent in the {@code element}
     * @param value the matched value (useful when {@code rangeInElement} is empty)
     */
    private MapstructTargetReference(PsiElement element, MapstructTargetReference previousReference,
        TextRange rangeInElement, String value) {
        super( element, previousReference, rangeInElement, value );
        mapStructVersion = MapstructUtil.resolveMapStructProjectVersion( element.getContainingFile()
            .getOriginalFile() );
    }

    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiType psiType) {
        boolean builderSupportPresent = mapStructVersion.isBuilderSupported();
        Pair<PsiClass, TargetType> pair = resolveBuilderOrSelfClass(
            psiType,
            builderSupportPresent && isBuilderEnabled( getMappingMethod() )
        );
        if ( pair == null ) {
            return null;
        }

        PsiClass psiClass = pair.getFirst();
        TargetType targetType = pair.getSecond();
        PsiType typeToUse = targetType.type();

        PsiRecordComponent recordComponent = findRecordComponent( value, psiClass );
        if ( recordComponent != null ) {
            return recordComponent;
        }

        if ( mapStructVersion.isConstructorSupported() && !targetType.builder() ) {
            PsiMethod constructor = TargetUtils.resolveMappingConstructor( psiClass );
            if ( constructor != null && constructor.hasParameters() ) {
                for ( PsiParameter parameter : constructor.getParameterList().getParameters() ) {
                    if ( value.equals( parameter.getName() ) ) {
                        return parameter;
                    }
                }
            }
        }

        String capitalizedName = MapstructUtil.capitalize( value );
        PsiMethod[] methods = psiClass.findMethodsByName( "set" + capitalizedName, true );
        if ( methods.length != 0 && isPublicNonStatic( methods[0] ) ) {
            return methods[0];
        }

        // If there is no such setter we need to check if there is a collection getter
        methods = psiClass.findMethodsByName( "get" + capitalizedName, true );
        if ( methods.length != 0 && isCollectionGetterWriteAccessor( methods[0] ) ) {
            return methods[0];
        }

        if ( builderSupportPresent ) {
            for ( Pair<PsiMethod, PsiSubstitutor> builderPair : psiClass.findMethodsAndTheirSubstitutorsByName(
                value,
                true
            ) ) {
                PsiMethod method = builderPair.getFirst();
                if ( method.getParameterList().getParametersCount() == 1 &&
                    mapstructUtil.isFluentSetter( method, typeToUse, builderPair.getSecond() ) ) {
                    return method;
                }
            }
        }

        PsiClass selfClass = PsiUtil.resolveClassInType( psiType );
        if ( selfClass != null ) {
            PsiField field = selfClass.findFieldByName( value, true );
            if ( field != null && isPublicModifiable( field ) ) {
                return field;
            }
        }

        return null;
    }

    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod) {
        PsiType relevantType = getRelevantType( mappingMethod );
        if ( relevantType == null ) {
            return null;
        }

        PsiElement psiElement = resolveInternal( value, relevantType );
        if ( psiElement != null ) {
            return psiElement;
        }

        return Stream.of( mappingMethod.getParameterList().getParameters() )
            .filter( MapstructUtil::isMappingTarget )
            .filter( psiParameter -> Objects.equals( psiParameter.getName(), value ) )
            .findAny()
            .orElse( null );
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiType psiType) {

        PsiMethod mappingMethod = getMappingMethod();

        Map<String, Pair<? extends PsiElement, PsiSubstitutor>> accessors = publicWriteAccessors(
            psiType,
            mapStructVersion,
            mapstructUtil,
            mappingMethod
        );

        if (mappingMethod != null) {
            Stream<String> allDefinedMappingTargets = findAllDefinedMappingTargets( mappingMethod );
            allDefinedMappingTargets.forEach( accessors::remove );
        }

        return asLookup(
            accessors,
            MapstructTargetReference::memberPsiType
        );
    }

    /**
     * Find all defined {@link Mapping#target()} for the given method
     *
     * @param method that needs to be checked
     *
     * @return see description
     */
    private Stream<String> findAllDefinedMappingTargets(@NotNull PsiMethod method) {
        return findAllDefinedMappingAnnotations( method, mapStructVersion )
            .map( psiAnnotation -> AnnotationUtil.getDeclaredStringAttributeValue( psiAnnotation, "target" ) )
            .filter( Objects::nonNull )
            .filter( s -> !s.isEmpty() );
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod) {
        PsiType targetType = getRelevantType( mappingMethod );
        return targetType == null ? LookupElement.EMPTY_ARRAY : getVariantsInternal( targetType );
    }

    @Nullable
    @Override
    PsiType resolvedType() {
        PsiElement element = resolve();

        if ( element instanceof PsiMethod psiMethod ) {
            return firstParameterPsiType( psiMethod );
        }
        else if ( element instanceof PsiParameter psiParameter ) {
            return psiParameter.getType();
        }
        else if ( element instanceof PsiRecordComponent psiRecordComponent ) {
            return psiRecordComponent.getType();
        }
        else if ( element instanceof PsiField psiField ) {
            return psiField.getType();
        }

        return null;
    }

    /**
     * @param psiElement the literal for which references need to be created
     *
     * @return the references for the given {@code psiLiteral}
     */
    static PsiReference[] create(PsiElement psiElement) {
        return MapstructBaseReference.create( psiElement, MapstructTargetReference::new, true );
    }

    private static PsiType memberPsiType(PsiElement psiMember) {
        if ( psiMember instanceof PsiMethod psiMemberMethod ) {
            return resolveMethodType( psiMemberMethod );
        }
        else if ( psiMember instanceof PsiVariable psiMemberVariable ) {
            return psiMemberVariable.getType();
        }
        else {
            return null;
        }

    }

    private static boolean isCollectionGetterWriteAccessor(@NotNull PsiMethod method) {
        if ( !isPublicNonStatic( method ) ) {
            return false;
        }
        PsiParameterList parameterList = method.getParameterList();
        if ( parameterList.getParametersCount() > 0 ) {
            return false;
        }
        return TargetUtils.isMethodReturnTypeAssignableToCollectionOrMap( method );
    }

    private static PsiType resolveMethodType(PsiMethod psiMethod) {
        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        if ( psiParameters.length == 0 ) {
            return psiMethod.getReturnType();
        }
        return psiParameters[0].getType();
    }
}
