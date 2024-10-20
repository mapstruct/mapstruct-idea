/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.rename;

import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

/**
 * @author Filip Hrisafov
 */
public class RenameHandlerJdk17Test extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/rename";
    }

    public void testRenameRecordSourceParameter() {
        myFixture.configureByFile( "RenameRecordSourceParameter.java" );
        myFixture.renameElementAtCaret( "anotherName" );
        myFixture.checkResultByFile( "RenameRecordSourceParameterAfter.java" );
    }

    public void testRenameRecordTargetParameter() {
        myFixture.configureByFile( "RenameRecordTargetParameter.java" );
        myFixture.renameElementAtCaret( "newName" );
        myFixture.checkResultByFile( "RenameRecordTargetParameterAfter.java" );
    }
}
