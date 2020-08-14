/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import org.mapstruct.intellij.util.toMappingElementPattern
import org.mapstruct.intellij.util.toMappingsElementPattern
import org.mapstruct.intellij.util.toValueMappingPattern
import org.mapstruct.intellij.util.toValueMappingsPattern


/**
 * @author Frank Wang
 */
class KtMapstructReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            "target".toMappingElementPattern(),
            MappingTargetReferenceProvider(MapstructTargetReference::create)
        )
        registrar.registerReferenceProvider(
            "source".toMappingElementPattern(),
            MappingTargetReferenceProvider(MapstructSourceReference::create)
        )
        registrar.registerReferenceProvider(
            "target".toMappingsElementPattern(),
            MappingTargetReferenceProvider(MapstructTargetReference::create)
        )
        registrar.registerReferenceProvider(
            "source".toMappingsElementPattern(),
            MappingTargetReferenceProvider(MapstructSourceReference::create)
        )

        registrar.registerReferenceProvider(
            "target".toValueMappingPattern(),
            MappingTargetReferenceProvider(ValueMappingSourceReference::create)
        )
        registrar.registerReferenceProvider(
            "source".toValueMappingPattern(),
            MappingTargetReferenceProvider(ValueMappingTargetReference::create)
        )
        registrar.registerReferenceProvider(
            "target".toValueMappingsPattern(),
            MappingTargetReferenceProvider(ValueMappingSourceReference::create)
        )
        registrar.registerReferenceProvider(
            "source".toValueMappingsPattern(),
            MappingTargetReferenceProvider(ValueMappingTargetReference::create)
        )
    }
}
