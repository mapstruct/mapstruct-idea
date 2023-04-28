/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.pom.java.LanguageLevel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests if unmappedTargetPolicy set to IGNORE, suppress all unmapped warnings
 * @author hduelme
 */
public class UnmappedTargetPropertiesInspectionReportPolicyIgnoreTest extends BaseInspectionTest {

    @Override
    protected LanguageLevel getLanguageLevel() {
        return LanguageLevel.JDK_1_7;
    }

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

    public void testUnmappedTargetPropertiesReportPolicyIgnore() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes ).isEmpty();
    }
}
