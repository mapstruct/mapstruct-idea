/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import org.mapstruct.intellij.util.MapstructKotlinElementUtils.mappingElementPattern
import org.mapstruct.intellij.util.MapstructKotlinElementUtils.valueMappingElementPattern

/**
 * @author Frank Wang
 */
class KtMapstructReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            mappingElementPattern("target"),
            MappingTargetReferenceProvider(MapstructTargetReference::create)
        )
        registrar.registerReferenceProvider(
            mappingElementPattern("source"),
            MappingTargetReferenceProvider(MapstructSourceReference::create)
        )
        registrar.registerReferenceProvider(
            valueMappingElementPattern("target"),
            MappingTargetReferenceProvider(ValueMappingSourceReference::create)
        )
        registrar.registerReferenceProvider(
            valueMappingElementPattern("source"),
            MappingTargetReferenceProvider(ValueMappingTargetReference::create)
        )
    }
}
