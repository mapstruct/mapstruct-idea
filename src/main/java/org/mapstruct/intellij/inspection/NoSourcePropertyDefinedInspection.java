/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationParamListImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapstructAnnotationUtils;
import org.mapstruct.intellij.util.MapstructUtil;

/**
 * Inspection that checks if inside a @Mapping at least one source property is defined
 *
 * @author hduelme
 */
public class NoSourcePropertyDefinedInspection extends MappingAnnotationInspectionBase {

    @Override
    void visitMappingAnnotation( @NotNull ProblemsHolder problemsHolder, @NotNull PsiAnnotation psiAnnotation,
                                 @NotNull MappingAnnotation mappingAnnotation ) {
        if (mappingAnnotation.hasNoSourceProperties() && !isIgnoreByDefaultEnabled( psiAnnotation ) ) {
            problemsHolder.registerProblem( psiAnnotation,
                    MapStructBundle.message( "inspection.no.source.property" ) );
        }
    }

    private static boolean isIgnoreByDefaultEnabled(@NotNull PsiAnnotation psiAnnotation) {
        PsiMethod annotatedMethod = MapstructAnnotationUtils.getAnnotatedMethod(psiAnnotation );
        if (annotatedMethod == null) {
            return false;
        }
        PsiAnnotation beanMappingAnnotation = annotatedMethod.getAnnotation( MapstructUtil.BEAN_MAPPING_FQN );
        if (beanMappingAnnotation == null) {
            return false;
        }
        PsiAnnotationMemberValue ignoreByDefault =
                beanMappingAnnotation.findDeclaredAttributeValue( "ignoreByDefault" );
        return ignoreByDefault instanceof PsiLiteralExpression
                && Boolean.TRUE.equals( ((PsiLiteralExpression) ignoreByDefault).getValue() );
    }
}
