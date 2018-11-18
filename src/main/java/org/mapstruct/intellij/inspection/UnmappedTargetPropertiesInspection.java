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

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapstructUtil;
import org.mapstruct.intellij.util.TargetUtils;

import static org.mapstruct.intellij.util.MapstructAnnotationUtils.addMappingAnnotation;
import static org.mapstruct.intellij.util.MapstructUtil.isInheritInverseConfiguration;
import static org.mapstruct.intellij.util.MapstructUtil.isMapper;
import static org.mapstruct.intellij.util.MapstructUtil.isMapperConfig;
import static org.mapstruct.intellij.util.SourceUtils.findAllSourceProperties;
import static org.mapstruct.intellij.util.TargetUtils.findAllTargetProperties;

/**
 * Inspection that checks if there are unmapped target properties.
 *
 * @author Filip Hrisafov
 */
public class UnmappedTargetPropertiesInspection extends InspectionBase {
    @NotNull
    @Override
    PsiElementVisitor buildVisitorInternal(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new MyJavaElementVisitor( holder, MapstructUtil.isMapStructBuilderSupportPresent( holder.getFile() ) );
    }

    private static class MyJavaElementVisitor extends JavaElementVisitor {
        private final ProblemsHolder holder;
        private final boolean builderSupportPresent;

        private MyJavaElementVisitor(ProblemsHolder holder, boolean builderSupportPresent) {
            this.holder = holder;
            this.builderSupportPresent = builderSupportPresent;
        }

        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod( method );

            PsiType targetType = getTargetType( method );
            if ( targetType == null ) {
                return;
            }

            Set<String> allTargetProperties = findAllTargetProperties( targetType, builderSupportPresent )
                .collect( Collectors.toSet() );

            // find and remove all defined mapping targets
            Set<String> definedTargets = TargetUtils.findAllDefinedMappingTargets( method )
                .collect( Collectors.toSet() );
            allTargetProperties.removeAll( definedTargets );

            //TODO maybe we need to improve this by more granular extraction
            Set<String> sourceProperties = findAllSourceProperties( method )
                .collect( Collectors.toSet() );
            allTargetProperties.removeAll( sourceProperties );

            int missingTargetProperties = allTargetProperties.size();
            if ( missingTargetProperties > 0 ) {
                String messageKey = missingTargetProperties == 1 ? "inspection.unmapped.target.property" :
                    "inspection.unmapped.target.properties.list";
                String descriptionTemplate = MapStructBundle.message(
                    messageKey,
                    allTargetProperties.stream()
                        .sorted()
                        .collect( Collectors.joining( ", " ) )
                );
                UnmappedTargetPropertyFix[] quickFixes = allTargetProperties.stream()
                    .sorted()
                    .flatMap( property -> Stream.of(
                        createAddIgnoreUnmappedTargetPropertyFix( method, property ),
                        createAddUnmappedTargetPropertyFix( method, property )
                    ) )
                    .toArray( UnmappedTargetPropertyFix[]::new );
                //noinspection ConstantConditions
                holder.registerProblem(
                    method.getNameIdentifier(),
                    descriptionTemplate,
                    quickFixes
                );
            }
        }

        /**
         * @param method the method to be used
         *
         * @return the target class for the inspection, or {@code null} if no inspection needs to be performed
         */
        @Nullable
        private static PsiType getTargetType(PsiMethod method) {
            if ( isInheritInverseConfiguration( method ) ) {
                return null;
            }
            PsiClass containingClass = method.getContainingClass();

            if ( containingClass == null
                || method.getNameIdentifier() == null
                || !( isMapper( containingClass ) || isMapperConfig( containingClass ) ) ) {
                return null;
            }
            PsiType targetType = TargetUtils.getRelevantType( method );
            if ( targetType == null ) {
                return null;
            }
            return targetType;
        }
    }

    private static class UnmappedTargetPropertyFix extends LocalQuickFixOnPsiElement {

        private final String myText;
        private final String myFamilyName;
        private final Supplier<PsiAnnotation> myAnnotationSupplier;

        private UnmappedTargetPropertyFix(@NotNull PsiMethod modifierListOwner,
            @NotNull String text,
            @NotNull String familyName,
            @NotNull Supplier<PsiAnnotation> annotationSupplier) {
            super( modifierListOwner );
            myText = text;
            myFamilyName = familyName;
            myAnnotationSupplier = annotationSupplier;
        }

        @NotNull
        @Override
        public String getText() {
            return myText;
        }

        @Nls
        @NotNull
        @Override
        public String getFamilyName() {
            return myFamilyName;
        }

        @Override
        public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement,
            @NotNull PsiElement endElement) {
            if ( !startElement.isValid() ) {
                return false;
            }
            if ( !PsiUtil.isLanguageLevel5OrHigher( startElement ) ) {
                return false;
            }
            final PsiModifierListOwner myModifierListOwner = (PsiModifierListOwner) startElement;

            // e.g. PsiTypeParameterImpl doesn't have modifier list
            return myModifierListOwner.getModifierList() != null;
        }

        @Override
        public void invoke(@NotNull Project project,
            @NotNull PsiFile file,
            @NotNull PsiElement startElement,
            @NotNull PsiElement endElement) {
            PsiMethod mappingMethod = (PsiMethod) startElement;

            addMappingAnnotation( project, mappingMethod, myAnnotationSupplier.get() );
        }

    }

    /**
     * Add unmapped property fix. Property fix that adds a {@link org.mapstruct.Mapping} annotation with the
     * given {@code target}
     *
     * @param method the method to which the property needs to be added
     * @param target the name of the target property
     *
     * @return the Local Quick fix
     */
    private static UnmappedTargetPropertyFix createAddUnmappedTargetPropertyFix(PsiMethod method, String target) {
        String fqn = MapstructUtil.MAPPING_ANNOTATION_FQN;
        Supplier<PsiAnnotation> annotationSupplier = () -> JavaPsiFacade.getElementFactory( method.getProject() )
            .createAnnotationFromText( "@" + fqn + "(target = \"" + target + "\", source=\"\")", null );
        String message = MapStructBundle.message( "inspection.add.unmapped.target.property", target );
        return new UnmappedTargetPropertyFix(
            method,
            message,
            MapStructBundle.message( "intention.add.unmapped.target.property" ),
            annotationSupplier
        );
    }

    /**
     * Add ignore unmapped property fix. Property fix that adds a {@link org.mapstruct.Mapping} annotation that ignores
     * the given {@code target}
     *
     * @param method the method to which the property needs to be added
     * @param target the name of the target property
     *
     * @return the Local Quick fix
     */
    private static UnmappedTargetPropertyFix createAddIgnoreUnmappedTargetPropertyFix(PsiMethod method, String target) {
        String fqn = MapstructUtil.MAPPING_ANNOTATION_FQN;
        Supplier<PsiAnnotation> annotationSupplier = () -> JavaPsiFacade.getElementFactory( method.getProject() )
            .createAnnotationFromText( "@" + fqn + "(target = \"" + target + "\", ignore= true)", null );
        String message = MapStructBundle.message( "inspection.add.ignore.unmapped.target.property", target );
        return new UnmappedTargetPropertyFix(
            method,
            message,
            MapStructBundle.message( "intention.add.ignore.unmapped.target.property" ),
            annotationSupplier
        );
    }
}
