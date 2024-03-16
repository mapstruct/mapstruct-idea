package org.mapstruct.intellij.inspection;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.intention.QuickFixFactory;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapStructVersion;
import org.mapstruct.intellij.util.MapstructAnnotationUtils;
import org.mapstruct.intellij.util.MapstructUtil;
import org.mapstruct.intellij.util.TargetUtils;

import java.util.Set;

import static org.mapstruct.intellij.util.TargetUtils.getTargetType;

public class NonExistingTargetPropertiesInspection extends MappingAnnotationInspectionBase {

  @Override
  void visitMappingAnnotation(
    @NotNull final ProblemsHolder problemsHolder,
    @NotNull final PsiAnnotation psiAnnotation,
    @NotNull final MappingAnnotation mappingAnnotation) {

    MapStructVersion version = MapstructUtil.resolveMapStructProjectVersion(problemsHolder.getFile());

    PsiNameValuePair targetProperty = mappingAnnotation.getTargetProperty();
    if (targetProperty != null) {

      PsiMethod method = MapstructAnnotationUtils.getAnnotatedMethod(psiAnnotation);
      if (method != null) {
        PsiType targetType = getTargetType(method);
        if (targetType != null && targetProperty.getValue() != null) {
          Set<String> targets = TargetUtils.findAllTargetProperties(targetType, version, method);

          String value = AnnotationUtil.getStringAttributeValue(targetProperty.getValue());

          if (!targets.contains(value)) {

            LocalQuickFix quickFix = QuickFixFactory.getInstance().createDeleteFix(
              psiAnnotation,
              MapStructBundle.message(
                "intention.remove.non.existing.mapping.declaration",
                value
              )
            );

            problemsHolder.registerProblem(
              targetProperty.getValue(),
              MapStructBundle.message(
                "inspection.non.existing.target.property",
                value
              ),
              ProblemHighlightType.ERROR,
              quickFix
            );
          }
        }
      }
    }
  }

  private static class RemoveMappingQuickFix extends LocalQuickFixOnPsiElement {

    private RemoveMappingQuickFix(PsiElement element) {
      super(element);
    }

    static LocalQuickFixOnPsiElement createRemoveMappingQuickFix(PsiElement element) {
      return new RemoveMappingQuickFix(element);
    }

    @Override
    public @IntentionName @NotNull String getText() {
      return "Remove @Mapping declaration";
    }

    @Override
    public void invoke(
      @NotNull final Project project,
      @NotNull final PsiFile psiFile,
      @NotNull final PsiElement psiElement,
      @NotNull final PsiElement psiElement1) {

      psiElement1.delete();
    }

    @Override
    public @IntentionFamilyName @NotNull String getFamilyName() {
      return "IntentionFamilyName";
    }
  }
}
