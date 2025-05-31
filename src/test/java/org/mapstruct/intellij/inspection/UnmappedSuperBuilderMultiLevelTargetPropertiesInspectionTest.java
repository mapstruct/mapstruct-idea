/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import java.util.List;

import com.intellij.codeInsight.intention.IntentionAction;
import org.jetbrains.annotations.NotNull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oliver Erhart
 */
public class UnmappedSuperBuilderMultiLevelTargetPropertiesInspectionTest extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<UnmappedTargetPropertiesInspection> getInspection() {
        return UnmappedTargetPropertiesInspection.class;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(
            "UnmappedSuperBuilderMultiLevelTargetPropertiesData.java",
            "org/example/data/UnmappedSuperBuilderMultiLevelTargetPropertiesData.java"
        );
    }

    public void testUnmappedSuperBuilderMultiLevelTargetProperties() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
            .extracting( IntentionAction::getText )
            .as( "Intent Text" )
            .containsExactly(
                "Ignore unmapped target property: 'age'",
                "Add unmapped target property: 'age'",
                "Ignore unmapped target property: 'id'",
                "Add unmapped target property: 'id'",
                "Ignore unmapped target property: 'name'",
                "Add unmapped target property: 'name'",
                "Ignore all unmapped target properties"
            );
    }
}
