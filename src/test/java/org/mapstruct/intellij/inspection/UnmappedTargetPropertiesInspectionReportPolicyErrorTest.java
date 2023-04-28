/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.pom.java.LanguageLevel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests if unmappedTargetPolicy set to ERROR, results in reported errors instead of warnings.
 * @author hduelme
 */
public class UnmappedTargetPropertiesInspectionReportPolicyErrorTest extends BaseInspectionTest {

    @Override
    protected LanguageLevel getLanguageLevel() {
        return LanguageLevel.JDK_1_7;
    }

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

    public void testUnmappedTargetPropertiesReportPolicyError() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
            .extracting( IntentionAction::getText )
            .as( "Intent Text" )
            .containsExactly(
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'",
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'",
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'",
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'",
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'",
                "Ignore all unmapped target properties",
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'",
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'",
                "Ignore all unmapped target properties",
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'",
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'",
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'"
            );

        allQuickFixes.forEach( myFixture::launchAction );
        String testName = getTestName( false );
        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
