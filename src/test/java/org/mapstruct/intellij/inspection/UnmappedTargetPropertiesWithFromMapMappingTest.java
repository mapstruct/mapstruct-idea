/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInsight.intention.IntentionAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hduelme
 */
public class UnmappedTargetPropertiesWithFromMapMappingTest  extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<UnmappedTargetPropertiesInspection> getInspection() {
        return UnmappedTargetPropertiesInspection.class;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(
                "UnmappedTargetPropertiesData.java",
                "org/example/data/UnmappedTargetPropertiesData.java"
        );
    }

    /**
     * Tests if no unmapped target properties warnings are generated when source is map
     */
    public void testUnmappedTargetPropertiesWithFromMapMapping() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes ).isEmpty();
    }

    public void testUnmappedTargetPropertiesWithFromMapMappingMultiSource() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes ).isEmpty();
    }

    public void testUnmappedTargetPropertiesWithFromMapMappingMultiSourceWrongKeyType() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly(
                        "Ignore unmapped target property: 'matching'",
                        "Add unmapped target property: 'matching'",

                        "Ignore unmapped target property: 'moreTarget'",
                        "Add unmapped target property: 'moreTarget'",

                        "Ignore unmapped target property: 'testName'",
                        "Add unmapped target property: 'testName'",
                        "Ignore all unmapped target properties",

                        "Ignore unmapped target property: 'matching'",
                        "Add unmapped target property: 'matching'",

                        "Ignore unmapped target property: 'moreTarget'",
                        "Add unmapped target property: 'moreTarget'",

                        "Ignore unmapped target property: 'testName'",
                        "Add unmapped target property: 'testName'",
                        "Ignore all unmapped target properties"
                );
    }
}
