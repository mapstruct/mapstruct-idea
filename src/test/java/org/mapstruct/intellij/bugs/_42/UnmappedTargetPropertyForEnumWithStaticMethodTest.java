/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.bugs._42;

import java.util.List;

import com.intellij.codeInsight.intention.IntentionAction;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.inspection.BaseInspectionTest;
import org.mapstruct.intellij.inspection.UnmappedTargetPropertiesInspection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class UnmappedTargetPropertyForEnumWithStaticMethodTest extends BaseInspectionTest {

    @Override
    protected String getTestDataPath() {
        return "testData/bugs/_42";
    }

    @NotNull
    @Override
    protected Class<UnmappedTargetPropertiesInspection> getInspection() {
        return UnmappedTargetPropertiesInspection.class;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testUnmappedTargetPropertyForEnumWithStaticMethod() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
            .extracting( IntentionAction::getText )
            .as( "Intent Text" )
            .isEmpty();
    }
}
