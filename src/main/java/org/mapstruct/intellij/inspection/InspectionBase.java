/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;

import static org.mapstruct.intellij.util.MapstructUtil.isMapStructPresent;

/**
 * Inspection base for MapStruct issues.
 *
 * @author Filip Hrisafov
 */
public abstract class InspectionBase extends LocalInspectionTool {

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return MapStructBundle.message( "group.names.mapstruct.issues" );
    }

    @NotNull
    @Override
    public final PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        if ( !isMapStructPresent( holder.getFile() ) ) {
            return PsiElementVisitor.EMPTY_VISITOR;
        }

        return buildVisitorInternal( holder, isOnTheFly );
    }

    /**
     * This method is only invoked if mapstruct is present in the module of the file being checked
     *
     * @param holder the problem holder
     * @param isOnTheFly true if inspection was run in non-batch mode
     *
     * @return The visitor that needs to be used for the inspection
     *
     * @see InspectionBase#buildVisitor(ProblemsHolder, boolean)
     */
    @NotNull
    abstract PsiElementVisitor buildVisitorInternal(@NotNull ProblemsHolder holder, boolean isOnTheFly);
}
