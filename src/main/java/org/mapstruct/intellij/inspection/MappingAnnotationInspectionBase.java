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
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.impl.source.tree.java.PsiAnnotationParamListImpl;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.util.MapstructUtil;

import static com.intellij.psi.PsiElementFactory.getInstance;

public abstract class MappingAnnotationInspectionBase extends InspectionBase {

    @Override
    @NotNull PsiElementVisitor buildVisitorInternal( @NotNull ProblemsHolder holder, boolean isOnTheFly ) {
        return new MappingAnnotationInspectionBase.MyJavaElementVisitor( holder );
    }

    private class MyJavaElementVisitor extends JavaElementVisitor {
        private final ProblemsHolder problemsHolder;

        private MyJavaElementVisitor( ProblemsHolder problemsHolder ) {
            this.problemsHolder = problemsHolder;
        }

        @Override
        public void visitAnnotation(@NotNull PsiAnnotation annotation ) {
            super.visitAnnotation( annotation );
            if (annotation.hasQualifiedName( MapstructUtil.MAPPING_ANNOTATION_FQN )) {
                MappingAnnotation mappingAnnotation = new MappingAnnotation();
                for (JvmAnnotationAttribute annotationAttribute : annotation.getAttributes()) {
                    // exclude not written attributes. They result in a syntax error
                    if (annotationAttribute instanceof PsiNameValuePair nameValuePair
                            && annotationAttribute.getAttributeValue() != null) {
                        switch (nameValuePair.getAttributeName()) {
                            case "target" :
                                mappingAnnotation.setTargetProperty( nameValuePair );
                                break;
                            case "source":
                                mappingAnnotation.setSourceProperty( nameValuePair );
                                break;
                            case "constant":
                                mappingAnnotation.setConstantProperty( nameValuePair );
                                break;
                            case "expression":
                                mappingAnnotation.setExpressionProperty( nameValuePair );
                                break;
                            case "defaultValue":
                                mappingAnnotation.setDefaultValueProperty( nameValuePair );
                                break;
                            case "defaultExpression":
                                mappingAnnotation.setDefaultExpressionProperty( nameValuePair );
                                break;
                            case "ignore":
                                mappingAnnotation.setIgnoreProperty( nameValuePair );
                                break;
                            case "dependsOn":
                                mappingAnnotation.setDependsOnProperty( nameValuePair );
                                break;
                            case "qualifiedByName":
                                mappingAnnotation.setQualifiedByNameProperty( nameValuePair );
                                break;
                            case "conditionExpression":
                                mappingAnnotation.setConditionExpression( nameValuePair );
                                break;
                            default:
                                break;
                        }
                    }
                }

                visitMappingAnnotation( problemsHolder, annotation,  mappingAnnotation );
            }
        }

    }

    abstract void visitMappingAnnotation( @NotNull ProblemsHolder problemsHolder, @NotNull PsiAnnotation psiAnnotation,
                                          @NotNull MappingAnnotation mappingAnnotation );

    protected static class MappingAnnotation {
        private PsiNameValuePair targetProperty;
        private PsiNameValuePair sourceProperty;
        private PsiNameValuePair constantProperty;
        private PsiNameValuePair defaultValueProperty;
        private PsiNameValuePair expressionProperty;
        private PsiNameValuePair defaultExpressionProperty;
        private PsiNameValuePair ignoreProperty;
        private PsiNameValuePair dependsOnProperty;
        private PsiNameValuePair qualifiedByNameProperty;
        private PsiNameValuePair conditionExpression;

        public PsiNameValuePair getTargetProperty() {
            return targetProperty;
        }

        public void setTargetProperty( PsiNameValuePair targetProperty ) {
            this.targetProperty = targetProperty;
        }

        public boolean isNotThisTarget() {
            return targetProperty == null || !".".equals( targetProperty.getLiteralValue() );
        }

        public PsiNameValuePair getSourceProperty() {
            return sourceProperty;
        }

        public void setSourceProperty( PsiNameValuePair sourceProperty ) {
            this.sourceProperty = sourceProperty;
        }

        public PsiNameValuePair getConstantProperty() {
            return constantProperty;
        }

        public void setConstantProperty( PsiNameValuePair constantProperty ) {
            this.constantProperty = constantProperty;
        }

        public PsiNameValuePair getDefaultValueProperty() {
            return defaultValueProperty;
        }

        public void setDefaultValueProperty( PsiNameValuePair defaultValueProperty ) {
            this.defaultValueProperty = defaultValueProperty;
        }

        public PsiNameValuePair getExpressionProperty() {
            return expressionProperty;
        }

        public void setExpressionProperty( PsiNameValuePair expressionProperty ) {
            this.expressionProperty = expressionProperty;
        }

