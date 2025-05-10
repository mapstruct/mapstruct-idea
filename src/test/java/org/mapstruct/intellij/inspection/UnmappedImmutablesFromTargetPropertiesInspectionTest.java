/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephan Leicht Vogt
 */
public class UnmappedImmutablesFromTargetPropertiesInspectionTest extends BaseInspectionTest {

    private static final String BUILD_TEST_LIBS_DIRECTORY = "build/test-libs";

    @NotNull
    @Override
    protected Class<UnmappedTargetPropertiesInspection> getInspection() {
        return UnmappedTargetPropertiesInspection.class;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String immutablesLibPath = PathUtil.toSystemIndependentName(
            new File( BUILD_TEST_LIBS_DIRECTORY ).getAbsolutePath()
        );
        PsiTestUtil.addLibrary(
            myFixture.getProjectDisposable(),
            myFixture.getModule(),
            "Immutables",
            immutablesLibPath,
            "immutables.jar"
        );
        myFixture.copyFileToProject(
            "UnmappedImmutablesFromTargetPropertiesData.java",
            "org/example/data/UnmappedImmutablesFromTargetPropertiesData.java"
        );
    }

    public void testUnmappedImmutablesFromTargetProperties() {
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
