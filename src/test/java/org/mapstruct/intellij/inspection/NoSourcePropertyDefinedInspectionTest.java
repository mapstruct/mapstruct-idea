/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import org.jetbrains.annotations.NotNull;

/**
 * @author hduelme
 */
public class NoSourcePropertyDefinedInspectionTest extends BaseInspectionTest {

    @Override
    protected @NotNull Class<NoSourcePropertyDefinedInspection> getInspection() {
        return NoSourcePropertyDefinedInspection.class;
    }

    public void testNoSourcePropertyDefined() {
        doTest();
    }

    public void testSourcePropertyDefined() {
        doTest();
    }

    public void testIgnoreSourcePropertyByDefault() {
        doTest();
    }
}
