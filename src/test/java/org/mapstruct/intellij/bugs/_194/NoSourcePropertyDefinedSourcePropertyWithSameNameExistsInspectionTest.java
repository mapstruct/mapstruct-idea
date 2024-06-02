/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.bugs._194;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.inspection.BaseInspectionTest;
import org.mapstruct.intellij.inspection.NoSourcePropertyDefinedInspection;

/**
 * @author hduelme
 */
public class NoSourcePropertyDefinedSourcePropertyWithSameNameExistsInspectionTest extends BaseInspectionTest {

    @Override
    protected @NotNull Class<NoSourcePropertyDefinedInspection> getInspection() {
        return NoSourcePropertyDefinedInspection.class;
    }

    @Override
    protected String getTestDataPath() {
        return "testData/bugs/_194";
    }

    public void testNoSourcePropertyDefinedSourcePropertyWithSameNameExists() {
        doTest();
    }
}
