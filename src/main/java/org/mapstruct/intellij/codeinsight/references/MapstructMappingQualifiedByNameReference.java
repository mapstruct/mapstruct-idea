/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapstructUtil;

import static com.intellij.codeInsight.AnnotationUtil.findAnnotation;
import static com.intellij.codeInsight.AnnotationUtil.getStringAttributeValue;
import static org.mapstruct.intellij.util.MapstructAnnotationUtils.findReferencedMapperClasses;
import static org.mapstruct.intellij.util.MapstructUtil.NAMED_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.MAPPER_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.MAPPER_CONFIG_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.asLookupWithRepresentableText;

/**
 * Reference for {@link org.mapstruct.Mapping#qualifiedByName()}.
 *
 * @author Oliver Erhart
 */
class MapstructMappingQualifiedByNameReference extends MapstructBaseReference {

    /**
     * Create a new {@link MapstructMappingQualifiedByNameReference} with the provided parameters
     *
     * @param element the element that the reference belongs to
     * @param previousReference the previous reference if there is one (in nested properties for example)
     * @param rangeInElement the range that the reference represent in the {@code element}
     * @param value the matched value (useful when {@code rangeInElement} is empty)
     */
    private MapstructMappingQualifiedByNameReference(PsiElement element,
                                                     MapstructMappingQualifiedByNameReference previousReference,
                                                     TextRange rangeInElement, String value) {
        super( element, previousReference, rangeInElement, value );
    }

    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiType psiType) {
        return null; // not needed
    }

    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod) {

        return findAllNamedMethodsFromThisAndReferencedMappers( mappingMethod )
            .filter( a -> Objects.equals( getNamedValue( a ), value ) )
            .findAny()
            .orElse( null );
    }

    @Nullable
    private String getNamedValue(PsiMethod method) {

        PsiAnnotation namedAnnotation = findAnnotation( method, true, NAMED_ANNOTATION_FQN );

        if ( namedAnnotation == null ) {
            return null;
        }

        return getStringAttributeValue( namedAnnotation, "value" );
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiType psiType) {
        return LookupElement.EMPTY_ARRAY; // not needed
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod) {

        return findAllNamedMethodsFromThisAndReferencedMappers( mappingMethod )
            .map( this::methodAsLookup )
            .filter( Objects::nonNull )
            .toArray();
    }

    private boolean methodHasReturnType(@NotNull PsiMethod psiMethod) {
        return !PsiTypes.voidType().equals( psiMethod.getReturnType() );
    }

    @NotNull
    private Stream<PsiMethod> findAllNamedMethodsFromThisAndReferencedMappers(@NotNull PsiMethod mappingMethod) {

        PsiClass containingClass = mappingMethod.getContainingClass();
        if ( containingClass == null ) {
            return Stream.empty();
        }

        Stream<PsiMethod> internalMethods = Stream.of( containingClass.getAllMethods() )
            .filter( MapstructUtil::isNamedMethod )
            .filter( m -> !m.hasModifierProperty( PsiModifier.PRIVATE ) );

        Stream<PsiMethod> externalMethods = findNamedMethodsInUsedMappers( containingClass )
            .filter( method -> methodIsAccessibleFrom( method, containingClass ) );

        return Stream.concat( internalMethods, externalMethods )
            .filter( this::methodHasReturnType );
    }

    private boolean methodIsAccessibleFrom(PsiMethod method, PsiClass containingClass) {
        PsiClass methodClass = method.getContainingClass();
        if ( methodClass == null ) {
            return false;
        }

        if ( method.hasModifierProperty( PsiModifier.PRIVATE ) ) {
            return false;
        }

        if ( method.hasModifierProperty( PsiModifier.PUBLIC ) ) {
            return true;
        }

        return haveSamePackage( containingClass, methodClass );
    }

    private boolean haveSamePackage(PsiClass firstClass, PsiClass secondClass) {
        return Objects.equals(
            StringUtil.getPackageName( Objects.requireNonNull( firstClass.getQualifiedName() ) ),
            StringUtil.getPackageName( Objects.requireNonNull( secondClass.getQualifiedName() ) )
        );
    }

    @NotNull
    private Stream<PsiMethod> findNamedMethodsInUsedMappers(@Nullable PsiClass containingClass) {

        PsiAnnotation mapperOrMapperConfigAnnotation =
            Optional.ofNullable( findAnnotation( containingClass, MAPPER_ANNOTATION_FQN ) )
                .orElseGet( () -> findAnnotation( containingClass, MAPPER_CONFIG_ANNOTATION_FQN ) );

        if ( mapperOrMapperConfigAnnotation == null ) {
            return Stream.empty();
        }

        return findReferencedMapperClasses( mapperOrMapperConfigAnnotation )
            .flatMap( psiClass -> Arrays.stream( psiClass.getMethods() ) )
            .filter( MapstructUtil::isNamedMethod );
    }

    private LookupElement methodAsLookup(@NotNull PsiMethod method) {
        String lookupString = getNamedValue( method );
        if ( StringUtil.isEmpty( lookupString ) ) {
            return null;
        }

        return asLookupWithRepresentableText(
            method,
            lookupString,
            lookupString,
            String.format(
                " %s#%s(%s)",
                Objects.requireNonNull( method.getContainingClass() ).getName(),
                method.getName(),
                formatParameters( method )
            )
        );
    }

    @NotNull
    private static String formatParameters(@NotNull PsiMethod method) {
        return Arrays.stream( method.getParameterList().getParameters() )
            .map( PsiParameter::getType )
            .map( PsiType::getPresentableText )
            .collect( Collectors.joining( ", " ) );
    }

    @Nullable
    @Override
    PsiType resolvedType() {
        return null;
    }

    @NotNull
    @Override
    public String getUnresolvedMessagePattern() {
        //noinspection UnresolvedPropertyKey
        return MapStructBundle.message( "unknown.qualifiedByName.reference" );
    }

    /**
     * @param psiElement the literal for which references need to be created
     * @return the references for the given {@code psiLiteral}
     */
    static PsiReference[] create(PsiElement psiElement) {
        return MapstructBaseReference.create( psiElement, MapstructMappingQualifiedByNameReference::new, false );
    }

}
