/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import java.util.List;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.ide.util.PropertiesComponent;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.settings.ProjectSettings;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class UnmappedTargetPropertiesInspectionSourceBeforeTargetTest extends BaseInspectionTest {

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

    @Override
    public void tearDown() throws Exception {
        PropertiesComponent.getInstance( myFixture.getProject() )
            .unsetValue( ProjectSettings.PREFER_SOURCE_BEFORE_TARGET_IN_MAPPING );

        super.tearDown();
    }

    public void testUnmappedTargetPropertiesSourceBeforeTarget() {
        ProjectSettings.setPreferSourceBeforeTargetInMapping( myFixture.getProject(), true );
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
            .extracting( IntentionAction::getText )
            .as( "Intent Text" )
            .containsExactly(
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'",
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'",
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'"
            );

        allQuickFixes.forEach( myFixture::launchAction );
        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
