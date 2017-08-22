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

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base Reference for MapStruct.
 *
 * @author Filip Hrisafov
 */
abstract class BaseReference extends PsiReferenceBase<PsiLiteral> {

    /**
     * @param element the element for which a reference should be found
     */
    BaseReference(@NotNull PsiLiteral element) {
        super( element );
    }

    /**
     * @param element the element for which a reference should be found
     * @param rangeInElement the range in the element
     */
    BaseReference(PsiLiteral element, TextRange rangeInElement) {
        super( element, rangeInElement );
    }

    /**
     * @return The mapping method that this reference belongs to
     */
    @Nullable
    PsiMethod getMappingMethod() {
        return PsiTreeUtil.getParentOfType( getElement(), PsiMethod.class );
    }
}
