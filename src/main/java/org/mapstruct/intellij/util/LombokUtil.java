/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;

/**
 * @author Filip Hrisafov
 */
public final class LombokUtil {

    private static final Class<?> LOMBOK_LIGHT_METHOD;

    static {
        Class<?> lombokLightMethod;
        try {
            lombokLightMethod = Class.forName( "de.plushnikov.intellij.plugin.psi.LombokLightMethodBuilder" );
        }
        catch ( ClassNotFoundException e ) {
            lombokLightMethod = null;
        }
        LOMBOK_LIGHT_METHOD = lombokLightMethod;
    }

    private LombokUtil() {
    }

    public static boolean isLombokLightMethod(PsiMethod method) {
        if ( LOMBOK_LIGHT_METHOD != null ) {
            return LOMBOK_LIGHT_METHOD.isInstance( method );
        }
        return false;
    }

    public static PsiElement resolvePsiElementForMethod(PsiMethod method, String value, PsiClass psiClass) {
        return resolvePsiElement( method, method, value, psiClass );
    }

    public static PsiElement resolvePsiElement(PsiMethod method, PsiElement currentResolved, String value,
                                               PsiClass psiClass) {
        if ( isLombokLightMethod( method ) ) {
            PsiField field = psiClass.findFieldByName( value, true );
            if ( field != null ) {
                return field;
            }
        }

        return currentResolved;
    }
}
