/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij;

import java.io.File;

import com.intellij.codeInsight.completion.LightFixtureCompletionTestCase;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.LanguageLevelModuleExtension;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.util.PathUtil;
import com.intellij.util.lang.JavaVersion;
import org.jetbrains.annotations.NotNull;

/**
 * Base completion test case for MapStruct.
 *
 * @author Filip Hrisafov
 */
public abstract class MapstructBaseCompletionTestCase extends LightFixtureCompletionTestCase {

    private static final String BUILD_LIBS_DIRECTORY = "build/libs";
    private static final String BUILD_MOCK_JDK_DIRECTORY = "build/mockJDK-";

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
        LanguageLevel languageLevel = getLanguageLevel();
        return new DefaultLightProjectDescriptor() {
            @Override
            public Sdk getSdk() {
                JavaVersion version = languageLevel.toJavaVersion();
                int mockJdk;
                if ( version.feature >= 11 ) {
                    mockJdk = 11;
                }
                else {
                    mockJdk = version.feature;
                }
                String compilerOption = ( mockJdk < 11 ? "1." : "" ) + mockJdk;
                return JavaSdk.getInstance()
                    .createJdk( "java " + compilerOption, BUILD_MOCK_JDK_DIRECTORY + compilerOption, false );
            }

            @Override
            public void configureModule(@NotNull Module module, @NotNull ModifiableRootModel model,
                @NotNull ContentEntry contentEntry) {
                model.getModuleExtension( LanguageLevelModuleExtension.class )
                    .setLanguageLevel( languageLevel );
            }
        };
    }

    protected LanguageLevel getLanguageLevel() {
        return LanguageLevel.JDK_1_8;
    }
}
