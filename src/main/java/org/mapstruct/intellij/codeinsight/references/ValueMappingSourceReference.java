/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.util.MapstructUtil;
import org.mapstruct.intellij.util.ValueMappingUtils;

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

    @Override
    PsiClass determineEnumClass(@NotNull PsiMethod mappingMethod) {
        return getParameterClass( mappingMethod.getParameterList().getParameters()[0] );
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod, @NotNull PsiClass sourceClass) {
        Set<String> alreadyDefinedValues = ValueMappingUtils.findAllDefinedValueMappingSources( mappingMethod )
            .collect( Collectors.toSet() );

        return Stream.of( sourceClass.getFields() )
            .filter( PsiEnumConstant.class::isInstance )
            .map( PsiEnumConstant.class::cast )
            .filter( enumConstant -> !alreadyDefinedValues.contains( enumConstant.getName() ) )
            .map( MapstructUtil::asLookup )
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
