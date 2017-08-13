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

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.util.MapstructUtil;

/**
 * @author Filip Hrisafov
 */
abstract class MapstructBaseReference extends PsiReferenceBase<PsiLiteral> {
    MapstructBaseReference(@NotNull PsiLiteral element) {
        super( element );
    }

    @Nullable
    PsiClass getRelevantClass() {
        PsiMethod mappingMethod = PsiTreeUtil.getParentOfType( getElement(), PsiMethod.class );
        if ( mappingMethod == null ) {
            return null;
        }
        return getRelevantClass( mappingMethod );
    }

    abstract PsiClass getRelevantClass(@NotNull PsiMethod mappingMethod);

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return super.handleElementRename( MapstructUtil.getPropertyName( newElementName ) );
    }
}
