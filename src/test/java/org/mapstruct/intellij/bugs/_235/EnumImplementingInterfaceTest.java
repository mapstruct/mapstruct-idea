/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.bugs._235;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.inspection.BaseInspectionTest;
import org.mapstruct.intellij.inspection.MapstructReferenceInspection;

/**
 * @author Oliver Erhart
 */
public class EnumImplementingInterfaceTest extends BaseInspectionTest {

    @Override
    protected String getTestDataPath() {
        return "testData/bugs/_235";
    }

    @NotNull
    @Override
    protected Class<MapstructReferenceInspection> getInspection() {
        return MapstructReferenceInspection.class;
    }

    public void testEnumImplementingInterface() {
        doTest();
    }
}
