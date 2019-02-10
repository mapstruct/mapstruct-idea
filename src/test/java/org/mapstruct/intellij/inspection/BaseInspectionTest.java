/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

/**
 * @author Filip Hrisafov
 */
public abstract class BaseInspectionTest extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/inspection";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @NotNull
    abstract Class<? extends LocalInspectionTool> getInspection();

    void doTest() {
        String testName = getTestName( false );
        configureByFile( testName + ".java" );
        myFixture.enableInspections( getInspection() );
        myFixture.testHighlighting( true, true, true );
    }
}
