/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.bugs._180;

import com.intellij.codeInsight.intention.IntentionAction;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.inspection.BaseInspectionTest;
import org.mapstruct.intellij.inspection.WrongUsageOfMappersFactoryInspection;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hduelme
 */
public class WrongUsageOfMappersFactoryFromConfigInspectionTest extends BaseInspectionTest {

    @Override
    protected String getTestDataPath() {
        return "testData/bugs/_180";
    }

    @NotNull
    @Override
    protected Class<WrongUsageOfMappersFactoryInspection> getInspection() {
        return WrongUsageOfMappersFactoryInspection.class;
    }

    public void testWrongUsageOfMappersFactoryFromConfig() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly(
                        "Remove 'spring' componentModel from 'SpringConfigComponentModelOverrideMapper' @Mapper",
                        "Remove usage of Mappers factory",
                        "Remove usage of Mappers factory",
                        "Remove usage of Mappers factory",
                        "Remove usage of Mappers factory"
                );
        // Remove usage fix
        myFixture.launchAction( allQuickFixes.get( 1 ) );
        myFixture.launchAction( allQuickFixes.get( 2 ) );
        myFixture.launchAction( allQuickFixes.get( 3 ) );
        myFixture.launchAction( allQuickFixes.get( 4 ) );
        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
