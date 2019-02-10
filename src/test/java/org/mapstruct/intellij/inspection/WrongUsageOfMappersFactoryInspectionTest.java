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
                "Annotate interface 'NotMapStructMapper' as @Mapper",
                "Remove usage of Mappers factory",
                "Annotate interface 'NotMapStructMapper2' as @Mapper",
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

        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
