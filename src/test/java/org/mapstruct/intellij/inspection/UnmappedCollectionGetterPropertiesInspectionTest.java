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
 * @author Filip Hrisafov
 */
public class UnmappedCollectionGetterPropertiesInspectionTest extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<UnmappedTargetPropertiesInspection> getInspection() {
        return UnmappedTargetPropertiesInspection.class;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(
            "UnmappedCollectionGetterPropertiesData.java",
            "org/example/data/UnmappedCollectionGetterPropertiesData.java"
        );
    }

    public void testUnmappedCollectionGetterProperties() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
            .extracting( IntentionAction::getText )
            .as( "Intent Text" )
                .containsExactlyInAnyOrder(
                        "Ignore unmapped target property: 'listTarget'",
                        "Add unmapped target property: 'listTarget'",
                        "Ignore unmapped target property: 'setTarget'",
                        "Add unmapped target property: 'setTarget'",
                        "Ignore unmapped target property: 'mapTarget'",
                        "Add unmapped target property: 'mapTarget'",
                        "Ignore all unmapped target properties"
            );

        allQuickFixes.forEach( myFixture::launchAction );
    }
}
