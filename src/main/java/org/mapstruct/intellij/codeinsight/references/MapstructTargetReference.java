/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import java.util.Objects;
import java.util.stream.Stream;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiRecordComponent;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.PsiUtil;
import de.plushnikov.intellij.plugin.psi.LombokLightMethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.util.MapStructVersion;
import org.mapstruct.intellij.util.MapstructUtil;
import org.mapstruct.intellij.util.TargetType;
import org.mapstruct.intellij.util.TargetUtils;

import static org.mapstruct.intellij.util.MapstructUtil.asLookup;
import static org.mapstruct.intellij.util.MapstructUtil.findRecordComponent;
import static org.mapstruct.intellij.util.MapstructUtil.isPublicModifiable;
import static org.mapstruct.intellij.util.MapstructUtil.isPublicNonStatic;
import static org.mapstruct.intellij.util.TargetUtils.getRelevantType;
import static org.mapstruct.intellij.util.TargetUtils.isBuilderEnabled;
import static org.mapstruct.intellij.util.TargetUtils.publicWriteAccessors;
import static org.mapstruct.intellij.util.TargetUtils.resolveBuilderOrSelfClass;

/**
 * Reference for {@link org.mapstruct.Mapping#target()}.
 *
 * @author Filip Hrisafov
 */
class MapstructTargetReference extends MapstructBaseReference {

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
                        if ( constructor instanceof LombokLightMethodBuilder ) {
                            PsiField field = psiClass.findFieldByName( value, true );
                            if ( field != null ) {
                                return field;
                            }
                        }
                        return parameter;
                    }
                }
            }
        }

        PsiMethod[] methods = psiClass.findMethodsByName( "set" + MapstructUtil.capitalize( value ), true );
        if ( methods.length != 0 && isPublicNonStatic( methods[0] ) ) {
            return methods[0];
        }

        if ( builderSupportPresent ) {
            for ( PsiMethod method : psiClass.findMethodsByName( value, true ) ) {
                if ( method.getParameterList().getParametersCount() == 1 &&
                    MapstructUtil.isFluentSetter( method, typeToUse ) ) {
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
        return asLookup(
            publicWriteAccessors( psiType, mapStructVersion, getMappingMethod() ),
            MapstructTargetReference::memberPsiType
        );
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

        if ( element instanceof PsiMethod ) {
            return firstParameterPsiType( (PsiMethod) element );
        }
        else if ( element instanceof PsiParameter ) {
            return ( (PsiParameter) element ).getType();
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
        if ( psiMember instanceof PsiMethod ) {
            return firstParameterPsiType( (PsiMethod) psiMember );
        }
        else if ( psiMember instanceof PsiVariable ) {
            return ( (PsiVariable) psiMember ).getType();
        }
        else {
            return null;
        }

    }

    /**
     * Util function for extracting the type of the first parameter of a method.
     *
     * @param psiMethod the method to extract the parameter from
     *
     * @return the type of the first parameter of the method
     */
    private static PsiType firstParameterPsiType(PsiMethod psiMethod) {
        return psiMethod.getParameterList().getParameters()[0].getType();
    }
}
