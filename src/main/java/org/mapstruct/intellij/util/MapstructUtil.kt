/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.asJava.classes.createGeneratedMethodFromDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.IDELightClassGenerationSupport
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode


/**
 * @author Frank Wang
 */
fun PsiElement.getPsiMethod(): PsiMethod? {
    return this.getNonStrictParentOfType() ?: this.getNonStrictParentOfType<KtNamedFunction>()?.toPsiMethod()
}

fun KtCallExpression.getFqName(): FqName? {
    val bindingContext = this.analyze(BodyResolveMode.PARTIAL_FOR_COMPLETION)
    val ktExpression = this.calleeExpression ?: return null
    return ktExpression.getResolvedCall(bindingContext)?.resultingDescriptor?.returnType?.fqName
}

private fun KtNamedFunction.toPsiMethod(): PsiMethod? {
    // dealing with kotlin class
    val ktClass = this.getNonStrictParentOfType(KtClass::class.java) ?: return null
    val descriptor =
        this.resolveToDescriptorIfAny(BodyResolveMode.PARTIAL_FOR_COMPLETION) ?: return null
    return IDELightClassGenerationSupport(this.project)
        .createUltraLightClass(ktClass)
        ?.createGeneratedMethodFromDescriptor(descriptor, null)
}
