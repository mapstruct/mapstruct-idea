/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.MapStructBundle;

/**
 * Base Reference for {@link org.mapstruct.ValueMapping}(s).
 *
 * @author Filip Hrisafov
 */
public abstract class BaseValueMappingReference extends BaseReference {

    /**
     * @param element the element for which a reference should be found
     */
    BaseValueMappingReference(@NotNull PsiElement element) {
        super( element );
    }

    @Nullable
    @Override
    public final PsiElement resolve() {
        String value = getValue();
        if ( value.isEmpty() ) {
            return null;
        }

        PsiClass enumClass = getEnumClass();
        if ( enumClass == null ) {
            return null;
        }

        return resolveInternal( value, enumClass );
    }

    @Override
    @Nullable
    PsiMethod getMappingMethod() {
        PsiMethod mappingMethod = super.getMappingMethod();
        if ( isNotValueMapping( mappingMethod ) ) {
            return null;
        }
        return mappingMethod;
    }

    public PsiClass getEnumClass() {
        PsiMethod mappingMethod = getMappingMethod();
        if ( mappingMethod == null ) {
            return null;
        }

        PsiClass enumClass = getEnumClass( mappingMethod );

        if ( enumClass == null || !enumClass.isEnum() ) {
            return null;
        }

        return enumClass;
    }

    @Nullable
    PsiElement resolveInternal(@NotNull String value, @NotNull PsiClass enumClass) {
        PsiField field = enumClass.findFieldByName( value, false );

        if ( field instanceof PsiEnumConstant ) {
            return field;
        }

        return null;
    }

    PsiClass getEnumClass(@NotNull PsiMethod mappingMethod) {
        PsiClass enumClass = determineEnumClass( mappingMethod );

        if ( enumClass == null || !enumClass.isEnum() ) {
            return null;
        }
        return enumClass;
    }

    abstract PsiClass determineEnumClass(@NotNull PsiMethod mappingMethod);

    @NotNull
    @Override
    public final Object[] getVariants() {
        PsiMethod mappingMethod = getMappingMethod();
        if ( mappingMethod == null ) {
            return LookupElement.EMPTY_ARRAY;
        }
        PsiClass enumClass = getEnumClass( mappingMethod );
        if ( enumClass == null ) {
            return LookupElement.EMPTY_ARRAY;
        }
        return getVariantsInternal( mappingMethod, enumClass );
    }

    @NotNull
    abstract Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod, @NotNull PsiClass enumClass);

    @NotNull
    @Override
    public String getUnresolvedMessagePattern() {
        //noinspection UnresolvedPropertyKey
        return MapStructBundle.message( "unknown.enum.constant" );
    }

    private static boolean isNotValueMapping(@Nullable PsiMethod mappingMethod) {
        return mappingMethod == null || mappingMethod.getParameterList().getParametersCount() != 1;
    }
}
