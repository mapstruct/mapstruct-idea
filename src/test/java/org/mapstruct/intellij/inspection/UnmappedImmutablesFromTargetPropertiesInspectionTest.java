/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import java.util.List;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.roots.DependencyScope;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.testFramework.fixtures.MavenDependencyUtil;
import org.jetbrains.annotations.NotNull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephan Leicht Vogt
 */
public class UnmappedImmutablesFromTargetPropertiesInspectionTest extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<UnmappedTargetPropertiesInspection> getInspection() {
        return UnmappedTargetPropertiesInspection.class;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ModuleRootModificationUtil.updateModel( getModule(), model -> {
            MavenDependencyUtil.addFromMaven( model, "org.immutables:value:2.10.1", false, DependencyScope.PROVIDED );
        } );

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
