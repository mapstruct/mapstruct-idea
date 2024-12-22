/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UExpression;
import org.jetbrains.uast.UMethod;
import org.jetbrains.uast.UastContextKt;
import org.jetbrains.uast.UastUtils;

/**
 * Base Reference for MapStruct.
 *
 * @author Filip Hrisafov
 */
public abstract class BaseReference extends PsiReferenceBase<PsiElement> {

    /**
     * @param element the element for which a reference should be found
     */
    BaseReference(@NotNull PsiElement element) {
        super( element );
    }

    /**
     * @param element the element for which a reference should be found
     * @param rangeInElement the range in the element
     */
    BaseReference(PsiElement element, TextRange rangeInElement) {
        super( element, rangeInElement );
    }

    /**
     * @return The mapping method that this reference belongs to
     */
    @Nullable
    PsiMethod getMappingMethod() {
        PsiElement element = getElement();
        UExpression expression = UastContextKt.toUElement( element, UExpression.class );
        if ( expression != null ) {
            UMethod parent = UastUtils.getParentOfType( expression, UMethod.class );
            if ( parent != null ) {
                return parent.getJavaPsi();
            }
        }
        return null;
    }
}
