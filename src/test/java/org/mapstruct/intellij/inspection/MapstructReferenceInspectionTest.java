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
public class MapstructReferenceInspectionTest extends BaseInspectionTest {

    @Override
    protected @NotNull Class<? extends LocalInspectionTool> getInspection() {
        return MapstructReferenceInspection.class;
    }

    public void testUnknownTargetReference() {
        doTest();
    }

    public void testUnknownNestedTargetReference() {
        doTest();
    }

    public void testUnknownSourceReference() {
        doTest();
    }

    public void testUnknownNestedSourceReference() {
        doTest();
    }

    public void testUnknownValueMappingSourceReference() {
        doTest();
    }

    public void testUnknownValueMappingTargetReference() {
        doTest();
    }

    public void testUnknownIgnoreUnmappedSourceReference() {
        doTest();
    }

    public void testUnknownQualifiedByNameReferenceReference() {
        doTest();
    }
}
