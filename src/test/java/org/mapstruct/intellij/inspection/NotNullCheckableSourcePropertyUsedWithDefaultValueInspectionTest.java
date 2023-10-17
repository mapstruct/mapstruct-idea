/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalInspectionTool;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class NotNullCheckableSourcePropertyUsedWithDefaultValueInspectionTest extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<? extends LocalInspectionTool> getInspection() {
        return NotNullCheckableSourcePropertyUsedWithDefaultValueInspection.class;
    }

    public void testConstantSourcePropertyUsedWithDefaultValueInspection() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly( "Remove defaultExpression",
                        "Remove defaultValue",
                        "Remove defaultExpression",
                        "Remove defaultValue"
                );
        allQuickFixes.forEach( myFixture::launchAction );
        String testName = getTestName( false );
        myFixture.checkResultByFile( testName + "_after.java" );
    }

    public void testExpressionSourcePropertyUsedWithDefaultValueInspection() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly( "Remove defaultExpression",
                        "Remove defaultValue",
                        "Remove defaultExpression",
                        "Remove defaultValue"
                );
        allQuickFixes.forEach( myFixture::launchAction );
        String testName = getTestName( false );
        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
