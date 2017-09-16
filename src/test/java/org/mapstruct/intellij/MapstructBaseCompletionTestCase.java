/*
 *  Copyright 2017 the MapStruct authors (http://www.mapstruct.org/)
 *  and/or other contributors as indicated by the @authors tag. See the
 *  copyright.txt file in the distribution for a full listing of all
 *  contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.mapstruct.intellij;

import java.io.File;

import com.intellij.codeInsight.completion.LightFixtureCompletionTestCase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

import static com.intellij.testFramework.LightPlatformTestCase.getModule;

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
        VfsRootAccess.allowRootAccess( mapstructLibPath );
        PsiTestUtil.addLibrary( getModule(), "Mapstruct", mapstructLibPath, "mapstruct.jar" );
    }

    protected void addDirectoryToProject(@NotNull String directory) {
        myFixture.copyDirectoryToProject( directory, StringUtil.getShortName( directory, '/' ) );
    }
}
