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
public class UnmappedTargetPropertiesWithMetaAnnotationInspectionTest extends BaseInspectionTest {

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
            "org/example/data/UnmappedConstructorTargetPropertiesData.java"
        );

        myFixture.copyFileToProject(
            "MetaMappingIgnoreTestName.java",
            "org/example/data/IgnoreTestName.java"
        );
    }

    public void testUnmappedTargetPropertiesWithMetaAnnotation() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
            .extracting( IntentionAction::getText )
            .as( "Intent Text" )
            .containsExactly(
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'"
            );

        allQuickFixes.forEach( myFixture::launchAction );
        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
