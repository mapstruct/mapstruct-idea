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
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.ap.internal.util.Strings;
import org.mapstruct.intellij.util.MapstructUtil;

/**
 * @author Filip Hrisafov
 */
class MapstructSourceReference extends MapstructBaseReference {

    MapstructSourceReference(PsiLiteral element) {
        super( element );
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiClass sourceClass = getRelevantClass();
        String value = getValue();
        if ( sourceClass == null || value.isEmpty()) {
            return null;
        }
        PsiMethod[] methods = sourceClass.findMethodsByName( "get" + Strings.capitalize( value ), true );

        if ( methods.length == 0 ) {
            methods = sourceClass.findMethodsByName( "is" + Strings.capitalize( value ), true );
        }

        //If instead of doing the above we replace with the below highlighting, renaming, Find Usages works correctly
        //PsiMethod[] methods = sourceClass.findMethodsByName(getValue(), true);

        return methods.length == 0 ? null : methods[0];
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        PsiClass sourceClass = getRelevantClass();
        if ( sourceClass == null ) {
            return new Object[0];
        }

        return sourceClass.getAllMethodsAndTheirSubstitutors().stream()
            .filter( pair -> MapstructUtil.isGetter( pair.getFirst() ) )
            .filter( pair -> MapstructUtil.isPublic( pair.getFirst() ) )
            .map( pair -> MapstructUtil.asLookup( pair, PsiMethod::getReturnType ) )
            .toArray();
    }

    @Override
    PsiClass getRelevantClass(@NotNull PsiMethod mappingMethod) {
        PsiParameterList parameters = mappingMethod.getParameterList();

        if ( parameters.getParametersCount() == 0 ) {
            return null;
        }

        //TODO this is not really correct, we need to adapt with @MappingTarget, multiple sources etc
        return PsiUtil.resolveClassInType( parameters.getParameters()[0].getType() );
    }

}
