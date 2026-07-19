/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij;

import com.intellij.codeInsight.completion.LightFixtureCompletionTestCase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Base completion test case for MapStruct.
 *
 * @author Filip Hrisafov
 */
public abstract class MapstructBaseCompletionTestCase extends LightFixtureCompletionTestCase {

    private static final String BUILD_LIBS_DIRECTORY = "build/libs";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final String mapstructLibPath = PathUtil.toSystemIndependentName( new File( BUILD_LIBS_DIRECTORY )
            .getAbsolutePath() );

        allowAccessToDirsIfExists(
                BUILD_LIBS_DIRECTORY,
                "testData",
                "build/test-libs"
        );

        PsiTestUtil.addLibrary(
            myFixture.getProjectDisposable(),
            myFixture.getModule(),
            "Mapstruct",
            mapstructLibPath,
            "mapstruct.jar"
        );
    }

    protected void addDirectoryToProject(@NotNull String directory) {
        myFixture.copyDirectoryToProject( directory, StringUtil.getShortName( directory, '/' ) );
    }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return JAVA_17;
    }
}
