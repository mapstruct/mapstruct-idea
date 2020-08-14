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
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reference for {@link org.mapstruct.ValueMapping#target()}.
 *
 * @author Filip Hrisafov
 */
public class ValueMappingTargetReference extends BaseValueMappingReference {

    /**
     * @param element the element for which a reference should be found
     */
    private ValueMappingTargetReference(@NotNull PsiElement element) {
        super( element );
    }

    @Nullable
    @Override
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod) {
        //TODO should we resolve and suggest MappingConstants as well?
        PsiClass targetClass = methodReturnClass( mappingMethod );
        if ( targetClass == null || !targetClass.isEnum() ) {
            return null;
        }
        PsiField field = targetClass.findFieldByName( value, false );

        if ( field instanceof PsiEnumConstant ) {
            return field;
        }

        return null;
    }

    @NotNull
    @Override
    Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod) {
        PsiClass targetClass = methodReturnClass( mappingMethod );
        if ( targetClass == null || !targetClass.isEnum() ) {
            return LookupElement.EMPTY_ARRAY;
        }

        return Stream.of( targetClass.getFields() )
            .filter( psiField -> psiField instanceof PsiEnumConstant )
            .toArray( PsiField[]::new );
    }

    /**
     * @param psiLiteral for which references need to be created
     *
     * @return the created references for the passed {@code psiLiteral}
     */
    public static PsiReference[] create(PsiElement psiLiteral) {
        return new PsiReference[] { new ValueMappingTargetReference( psiLiteral ) };
    }

    @Nullable
    private static PsiClass methodReturnClass(@NotNull PsiMethod method) {
        return PsiUtil.resolveClassInType( method.getReturnType() );
    }
}
