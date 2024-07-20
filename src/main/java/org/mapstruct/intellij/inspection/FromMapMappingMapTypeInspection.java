/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapStructVersion;
import org.mapstruct.intellij.util.MapstructUtil;

import java.util.Set;

import static com.intellij.psi.PsiElementFactory.getInstance;
import static org.mapstruct.intellij.util.MapstructUtil.getSourceParameters;
import static org.mapstruct.intellij.util.SourceUtils.findAllDefinedMappingSources;
import static org.mapstruct.intellij.util.SourceUtils.getGenericTypes;
import static org.mapstruct.intellij.util.TargetUtils.findAllTargetProperties;
import static org.mapstruct.intellij.util.TargetUtils.getTargetType;

/**
 * @author hduelme
 */
public class FromMapMappingMapTypeInspection extends InspectionBase {

    @NotNull
    @Override
    PsiElementVisitor buildVisitorInternal(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new MyJavaElementVisitor( holder, MapstructUtil.resolveMapStructProjectVersion( holder.getFile() ) );
    }

    private static class MyJavaElementVisitor extends JavaElementVisitor {
        private final ProblemsHolder holder;
        private final MapStructVersion mapStructVersion;

        private MyJavaElementVisitor(ProblemsHolder holder, MapStructVersion mapStructVersion) {
            this.holder = holder;
            this.mapStructVersion = mapStructVersion;
        }

        @Override
        public void visitMethod(@NotNull PsiMethod method) {
            super.visitMethod( method );

            if (!MapstructUtil.isMapper( method.getContainingClass() ) ) {
                return;
            }

            PsiType targetType = getTargetType( method );
            if (targetType == null) {
                return;
            }

            PsiParameter fromMapMappingParameter = getFromMapMappingParameter( method );
            if (fromMapMappingParameter == null) {
                return;
            }
            PsiType[] parameters = getGenericTypes( fromMapMappingParameter );
            if (parameters == null)  {
                return;
            }
            Set<String> allTargetProperties = findAllTargetProperties( targetType, mapStructVersion, method );
            if ( allTargetProperties.contains( fromMapMappingParameter.getName() ) ) {
                return;
            }
            if ( findAllDefinedMappingSources( method, mapStructVersion )
                    .anyMatch( source -> fromMapMappingParameter.getName().equals( source ) ) ) {
                return;
            }
            if (parameters.length == 0) {
                // handle raw type
                holder.registerProblem( fromMapMappingParameter,
                        MapStructBundle.message( "inspection.wrong.map.mapping.map.type.raw" ),
                        new ReplaceByStringStringMapTypeFix( fromMapMappingParameter ) );
            }
            else if (parameters.length == 2) {
                // only if both parameters of the map are set
                PsiType keyParameter = parameters[0];
                if ( !keyParameter.equalsToText( "java.lang.String" ) ) {
                    // handle wrong map key type
                    holder.registerProblem( fromMapMappingParameter,
                            MapStructBundle.message( "inspection.wrong.map.mapping.map.key" ),
                            new ReplaceMapKeyByStringTypeFix( fromMapMappingParameter ) );
                }
            }
        }

        @Nullable
        private static  PsiParameter getFromMapMappingParameter(@NotNull PsiMethod method) {
            PsiParameter[]  sourceParameters = getSourceParameters( method );
            if (sourceParameters.length == 1) {
                PsiParameter parameter = sourceParameters[0];
                if (parameter != null && PsiType.getTypeByName( "java.util.Map", method.getProject(),
                        method.getResolveScope() ).isAssignableFrom( parameter.getType() ) ) {
                    return parameter;
                }
            }
            return null;
        }

        private static class ReplaceByStringStringMapTypeFix extends LocalQuickFixOnPsiElement {

            private final String text;

            private ReplaceByStringStringMapTypeFix(@NotNull PsiParameter element) {
                super( element );
                this.text = MapStructBundle.message( "inspection.wrong.map.mapping.map.type.raw.set.default",
                        element.getType().getPresentableText() );
            }

            @Override
            public @IntentionName @NotNull String getText() {
                return text;
            }

            @Override
            public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file,
                                       @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
                if (!super.isAvailable( project, file, startElement, endElement ) ) {
                    return false;
                }
                if (startElement instanceof PsiParameter parameter) {
                    PsiType[] parameters = getGenericTypes( parameter );
                    return parameters != null && parameters.length == 0;
                }
                return false;
            }

            @Override
            public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull PsiElement psiElement,
                               @NotNull PsiElement psiElement1) {
                if (psiElement instanceof PsiParameter) {
                    String mapText = psiElement.getText();
                    String prefix = mapText.substring( 0, mapText.indexOf( ' ' ) );
                    String end = mapText.substring( mapText.lastIndexOf( ' ' ) );
                    String result = prefix + "<String, String>" + end;
                    psiElement.replace( getInstance( project ).createParameterFromText( result, psiElement ) );
                }
            }

            @Override
            public @IntentionFamilyName @NotNull String getFamilyName() {
                return MapStructBundle.message( "intention.wrong.map.mapping.map.type.raw" );
            }
        }

        private static class ReplaceMapKeyByStringTypeFix extends LocalQuickFixOnPsiElement {

            private final String text;

            private ReplaceMapKeyByStringTypeFix(@NotNull PsiParameter element) {
                super( element );
                this.text = MapStructBundle.message( "inspection.wrong.map.mapping.map.key.change.to.string" );
            }

            @Override
            public @IntentionName @NotNull String getText() {
                return text;
            }

            @Override
            public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file,
                                       @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
                if (!super.isAvailable( project, file, startElement, endElement ) ) {
                    return false;
                }
                if (startElement instanceof PsiParameter parameter) {
                    PsiType[] parameters = getGenericTypes( parameter );
                    if (parameters == null || parameters.length != 2) {
                        return false;
                    }
                    return !parameters[0].equalsToText( "java.lang.String" );
                }
                return false;
            }

            @Override
            public void invoke(@NotNull Project project, @NotNull PsiFile psiFile, @NotNull PsiElement psiElement,
                               @NotNull PsiElement psiElement1) {
                if (psiElement instanceof PsiParameter) {
                    String mapText = psiElement.getText();
                    String prefix = mapText.substring( 0, mapText.indexOf( '<' ) + 1 );
                    String end = mapText.substring( mapText.indexOf( ',' ) );
                    String result = prefix + "String" + end;
                    psiElement.replace( getInstance( project ).createParameterFromText( result, psiElement ) );
                }
            }

            @Override
            public @IntentionFamilyName @NotNull String getFamilyName() {
                return MapStructBundle.message( "intention.wrong.map.mapping.map.key" );
            }
        }
    }

}
