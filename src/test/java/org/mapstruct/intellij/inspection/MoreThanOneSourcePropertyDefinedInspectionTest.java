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
public class MoreThanOneSourcePropertyDefinedInspectionTest extends BaseInspectionTest {

    @Override
    protected @NotNull Class<MoreThanOneSourcePropertyDefinedInspection> getInspection() {
        return MoreThanOneSourcePropertyDefinedInspection.class;
    }

    public void testMoreThanOneSourceConstantAndSource() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly( "Remove source value",
                        "Remove constant value",
                        "Remove source value",
                        "Remove constant value",
                        "Use constant value as default value"
                );
        myFixture.launchAction( allQuickFixes.get( 1 ) );
        myFixture.launchAction( allQuickFixes.get( 4 ) );
        String testName = getTestName( false );
        myFixture.checkResultByFile( testName + "_after.java" );
    }

    public void testMoreThanOneSourceConstantAndSourceThisMapping() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly(
                        "Remove constant value",
                        "Remove constant value"
                );
        allQuickFixes.forEach( myFixture::launchAction );
        String testName = getTestName( false );
        myFixture.checkResultByFile( testName + "_after.java" );
    }

    public void testMoreThanOneSourceConstantAndExpression() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly( "Remove constant value",
                        "Remove expression",
                        "Remove constant value",
                        "Remove expression"
                );
        myFixture.launchAction( allQuickFixes.get( 1 ) );
        myFixture.launchAction( allQuickFixes.get( 2 ) );
        String testName = getTestName( false );
        myFixture.checkResultByFile( testName + "_after.java" );
    }

    public void testMoreThanOneSourceExpressionAndSource() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly( "Remove source value",
                        "Remove expression",
                        "Remove source value",
                        "Remove expression",
                        "Use expression as default expression"
                );
        myFixture.launchAction( allQuickFixes.get( 0 ) );
        myFixture.launchAction( allQuickFixes.get( 2 ) );
        String testName = getTestName( false );
        myFixture.checkResultByFile( testName + "_after.java" );
    }

    public void testMoreThanOneSourceExpressionAndSourceThisMapping() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly(
                        "Remove expression",
                        "Remove expression"
                );
        allQuickFixes.forEach( myFixture::launchAction );
        String testName = getTestName( false );
        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
