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
package org.mapstruct.intellij.codeinsight.references;

import java.util.stream.Stream;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private ValueMappingSourceReference(@NotNull PsiLiteral element) {
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
            .toArray( PsiField[]::new );
    }

    /**
     * @param psiLiteral for which references need to be created
     *
     * @return the created references for the passed {@code psiLiteral}
     */
    public static PsiReference[] create(PsiLiteral psiLiteral) {
        return new PsiReference[] { new ValueMappingSourceReference( psiLiteral ) };
    }
}
