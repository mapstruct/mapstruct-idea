/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij;

import com.intellij.codeInsight.completion.LightFixtureCompletionTestCase;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.DependencyScope;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.MavenDependencyUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Base completion test case for MapStruct.
 *
 * @author Filip Hrisafov
 */
public abstract class MapstructBaseCompletionTestCase extends LightFixtureCompletionTestCase {

    public static final LightProjectDescriptor WITH_MAPSTRUCT_JAVA17 = new ProjectDescriptor(LanguageLevel.JDK_17) {
        @Override
        public void configureModule(@NotNull Module module, @NotNull ModifiableRootModel model,
                                    @NotNull ContentEntry contentEntry) {
            super.configureModule( module, model, contentEntry );
            MavenDependencyUtil.addFromMaven( model, "org.mapstruct:mapstruct:1.5.3.Final",
                    false, DependencyScope.PROVIDED );
        }
    };

    protected void addDirectoryToProject(@NotNull String directory) {
        myFixture.copyDirectoryToProject( directory, StringUtil.getShortName( directory, '/' ) );
    }

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return WITH_MAPSTRUCT_JAVA17;
    }
}
