/*
 *  Copyright 2017 the MapStruct authors (http://www.mapstruct.org/)
 *  and/or other contributors as indicated by the @authors tag. See the
 *  copyright.txt file in the distribution for a full listing of all
 *  contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
