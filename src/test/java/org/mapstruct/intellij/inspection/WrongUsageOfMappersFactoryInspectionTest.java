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
import static org.mapstruct.intellij.testutil.TestUtils.quickFixAnnotateInterfaceMessage;

/**
 * @author Filip Hrisafov
 */
public class WrongUsageOfMappersFactoryInspectionTest extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<WrongUsageOfMappersFactoryInspection> getInspection() {
        return WrongUsageOfMappersFactoryInspection.class;
    }

    public void testWrongUsageOfMappersFactory() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
            .extracting( IntentionAction::getText )
            .as( "Intent Text" )
            .containsExactly(
                quickFixAnnotateInterfaceMessage( "NotMapStructMapper", "Mapper" ),
                "Remove usage of Mappers factory",
                quickFixAnnotateInterfaceMessage( "NotMapStructMapper2", "Mapper" ),
                "Remove usage of Mappers factory",
                "Remove 'spring' componentModel from 'SpringComponentModelMapper' @Mapper",
                "Remove usage of Mappers factory",
                "Remove 'jsr330' componentModel from 'Jsr330ComponentModelMapper' @Mapper",
                "Remove usage of Mappers factory",
                "Remove 'custom' componentModel from 'CustomComponentModelMapper' @Mapper",
                "Remove usage of Mappers factory"
            );

        myFixture.launchAction( allQuickFixes.get( 1 ) ); // Remove usage of Mappers factory
        myFixture.launchAction( allQuickFixes.get( 2 ) ); // Annotate NotMapStructMapper2
        myFixture.launchAction( allQuickFixes.get( 5 ) ); // Remove usage of Mappers factory
        myFixture.launchAction( allQuickFixes.get( 6 ) ); // Remove jsr330 componentModel
        myFixture.launchAction( allQuickFixes.get( 8 ) ); // Remove custom componentModel

        // IDEA LATEST-EAP-SNAPSHOT fails to assert this (and only this) test
        // when myFixture.getAllQuickFixes() does not get called beforehand.
        // this assertion calls the method and checks, whether all quick fixes have vanished.
        // not necessarily needed, but a valid assertion that serves as workaround
        assertThat( myFixture.getAllQuickFixes() ).isEmpty();

        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
