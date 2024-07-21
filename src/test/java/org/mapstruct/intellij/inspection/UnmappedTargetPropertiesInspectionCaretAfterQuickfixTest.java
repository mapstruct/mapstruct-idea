/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import java.util.List;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Caret;
import org.jetbrains.annotations.NotNull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oliver Erhart
 */
public class UnmappedTargetPropertiesInspectionCaretAfterQuickfixTest extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<UnmappedTargetPropertiesInspection> getInspection() {
        return UnmappedTargetPropertiesInspection.class;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(
            "UnmappedTargetPropertiesData.java",
            "org/example/data/UnmappedTargetPropertiesData.java"
        );
    }

    public void testAddUnmappedTargetProperties() {
        doTest( "UnmappedTargetPropertiesJdk8.java" );

        List<IntentionAction> addMissingTargetQuickfixes = myFixture.getAllQuickFixes()
            .stream()
            .filter( i -> i.getText().startsWith( "Add unmapped target property " ) )
            .toList();

        addMissingTargetQuickfixes.forEach( this::launchAndAssertCaretPositionInSource );
    }

    public void testIgnoreUnmappedTargetProperties() {
        doTest( "UnmappedTargetPropertiesJdk8.java" );

        List<IntentionAction> addMissingTargetQuickfixes = myFixture.getAllQuickFixes()
            .stream()
            .filter( i -> i.getText().startsWith( "Ignore unmapped target property" ) )
            .toList();

        addMissingTargetQuickfixes.forEach( this::launchAndAssertUnchangedCaretPosition );
    }

    public void testIgnoreAllUnmappedTargetProperties() {
        doTest( "UnmappedTargetPropertiesJdk8.java" );

        List<IntentionAction> addMissingTargetQuickfixes = myFixture.getAllQuickFixes()
            .stream()
            .filter( i -> i.getText().startsWith( "Ignore all unmapped target properties" ) )
            .toList();

        addMissingTargetQuickfixes.forEach( this::launchAndAssertUnchangedCaretPosition );
    }

    private void launchAndAssertCaretPositionInSource(IntentionAction quickFix) {

        myFixture.launchAction( quickFix );

        assertThatCaretIsInsideOfSourceString();
    }

    private void launchAndAssertUnchangedCaretPosition(IntentionAction quickFix) {

        Caret caretBefore = myFixture.getEditor().getCaretModel().getCurrentCaret();

        myFixture.launchAction( quickFix );

        Caret caretAfter = myFixture.getEditor().getCaretModel().getCurrentCaret();

        assertThat( caretAfter ).isEqualTo( caretBefore );
    }

    private void assertThatCaretIsInsideOfSourceString() {
        assertThat( lineContentBeforeSelection() ).endsWith( "source = \"" );
    }

    private CharSequence lineContentBeforeSelection() {

        Caret currentCaret = myFixture.getEditor().getCaretModel().getCurrentCaret();

        return myFixture.getEditor()
            .getDocument()
            .getCharsSequence()
            .subSequence( currentCaret.getVisualLineStart(), currentCaret.getSelectionEnd() );
    }
}
