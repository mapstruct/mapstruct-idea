/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import java.util.Optional;
import java.util.stream.Stream;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiModifierListOwner;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapStructBundle;
import org.mapstruct.intellij.util.MapStructVersion;

import static com.intellij.codeInsight.AnnotationUtil.getStringAttributeValue;
import static org.mapstruct.intellij.util.MapstructAnnotationUtils.extractValueMappingAnnotationsFromMappings;
import static org.mapstruct.intellij.util.MapstructAnnotationUtils.findAllDefinedValueMappingAnnotations;
import static org.mapstruct.intellij.util.MapstructUtil.VALUE_MAPPINGS_ANNOTATION_FQN;
import static org.mapstruct.intellij.util.MapstructUtil.VALUE_MAPPING_ANNOTATION_FQN;

public class ValueMappingSourceMappedMoreThanOnceInspection extends MoreThanOnceMappedAnnotationInspectionBase<String> {

    @NotNull
    @Override
    protected String getSingleMappingAnnotationFqn() {
        return VALUE_MAPPING_ANNOTATION_FQN;
    }

    @NotNull
    @Override
    protected String getRepeatableMappingsAnnotationFqn() {
        return VALUE_MAPPINGS_ANNOTATION_FQN;
    }

    @NotNull
    @Override
    protected String getAttributeName() {
        return "source";
    }

    @NotNull
    @Override
    protected Stream<PsiAnnotation> findAllDefinedMappings(@NotNull PsiModifierListOwner owner,
                                                           @NotNull MapStructVersion mapStructVersion) {
        return findAllDefinedValueMappingAnnotations( owner );
    }

    @Override
    protected Optional<String> extractCompareKeyFromAnnotationMember(
            @NotNull PsiAnnotationMemberValue annotationMemberValue) {
        return Optional.ofNullable( getStringAttributeValue( annotationMemberValue ) );
    }

    @Override
    protected LocalQuickFix getChangeTargetQuickFix(@NotNull PsiAnnotationMemberValue problemPsiAnnotationMemberValue) {
        return new ChangeTargetQuickFix( problemPsiAnnotationMemberValue );
    }

    @Override
    protected String getProblemDescription(@NotNull String problemKey) {
        return MapStructBundle.message( "inspection.value.mapping.source.mapped.more.than.once", problemKey );
    }

    @NotNull
    @Override
    protected Stream<PsiAnnotation> extractAnnotationsFromRepeatableMappingsAnnotation(
            @NotNull PsiAnnotation mappings) {
        return extractValueMappingAnnotationsFromMappings( mappings );
    }

    private static class ChangeTargetQuickFix extends ChangeTargetStringQuickFixBase {

        private ChangeTargetQuickFix(@NotNull PsiAnnotationMemberValue element) {
            super(
                    element,
                    MapStructBundle.message( "intention.change.source.property" ),
                    MapStructBundle.message( "inspection.value.mapping.source.mapped.more.than.once",
                            element.getText() )
            );
        }
    }
}
