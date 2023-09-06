/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;

public final class TypeUtils {

    private TypeUtils() {
    }

    /**
     * Util function for extracting the type of the first parameter of a method.
     *
     * @param psiMethod the method to extract the parameter from
     *
     * @return the type of the first parameter of the method
     */
    public static PsiType firstParameterPsiType(PsiMethod psiMethod) {
        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        if ( psiParameters.length == 0) {
            return null;
        }
        return psiParameters[0].getType();
    }
}
