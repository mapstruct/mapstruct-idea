/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapStructVersion;
import org.mapstruct.intellij.util.MapstructUtil;
import org.mapstruct.intellij.util.TargetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intellij.codeInsight.AnnotationUtil.getStringAttributeValue;
import static org.mapstruct.intellij.util.MapstructAnnotationUtils.extractMappingAnnotationsFromMappings;
import static org.mapstruct.intellij.util.MapstructUtil.MAPPINGS_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.MAPPING_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.TargetUtils.getTargetType;

/**
 * @author hduelme
 */
public class TargetPropertyMappedMoreThanOnceInspection extends InspectionBase {
    @NotNull
    @Override
    PsiElementVisitor buildVisitorInternal(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new TargetPropertyMappedMoreThanOnceInspection.MyJavaElementVisitor( holder,
                MapstructUtil.resolveMapStructProjectVersion( holder.getFile() ) );
    }

    private static class MyJavaElementVisitor extends JavaElementVisitor {
        private final ProblemsHolder holder;
        private final MapStructVersion mapStructVersion;

        private MyJavaElementVisitor(ProblemsHolder holder, MapStructVersion mapStructVersion) {
            this.holder = holder;
            this.mapStructVersion = mapStructVersion;
        }

        @Override
        public void visitMethod(PsiMethod method) {
            if ( !MapstructUtil.isMapper( method.getContainingClass() ) ) {
                return;
            }
            PsiType targetType = getTargetType( method );
            if ( targetType == null ) {
                return;
            }
            Map<String, List<PsiElement>> problemMap = new HashMap<>();
            for (PsiAnnotation psiAnnotation :method.getAnnotations()) {
                String qualifiedName = psiAnnotation.getQualifiedName();
                if ( MAPPING_ANNOTATION_FQN.equals( qualifiedName ) ) {
                    handleMappingAnnotation( psiAnnotation, problemMap );
                }
                else if (MAPPINGS_ANNOTATION_FQN.equals( qualifiedName )) {
                    extractMappingAnnotationsFromMappings( psiAnnotation )
                            .forEach( a -> handleMappingAnnotation( a, problemMap ) );
                }
                else {
                    // Handle annotations containing at least one Mapping annotation
                    handleAnnotationWithMappingAnnotation( psiAnnotation, problemMap );
                }
            }
            for (Map.Entry<String, List<PsiElement>> problem : problemMap.entrySet()) {
                List<PsiElement> problemElements = problem.getValue();
                if (problemElements.size() > 1) {
                    for (PsiElement problemElement : problemElements) {
                       holder.registerProblem( problemElement,
                               MapStructBundle.message( "inspection.target.property.mapped.more.than.once",
                                       problem.getKey() ) );
                    }
                }
            }
        }

        private void handleAnnotationWithMappingAnnotation(PsiAnnotation psiAnnotation,
                                                           Map<String, List<PsiElement>> problemMap) {
            PsiClass annotationClass = psiAnnotation.resolveAnnotationType();
            if (annotationClass == null) {
                return;
            }
            TargetUtils.findAllDefinedMappingTargets( annotationClass, mapStructVersion )
                    .forEach( target ->
                            problemMap.computeIfAbsent( target, k -> new ArrayList<>() ).add( psiAnnotation ) );
        }

        private static void handleMappingAnnotation(PsiAnnotation psiAnnotation,
                                                    Map<String, List<PsiElement>> problemMap) {
            PsiAnnotationMemberValue value = psiAnnotation.findDeclaredAttributeValue( "target" );
            if (value != null) {
                String target = getStringAttributeValue( value );
                if (target != null) {
                    problemMap.computeIfAbsent( target, k -> new ArrayList<>() ).add( value );
                }
            }

        }
    }
}