        public PsiNameValuePair getDefaultExpressionProperty() {
            return defaultExpressionProperty;
        }

        public void setDefaultExpressionProperty( PsiNameValuePair defaultExpressionProperty ) {
            this.defaultExpressionProperty = defaultExpressionProperty;
        }

        public PsiNameValuePair getIgnoreProperty() {
            return ignoreProperty;
        }

        public void setIgnoreProperty( PsiNameValuePair ignoreProperty ) {
            this.ignoreProperty = ignoreProperty;
        }

        public boolean hasNoSourceProperties() {
            return sourceProperty == null && defaultValueProperty == null && expressionProperty == null
                    && ignoreProperty == null && constantProperty == null && dependsOnProperty == null
                    && qualifiedByNameProperty == null;
        }

        public boolean hasNoDefaultProperties() {
            return defaultValueProperty == null && defaultExpressionProperty == null;
        }

        public PsiNameValuePair getDependsOnProperty() {
            return dependsOnProperty;
        }

        public void setDependsOnProperty(PsiNameValuePair dependsOnProperty) {
            this.dependsOnProperty = dependsOnProperty;
        }

        public PsiNameValuePair getQualifiedByNameProperty() {
            return qualifiedByNameProperty;
        }

        public void setQualifiedByNameProperty(PsiNameValuePair qualifiedByNameProperty) {
            this.qualifiedByNameProperty = qualifiedByNameProperty;
        }

        public PsiNameValuePair getConditionExpression() {
            return conditionExpression;
        }

        public void setConditionExpression(PsiNameValuePair conditionExpression) {
            this.conditionExpression = conditionExpression;
        }
    }

    protected static RemoveAnnotationAttributeQuickFix createRemoveAnnotationAttributeQuickFix(
            @NotNull PsiNameValuePair annotationAttribute, @NotNull String text, @NotNull String family ) {
        return new RemoveAnnotationAttributeQuickFix( annotationAttribute, text, family );
    }

    protected static ReplaceAsDefaultValueQuickFix createReplaceAsDefaultValueQuickFix(
            @NotNull  PsiNameValuePair annotationAttribute,  @NotNull String source,
            @NotNull String target,  @NotNull String text, @NotNull String family ) {
        return new ReplaceAsDefaultValueQuickFix( annotationAttribute, source, target, text, family );
    }

    protected static class RemoveAnnotationAttributeQuickFix extends LocalQuickFixOnPsiElement {
        private final String text;
        private final String family;

        private RemoveAnnotationAttributeQuickFix( @NotNull PsiNameValuePair element, @NotNull String text,
                                                   @NotNull String family) {
            super( element );
            this.text = text;
            this.family = family;
        }

        @Override
        public boolean isAvailable( @NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement,
                                    @NotNull PsiElement endElement ) {
            return startElement.isValid();
        }

        @Override
        public @IntentionName @NotNull String getText() {
            return text;
        }

        @Override
        public void invoke( @NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement,
                            @NotNull PsiElement endElement ) {
            startElement.delete();
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return family;
        }

        @Override
        public boolean availableInBatchMode() {
            return false;
        }
    }

    protected static class ReplaceAsDefaultValueQuickFix extends LocalQuickFixOnPsiElement {

        private final String source;
        private final String target;
        private final String text;
        private final String family;

        private ReplaceAsDefaultValueQuickFix( @NotNull PsiNameValuePair element, @NotNull String source,
                                               @NotNull String target, @NotNull String text,
                                               @NotNull String family) {
            super( element );
            this.source = source;
            this.target = target;
            this.text = text;
            this.family = family;
        }

        @Override
        public boolean isAvailable( @NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement,
                                    @NotNull PsiElement endElement ) {
            if ( !endElement.isValid() ) {
                return false;
            }
            PsiElement parent = endElement.getParent();
            return parent.isValid() && parent instanceof PsiAnnotationParamListImpl;
        }

        @Override
        public @IntentionName @NotNull String getText() {
            return  text;
        }

        @Override
        public void invoke( @NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement,
                            @NotNull PsiElement endElement ) {
            if (endElement instanceof PsiNameValuePair end) {
                PsiAnnotationParamListImpl parent = (PsiAnnotationParamListImpl) end.getParent();
                PsiElement parent1 = parent.getParent();

                // don't replace inside of strings. Only the constant value name
                String annotationText = parent1.getText().replaceFirst( "(?<!\")\\s*,?\\s*" + source + "\\s*=\\s*",
                        target + " = " );
                parent1.replace( getInstance( project ).createAnnotationFromText( annotationText, parent1 ) );
            }
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return family;
        }

        @Override
        public boolean availableInBatchMode() {
            return false;
        }
    }

}
