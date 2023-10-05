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
 * @author hduelme
 */
public class MoreThanOneDefaultSourcePropertyDefinedInspectionTest extends BaseInspectionTest {

    @Override
    protected @NotNull Class<MoreThanOneDefaultSourcePropertyDefinedInspection> getInspection() {
        return MoreThanOneDefaultSourcePropertyDefinedInspection.class;
    }

    public void testMoreThanOneDefaultSourceProperty() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly( "Remove default value",
                        "Remove default expression",
                        "Remove default value",
                        "Remove default expression"
                );
        myFixture.launchAction( allQuickFixes.get( 0 ) );
        myFixture.launchAction( allQuickFixes.get( 3 ) );
        String testName = getTestName( false );
        myFixture.checkResultByFile( testName + "_after.java" );
    }

}
