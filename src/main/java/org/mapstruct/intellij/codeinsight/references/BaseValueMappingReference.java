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

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base Reference for {@link org.mapstruct.ValueMapping}(s).
 *
 * @author Filip Hrisafov
 */
abstract class BaseValueMappingReference extends BaseReference {

    /**
     * @param element the element for which a reference should be found
     */
    BaseValueMappingReference(@NotNull PsiLiteral element) {
        super( element );
    }

    @Nullable
    @Override
    public final PsiElement resolve() {
        String value = getValue();
        if ( value.isEmpty() ) {
            return null;
        }

        PsiMethod mappingMethod = getMappingMethod();
        if ( isNotValueMapping( mappingMethod ) ) {
            return null;
        }

        return resolveInternal( value, mappingMethod );
    }

    @Nullable
    abstract PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod);

    @NotNull
    @Override
    public final Object[] getVariants() {
        PsiMethod mappingMethod = getMappingMethod();
        if ( isNotValueMapping( mappingMethod ) ) {
            return LookupElement.EMPTY_ARRAY;
        }
        return getVariantsInternal( mappingMethod );
    }

    @NotNull
    abstract Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod);

    private static boolean isNotValueMapping(@Nullable PsiMethod mappingMethod) {
        return mappingMethod == null || mappingMethod.getParameterList().getParametersCount() != 1;
    }
}
