/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ContributedReferenceHost;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.codeinsight.references.BaseReference;
import org.mapstruct.intellij.codeinsight.references.BaseValueMappingReference;

/**
 * Inspection that checks if mapstruct references can be resolved.
 * @see BaseReference
 * @author hduelme
 */
public class MapstructReferenceInspection extends InspectionBase {

    @Override
    @NotNull PsiElementVisitor buildVisitorInternal(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new MapstructReferenceVisitor(holder);
    }

    private static class MapstructReferenceVisitor extends PsiElementVisitor {

        private final ProblemsHolder holder;

        private MapstructReferenceVisitor(ProblemsHolder holder) {
            this.holder = holder;
        }

        /**
         * Based on org.intellij.plugins.intelliLang.references.InjectedReferencesInspection
         */
        @Override
        public void visitElement(@NotNull PsiElement element) {
            if (element instanceof ContributedReferenceHost r && element instanceof PsiLanguageInjectionHost) {
                for (PsiReference psiReference : r.getReferences()) {
                    if ( psiReference instanceof BaseReference baseReference && psiReference.resolve() == null ) {
                        TextRange range = psiReference.getRangeInElement();
                        if (range.isEmpty() && range.getStartOffset() == 1 && "\"\"".equals( element.getText() ) ) {
                            String message = ProblemsHolder.unresolvedReferenceMessage( baseReference );
                            holder.registerProblem( element, message, ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                                    TextRange.create( 0, 2 ) );
                        }
                        else if ( shouldRegisterProblem( baseReference ) ) {
                            holder.registerProblem( psiReference );
                        }
                    }
                }
            }
            super.visitElement( element );
        }

        private boolean shouldRegisterProblem(BaseReference reference) {
            if ( reference instanceof BaseValueMappingReference valueMappingReference ) {
                return valueMappingReference.getEnumClass() != null;
            }

            return !containingClassIsAnnotationType( reference.getElement() );
        }

        private boolean containingClassIsAnnotationType(PsiElement element) {

            PsiClass containingClass = PsiTreeUtil.getParentOfType( element, PsiClass.class );

            if ( containingClass == null ) {
                return false;
            }

            return containingClass.isAnnotationType();
        }
    }
}
