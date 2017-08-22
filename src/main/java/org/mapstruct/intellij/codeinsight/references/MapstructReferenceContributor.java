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

import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;

import static org.mapstruct.intellij.util.MapstructElementUtils.mappingElementPattern;
import static org.mapstruct.intellij.util.MapstructElementUtils.valueMappingElementPattern;

/**
 * {@link PsiReferenceContributor} for MapStruct annotations.
 *
 * @author Filip Hrisafov
 */
public class MapstructReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
            mappingElementPattern( "target" ),
            new MappingTargetReferenceProvider( MapstructTargetReference::create )
        );
        registrar.registerReferenceProvider(
            mappingElementPattern( "source" ),
            new MappingTargetReferenceProvider( MapstructSourceReference::create )
        );

        registrar.registerReferenceProvider(
            valueMappingElementPattern( "source" ),
            new MappingTargetReferenceProvider( ValueMappingSourceReference::create )
        );
        registrar.registerReferenceProvider(
            valueMappingElementPattern( "target" ),
            new MappingTargetReferenceProvider( ValueMappingTargetReference::create )
        );
    }

}
