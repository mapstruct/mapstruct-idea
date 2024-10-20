/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij;

import java.io.File;

import com.intellij.codeInsight.completion.LightFixtureCompletionTestCase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

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
        VfsRootAccess.allowRootAccess( getTestRootDisposable(), mapstructLibPath );
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
