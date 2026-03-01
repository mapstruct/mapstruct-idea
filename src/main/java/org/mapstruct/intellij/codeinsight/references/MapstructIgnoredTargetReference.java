/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import java.util.stream.Stream;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.util.TargetUtils;

import static org.mapstruct.intellij.util.MapstructUtil.canDescendIntoType;
import static org.mapstruct.intellij.util.TargetUtils.getRelevantType;

/**
 * Reference for {@link org.mapstruct.Ignored#targets()}.
 *
 * @author Filip Hrisafov
 */
class MapstructIgnoredTargetReference extends BaseTargetReference {

    private MapstructIgnoredTargetReference(PsiElement element, MapstructIgnoredTargetReference previousReference,
        TextRange rangeInElement, String value) {
        super( element, previousReference, rangeInElement, value );
    }

    @Nullable
    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod) {
        PsiType targetType = resolveIgnoredTargetsBaseType( mappingMethod );
        return targetType == null ? null : resolveInternal( value, targetType );
    }

    @Override
    protected Stream<String> findAllDefinedTargets(PsiMethod mappingMethod) {
        PsiAnnotation annotation = PsiTreeUtil.getParentOfType( getElement(), PsiAnnotation.class );

        Stream<String> allTargets = super.findAllDefinedTargets( mappingMethod );

        String prefixDot = TargetUtils.getIgnoredPrefix( annotation );

        if ( !prefixDot.isEmpty() ) {
            allTargets = allTargets
                .filter( target -> target.startsWith( prefixDot ) )
                .map( target -> target.substring( prefixDot.length() ) );
        }
        return allTargets;
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod) {
        PsiType targetType = resolveIgnoredTargetsBaseType( mappingMethod );
        return targetType == null ? LookupElement.EMPTY_ARRAY : getVariantsInternal( targetType );
    }

    static PsiReference[] create(PsiElement psiElement) {
        return MapstructBaseReference.create( psiElement, MapstructIgnoredTargetReference::new, true );
    }

    @Nullable
    private PsiType resolveIgnoredTargetsBaseType(@NotNull PsiMethod mappingMethod) {
        PsiType targetType = getRelevantType( mappingMethod );
        if ( targetType == null ) {
            return null;
        }

        PsiAnnotation annotation = PsiTreeUtil.getParentOfType( getElement(), PsiAnnotation.class );
        if ( annotation == null ) {
            return targetType;
        }

        String prefix = AnnotationUtil.getDeclaredStringAttributeValue( annotation, "prefix" );
        if ( prefix == null || prefix.isEmpty() ) {
            return targetType;
        }

        PsiElement prefixElement = annotation.findDeclaredAttributeValue( "prefix" );
        if ( prefixElement == null ) {
            return targetType;
        }

        PsiReference[] prefixReferences = MapstructTargetReference.create( prefixElement );
        if ( prefixReferences.length == 0 ) {
            return null;
        }

        PsiReference lastReference = prefixReferences[prefixReferences.length - 1];
        if ( lastReference instanceof MapstructBaseReference baseReference ) {
            PsiType resolvedType = baseReference.resolvedType();
            return canDescendIntoType( resolvedType ) ? resolvedType : null;
        }

        return null;
    }

}
