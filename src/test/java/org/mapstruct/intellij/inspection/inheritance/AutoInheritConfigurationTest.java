/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection.inheritance;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.inspection.BaseInspectionTest;
import org.mapstruct.intellij.inspection.UnmappedTargetPropertiesInspection;

/**
 * @author Oliver Erhart
 */
public class AutoInheritConfigurationTest extends BaseInspectionTest {

    @Override
    protected String getTestDataPath() {
        return "testData/inspection/inheritance/auto";
    }

    @NotNull
    @Override
    protected Class<UnmappedTargetPropertiesInspection> getInspection() {
        return UnmappedTargetPropertiesInspection.class;
    }

    public void testInheritanceStrategyAutoInheritAllFromConfigForward() {
        doTest();
    }

    public void testInheritanceStrategyAutoInheritAllFromConfigForwardNotConfigured() {
        doTest();
    }

    public void testInheritanceStrategyAutoInheritAllFromConfigReverse() {
        doTest();
    }

    public void testInheritanceStrategyAutoInheritAllFromConfigReverseNotConfigured() {
        doTest();
    }

    public void testInheritanceStrategyAutoInheritFromConfig() {
        doTest();
    }

    public void testInheritanceStrategyAutoInheritFromConfigNotConfigured() {
        doTest();
    }

    public void testInheritanceStrategyAutoInheritReverseFromConfig() {
        doTest();
    }

    public void testInheritanceStrategyAutoInheritReverseFromConfigNotConfigured() {
        doTest();
    }

    public void testInheritanceStrategyExplicitNotAnnotated() {
        doTest();
    }

    public void testInheritanceStrategyExplicitAnnotated() {
        doTest();
    }

    public void testMapstructIssue2318Mapper() {
        doTest();
    }

    public void testCarMapperWithExplicitInheritance() {
        doTest();
    }

}
