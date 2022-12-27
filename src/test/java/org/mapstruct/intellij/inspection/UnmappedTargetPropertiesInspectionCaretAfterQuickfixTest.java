/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import java.util.List;
import java.util.stream.Collectors;

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

    public void testUnmappedTargetPropertiesJdk8() {
        doTest();
        List<IntentionAction> addMissingTargetQuickfixes = myFixture.getAllQuickFixes()
            .stream()
            .filter( i -> i.getText().startsWith( "Add unmapped target property " ) )
            .collect( Collectors.toList() );

        addMissingTargetQuickfixes.forEach( this::launchAndAssertCaretPosition );
    }

    private void launchAndAssertCaretPosition(IntentionAction addMissingTargetQuickFix) {

        myFixture.launchAction( addMissingTargetQuickFix );

        assertThatCaretIsInsideOfSourceString();
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
