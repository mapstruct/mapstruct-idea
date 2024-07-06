/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author hduelme
 */
public class TargetPropertyMappedMoreThanOnceInspectionTest extends BaseInspectionTest {

    @Override
    protected @NotNull Class<? extends LocalInspectionTool> getInspection() {
        return TargetPropertyMappedMoreThanOnceInspection.class;
    }

    public void testTargetPropertyMappedMoreThanOnce() {
        doTest();
    }
}
