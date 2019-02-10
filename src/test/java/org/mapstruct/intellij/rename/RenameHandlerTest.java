/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.rename;

import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

/**
 * @author Filip Hrisafov
 */
public class RenameHandlerTest extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/rename";
    }

    public void testRenameSourceParameter() {
        myFixture.configureByFile( "RenameSourceParameter.java" );
        myFixture.renameElementAtCaretUsingHandler( "getAnotherName" );
        myFixture.checkResultByFile( "RenameSourceParameterAfter.java" );
    }

    public void testRenameMethodSourceParameter() {
        myFixture.configureByFile( "RenameSourceParameterForMethod.java" );
        myFixture.renameElementAtCaret( "param" );
        myFixture.checkResultByFile( "RenameSourceParameterForMethodAfter.java" );
    }

    public void testRenameTargetParameter() {
        myFixture.configureByFile( "RenameTargetParameter.java" );
        myFixture.renameElementAtCaretUsingHandler( "setNewName" );
        myFixture.checkResultByFile( "RenameTargetParameterAfter.java" );
    }

    public void testRenameFluentTargetParameter() {
        myFixture.configureByFile( "RenameFluentTargetParameter.java" );
        myFixture.renameElementAtCaretUsingHandler( "newName" );
        myFixture.checkResultByFile( "RenameFluentTargetParameterAfter.java" );
    }

    public void testRenameMethodTargetParameter() {
        myFixture.configureByFile( "RenameTargetParameterForMethod.java" );
        myFixture.renameElementAtCaret( "newTarget" );
        myFixture.checkResultByFile( "RenameTargetParameterForMethodAfter.java" );
    }
}
