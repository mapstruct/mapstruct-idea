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

public class JavaExpressionUnnecessaryWhitespacesInspectorTest extends BaseInspectionTest {
    @Override
    protected @NotNull Class<? extends LocalInspectionTool> getInspection() {
        return JavaExpressionUnnecessaryWhitespacesInspector.class;
    }

    public void testJavaExpressionUnnecessaryWhitespacesInspectorWhitespaceBefore() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly(
                        "Remove unnecessary whitespaces before conditionExpression",
                        "Remove unnecessary whitespaces before defaultExpression",
                        "Remove unnecessary whitespaces before expression"
                );

        allQuickFixes.forEach( myFixture::launchAction );
        myFixture.checkResultByFile( testName + "_after.java" );
    }

    public void testJavaExpressionUnnecessaryWhitespacesInspectorWhitespaceAfter() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly(
                        "Remove unnecessary whitespaces after conditionExpression",
                        "Remove unnecessary whitespaces after defaultExpression",
                        "Remove unnecessary whitespaces after expression"
                );

        allQuickFixes.forEach( myFixture::launchAction );
        myFixture.checkResultByFile( testName + "_after.java" );
    }

    //Tests if inspection ignores Textblocks
    public void testJavaExpressionUnnecessaryWhitespacesTextBlock() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes ).isEmpty();
    }
}
