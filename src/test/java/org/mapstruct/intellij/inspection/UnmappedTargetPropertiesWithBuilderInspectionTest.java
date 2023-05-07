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
 * @author Filip Hrisafov
 */
public class UnmappedTargetPropertiesWithBuilderInspectionTest extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<UnmappedTargetPropertiesInspection> getInspection() {
        return UnmappedTargetPropertiesInspection.class;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(
            "UnmappedTargetPropertiesWithBuilderData.java",
            "org/example/data/UnmappedTargetPropertiesWithBuilderData.java"
        );
    }

    public void testUnmappedTargetPropertiesWithBuilder() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
            .extracting( IntentionAction::getText )
            .as( "Intent Text" )
            .containsExactly(
                "Ignore unmapped target property: 'builderTestName'",
                "Add unmapped target property: 'builderTestName'",

                "Ignore unmapped target property: 'targetTestName'",
                "Add unmapped target property: 'targetTestName'",

                "Ignore unmapped target property: 'targetTestName'",
                "Add unmapped target property: 'targetTestName'",

                "Ignore unmapped target property: 'builderTestName'",
                "Add unmapped target property: 'builderTestName'",

                "Ignore unmapped target property: 'targetTestName'",
                "Add unmapped target property: 'targetTestName'"
            );

        allQuickFixes.forEach( myFixture::launchAction );
        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
