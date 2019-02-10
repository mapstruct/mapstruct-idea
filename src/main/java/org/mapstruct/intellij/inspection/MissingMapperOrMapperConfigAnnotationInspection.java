/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInsight.intention.AddAnnotationPsiFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapstructUtil;

import static org.mapstruct.intellij.util.MapstructUtil.isMapper;
import static org.mapstruct.intellij.util.MapstructUtil.isMapperConfig;

/**
 * Inspection that checks if a mapping class (a class that contains at lease one mapping method) is anntoated with
 * {@link org.mapstruct.Mapper} or {@link org.mapstruct.MapperConfig}.
 *
 * @author Filip Hrisafov
 */
public class MissingMapperOrMapperConfigAnnotationInspection extends InspectionBase {

    @NotNull
    @Override
    PsiElementVisitor buildVisitorInternal(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new MyJavaElementVisitor( holder );
    }

    private static class MyJavaElementVisitor extends JavaElementVisitor {

        private final ProblemsHolder holder;

        MyJavaElementVisitor(ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass( aClass );
            if ( aClass.getNameIdentifier() == null
                || !aClass.isValid()
                || isMapper( aClass )
                || isMapperConfig( aClass ) ) {
                return;
            }

            for ( PsiMethod method : aClass.getAllMethods() ) {
                if ( MapstructUtil.isMappingMethod( method ) ) {
                    holder.registerProblem(
                        aClass.getNameIdentifier(),
                        MapStructBundle.message( "inspection.missing.annotation" ),
                        new AddAnnotationPsiFix(
                            MapstructUtil.MAPPER_ANNOTATION_FQN,
                            aClass,
                            PsiNameValuePair.EMPTY_ARRAY
                        ),
                        new AddAnnotationPsiFix(
                            MapstructUtil.MAPPER_CONFIG_ANNOTATION_FQN,
                            aClass,
                            PsiNameValuePair.EMPTY_ARRAY
                        )
                    );
                    break;
                }
            }
        }
    }
}
