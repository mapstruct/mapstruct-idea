/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import java.util.stream.Stream;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.mapstruct.intellij.util.MapstructUtil.asLookup;
import static org.mapstruct.intellij.util.SourceUtils.getParameterClass;

/**
 * Reference for {@link org.mapstruct.ValueMapping#source()}.
 *
 * @author Filip Hrisafov
 */
public class ValueMappingSourceReference extends BaseValueMappingReference {

    /**
     * @param element the element for which a reference should be found
     */
    private ValueMappingSourceReference(@NotNull PsiElement element) {
        super( element );
    }

    @Nullable
    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod) {
        //TODO should we resolve and suggest MappingConstants as well?
        PsiClass sourceClass = getParameterClass( mappingMethod.getParameterList().getParameters()[0] );
        if ( sourceClass == null || !sourceClass.isEnum() ) {
            return null;
        }
        PsiField field = sourceClass.findFieldByName( value, false );

        if ( field instanceof PsiEnumConstant ) {
            return field;
        }

        return null;
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod) {
        PsiClass sourceClass = getParameterClass( mappingMethod.getParameterList().getParameters()[0] );
        if ( sourceClass == null || !sourceClass.isEnum() ) {
            return LookupElement.EMPTY_ARRAY;
        }

        return Stream.of( sourceClass.getFields() )
            .filter( psiField -> psiField instanceof PsiEnumConstant )
            .map( psiEnumConstant -> asLookup( (PsiEnumConstant) psiEnumConstant ) )
            .toArray( LookupElement[]::new );
    }

    /**
     * @param psiLiteral for which references need to be created
     *
     * @return the created references for the passed {@code psiLiteral}
     */
    public static PsiReference[] create(PsiElement psiLiteral) {
        return new PsiReference[] { new ValueMappingSourceReference( psiLiteral ) };
    }
}
