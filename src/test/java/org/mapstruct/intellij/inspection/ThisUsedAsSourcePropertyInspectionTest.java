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

/**
 * @author hduelme
 */
public class ThisUsedAsSourcePropertyInspectionTest extends BaseInspectionTest {
    @Override
    protected @NotNull Class<? extends LocalInspectionTool> getInspection() {
        return ThisUsedAsSourcePropertyInspection.class;
    }

    public void testThisUsedAsSourcePropertyInspection() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactlyInAnyOrder(
                        "Replace source '.' with 'source'",
                        "Replace source '.' with 'source'",
                        "Replace source '.' with 'source'",
                        "Replace source '.' with 'age'"
                );

        myFixture.launchAction( allQuickFixes.get( 0 ) );
        myFixture.launchAction( allQuickFixes.get( 1 ) );
        myFixture.launchAction( allQuickFixes.get( 2 ) );
        String testName = getTestName( false );
        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
