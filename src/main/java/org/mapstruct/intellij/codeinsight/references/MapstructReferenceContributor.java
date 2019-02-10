/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
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
