/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import java.util.List;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.IdeaTestUtil;
import org.jetbrains.annotations.NotNull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class UnmappedTargetPropertiesInspectionTest extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<UnmappedTargetPropertiesInspection> getInspection() {
        return UnmappedTargetPropertiesInspection.class;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        IdeaTestUtil.setModuleLanguageLevel( getModule(), LanguageLevel.JDK_1_7 );
        myFixture.copyFileToProject(
            "UnmappedTargetPropertiesData.java",
            "org/example/data/UnmappedTargetPropertiesData.java"
        );
    }

    @Override
    public void tearDown() throws Exception {
        IdeaTestUtil.setModuleLanguageLevel( getModule(), LanguageLevel.JDK_17 );
        super.tearDown();
    }

    public void testUnmappedTargetProperties() {
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
