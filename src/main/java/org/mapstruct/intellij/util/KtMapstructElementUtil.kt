/**
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import kotlin.reflect.KClass

/**
 * @author Frank Wang
 */
fun String.toValueMappingPattern(): ElementPattern<out PsiElement> {
    return this.toElementPattern(MapstructUtil.VALUE_MAPPING_ANNOTATION_FQN)
}

fun String.toMappingElementPattern(): ElementPattern<out PsiElement> {
    return this.toElementPattern(MapstructUtil.MAPPING_ANNOTATION_FQN)
}

private fun String.toElementPattern(annotationFqName: String): ElementPattern<out PsiElement> {
    val paramName = this
    return PlatformPatterns.psiElement(KtStringTemplateExpression::class.java).withParent(
        KtValueArgument::class.psiPattern { ktValueArgument, _ ->
            ktValueArgument.getArgumentName()?.text == paramName
        }.withSuperParent(
            2,
            KtCallExpression::class.psiPattern { ktCallExpression, _ ->
                ktCallExpression.getFqName()?.asString() == annotationFqName
            }
        )
    )
}

private inline fun <reified T : PsiElement> KClass<T>.psiPattern(
    crossinline acceptsInvoker: (T, ProcessingContext?) -> Boolean
): PsiElementPattern.Capture<T> {
    return PlatformPatterns.psiElement(T::class.java).with(
        object : PatternCondition<T>("KtValueArgument") {
            override fun accepts(t: T, context: ProcessingContext?): Boolean {
                return acceptsInvoker(t, context)
            }
        }
    )
}
