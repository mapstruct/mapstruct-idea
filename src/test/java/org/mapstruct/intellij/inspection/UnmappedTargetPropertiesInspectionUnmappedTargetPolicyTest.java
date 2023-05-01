/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInsight.intention.IntentionAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hduelme
 */
public class UnmappedTargetPropertiesInspectionUnmappedTargetPolicyTest extends BaseInspectionTest {

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

    /**
     * Tests if unmappedTargetPolicy is read from methode first. Methode level annotation should overwrite class values.
     */
    public void testUnmappedTargetPropertiesReportPolicyBeanMappingBeforeClassConfig() {
        doTest();
        checkQuickFixes();
    }

    /**
     * Tests if unmappedTargetPolicy could be read from @BeanMapping annotation.
     */
    public void testUnmappedTargetPropertiesReportPolicyBeanMapping() {
        doTest();
        checkQuickFixes();
    }

    /**
     * Tests if unmappedTargetPolicy is read from class annotation first.
     * Class level annotation should overwrite config class values.
     */
    public void testUnmappedTargetPropertiesReportPolicyClassLevelBeforeConfigClassError() {
        doTest();
        checkQuickFixes();
    }

    /**
     * Tests if unmappedTargetPolicy is read from class annotation first.
     * Class level annotation should overwrite config class values.
     */
    public void testUnmappedTargetPropertiesReportPolicyClassLevelBeforeConfigClassWarn() {
        doTest();
        checkQuickFixes();
    }

    /**
     * Tests if unmappedTargetPolicy could be read from mapper config class.
     */
    public void testUnmappedTargetPropertiesReportPolicyConfigClass() {
        doTest();
        checkQuickFixes();
    }

    /**
     * Tests if unmappedTargetPolicy set to ERROR, results in reported errors instead of warnings.
     */
    public void testUnmappedTargetPropertiesReportPolicyError() {
        doTest();
        checkQuickFixes();
    }

    /**
     * Tests if unmappedTargetPolicy set to IGNORE, suppress all unmapped warnings
     */
    public void testUnmappedTargetPropertiesReportPolicyIgnoreBeanMapping() {
        doTest();
        assertThat( myFixture.getAllQuickFixes() ).isEmpty();
    }

    /**
     * Tests if unmappedTargetPolicy set to IGNORE, suppress all unmapped warnings
     */
    public void testUnmappedTargetPropertiesReportPolicyIgnoreClassAnnotation() {
        doTest();
        assertThat( myFixture.getAllQuickFixes() ).isEmpty();
    }

    /**
     * Tests if unmappedTargetPolicy set to IGNORE, suppress all unmapped warnings
     */
    public void testUnmappedTargetPropertiesReportPolicyIgnoreConfigClass() {
        doTest();
        assertThat( myFixture.getAllQuickFixes() ).isEmpty();
    }

    /**
     * Tests if unmappedTargetPolicy could be read, if static import is used.
     */
    public void testUnmappedTargetPropertiesReportPolicyStaticImport() {
        doTest();
        checkQuickFixes();
    }

    private void checkQuickFixes() {
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
            .extracting( IntentionAction::getText )
            .as( "Intent Text" )
            .containsExactly(
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'",
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'",
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'",
                "Ignore all unmapped target properties",
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'",
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'"
            );
    }
}
