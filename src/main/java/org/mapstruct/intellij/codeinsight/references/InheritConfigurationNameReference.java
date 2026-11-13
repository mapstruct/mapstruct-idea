/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.inspection.inheritance.SourceMethod;

import static org.mapstruct.intellij.inspection.inheritance.InheritConfigurationUtils.findMappingMethodsFromInheritScope;
import static org.mapstruct.intellij.util.MapstructUtil.MAPPER_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.asLookupWithRepresentableText;
import static org.mapstruct.intellij.util.TargetUtils.getRelevantType;

/**
 * Reference for {@link org.mapstruct.InheritConfiguration#name()} and
 * {@link org.mapstruct.InheritInverseConfiguration#name()}.
 *
 * @author hduelme
 */
public class InheritConfigurationNameReference extends MapstructBaseReference {

    private final boolean inverse;

    /**
     * Create a new {@link InheritConfigurationNameReference} with the provided parameters
     *
     * @param element the element that the reference belongs to
     * @param previousReference the previous reference if there is one (in nested properties for example)
     * @param rangeInElement the range that the reference represent in the {@code element}
     * @param value the matched value (useful when {@code rangeInElement} is empty)
     * @param inverse whether this is the inverse variant ({@link org.mapstruct.InheritInverseConfiguration})
     */
    private InheritConfigurationNameReference(PsiElement element,
                                              InheritConfigurationNameReference previousReference,
                                              TextRange rangeInElement,
                                              String value,
                                              boolean inverse) {
        super( element, previousReference, rangeInElement, value );
        this.inverse = inverse;
    }

    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiType psiType) {
        return null; // not needed
    }

    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod) {

        java.util.List<PsiMethod> namedCandidates = candidatesInScope( mappingMethod )
            .filter( candidate -> value.equals( candidate.getName() ) )
            .distinct()
            .toList();

        // Match the disambiguation rule of InheritConfigurationUtils#getResultMethod:
        // a name value is only valid if it identifies exactly one in-scope candidate.
        if ( namedCandidates.size() != 1 ) {
            return null;
        }

        return namedCandidates.getFirst().getNameIdentifier();
    }

    @NotNull
    private Stream<PsiMethod> candidatesInScope(@NotNull PsiMethod mappingMethod) {
        return inScopeMethods( mappingMethod )
            .filter( candidateFilter( mappingMethod ) )
            .filter( m -> !m.equals( mappingMethod ) )
            .distinct();
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiType psiType) {
        return LookupElement.EMPTY_ARRAY; // not needed
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod) {
        return candidatesInScope( mappingMethod )
            .map( this::methodAsLookup )
            .filter( Objects::nonNull )
            .toArray();
    }

    @Nullable
    private LookupElement methodAsLookup(@NotNull PsiMethod method) {
        String lookupString = method.getName();
        if ( lookupString.isEmpty() ) {
            return null;
        }

        PsiClass containingClass = method.getContainingClass();
        String classSimpleName = containingClass != null ? containingClass.getName() : "?";

        return asLookupWithRepresentableText(
            method,
            lookupString,
            lookupString,
            String.format(
                " %s#%s(%s)",
                classSimpleName,
                method.getName(),
                formatParameters( method )
            )
        );
    }

    @NotNull
    private Stream<PsiMethod> inScopeMethods(@NotNull PsiMethod mappingMethod) {
        PsiClass containingClass = mappingMethod.getContainingClass();
        if ( containingClass == null ) {
            return Stream.empty();
        }

        PsiAnnotation mapperAnnotation = containingClass.getAnnotation( MAPPER_ANNOTATION_FQN );
        if ( mapperAnnotation == null ) {
            return Stream.empty();
        }

        return findMappingMethodsFromInheritScope( containingClass, mapperAnnotation );
    }

    @NotNull
    private Predicate<PsiMethod> candidateFilter(@NotNull PsiMethod mappingMethod) {
        PsiType targetType = getRelevantType( mappingMethod );
        SourceMethod sourceMethod = new SourceMethod( mappingMethod );
        if ( inverse ) {
            return candidate -> candidate.getBody() == null && sourceMethod.inverses( candidate, targetType );
        }
        return candidate -> candidate.getBody() == null && sourceMethod.canInheritFrom( candidate, targetType );
    }

    @NotNull
    private static String formatParameters(@NotNull PsiMethod method) {
        return Arrays.stream( method.getParameterList().getParameters() )
            .map( PsiParameter::getType )
            .map( PsiType::getPresentableText )
            .collect( java.util.stream.Collectors.joining( ", " ) );
    }

    @Nullable
    @Override
    PsiType resolvedType() {
        return null;
    }

    @NotNull
    @Override
    public String getUnresolvedMessagePattern() {
        return MapStructBundle.message(
            inverse ? "unknown.inheritInverseConfiguration.reference" : "unknown.inheritConfiguration.reference"
        );
    }

    /**
     * @param psiElement the literal for which references need to be created
     * @return the references for the given {@code psiLiteral}
     */
    static PsiReference[] createForward(PsiElement psiElement) {
        return MapstructBaseReference.create( psiElement,
            InheritConfigurationNameReference::newForward,
            false );
    }

    /**
     * @param psiElement the literal for which references need to be created
     * @return the references for the given {@code psiLiteral}
     */
    static PsiReference[] createInverse(PsiElement psiElement) {
        return MapstructBaseReference.create( psiElement,
            InheritConfigurationNameReference::newInverse,
            false );
    }

    private static InheritConfigurationNameReference newForward(PsiElement element,
                                                                InheritConfigurationNameReference previous,
                                                                TextRange range,
                                                                String value) {
        return new InheritConfigurationNameReference( element, previous, range, value, false );
    }

    private static InheritConfigurationNameReference newInverse(PsiElement element,
                                                                InheritConfigurationNameReference previous,
                                                                TextRange range,
                                                                String value) {
        return new InheritConfigurationNameReference( element, previous, range, value, true );
    }

}
