/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.openapi.editor.Caret;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hduelme
 */
public class TargetPropertyMappedMoreThanOnceInspectionTest extends BaseInspectionTest {

    @Override
    protected @NotNull Class<? extends LocalInspectionTool> getInspection() {
        return TargetPropertyMappedMoreThanOnceInspection.class;
    }

    public void testTargetPropertyMappedMoreThanOnce() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly(
                        "Remove Mapping annotation",
                        "Change target property",
                        "Remove Mapping annotation",
                        "Change target property",
                        "Remove Mapping annotation",
                        "Change target property",
                        "Remove Mapping annotation",
                        "Change target property",
                        "Remove Mapping annotation",
                        "Change target property",
                        "Remove Mapping annotation",
                        "Change target property",
                        "Remove Mapping annotation",
                        "Change target property",
                        "Remove MyMappingAnnotation annotation",
                        "Remove Mapping annotation",
                        "Change target property",
                        "Remove MyMappingAnnotation annotation"
                );

        // Delete annotations
        myFixture.launchAction( allQuickFixes.get( 0 ) );
        myFixture.launchAction( allQuickFixes.get( 3 ) );
        myFixture.launchAction( allQuickFixes.get( 6 ) );
        myFixture.launchAction( allQuickFixes.get( 8 ) );
        myFixture.launchAction( allQuickFixes.get( 14 ) );
        // Set cursor
        myFixture.launchAction( allQuickFixes.get( 16 ) );
        myFixture.checkResultByFile( testName + "_after.java" );
        Caret currentCaret = myFixture.getEditor().getCaretModel().getCurrentCaret();
        assertThat( currentCaret.getSelectedText( ) ).isEqualTo( "testName" );
    }
}
