/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.DefaultAnnotationParamInspection;
import com.intellij.codeInspection.LocalInspectionTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author Filip Hrisafov
 */
public class MapStructDefaultAnnotationParamSupportTest extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<? extends LocalInspectionTool> getInspection() {
        return DefaultAnnotationParamInspection.class;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(
            "UnmappedConstructorTargetPropertiesData.java",
            "org/example/data/UnmappedConstructorTargetPropertiesData.java"
        );
    }

    public void testIgnoreRedundantDefaultParameterValueInMapping() {
        doTest();
    }
}
