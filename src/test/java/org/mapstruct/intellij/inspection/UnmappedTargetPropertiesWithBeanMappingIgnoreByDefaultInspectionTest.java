/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import java.util.List;

import com.intellij.codeInsight.intention.IntentionAction;
import org.jetbrains.annotations.NotNull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class UnmappedTargetPropertiesWithBeanMappingIgnoreByDefaultInspectionTest extends BaseInspectionTest {

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

    public void testUnmappedTargetPropertiesWithBeanMappingIgnoreByDefault() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
            .extracting( IntentionAction::getText )
            .as( "Intent Text" )
            .containsExactly(
                // For SingleMappingMapper
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'",

                // For UpdateMapper
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'"
            );

        allQuickFixes.forEach( myFixture::launchAction );
        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
