/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author Filip Hrisafov
 */
public interface ProjectSettings {

    String PREFIX = "MapStructPlugin";

    String PREFER_SOURCE_BEFORE_TARGET_IN_MAPPING =
        PREFIX + "PREFER_SOURCE_BEFORE_TARGET_IN_MAPPING";

    static boolean isPreferSourceBeforeTargetInMapping(@NotNull Project project) {
        return PropertiesComponent.getInstance( project ).getBoolean( PREFER_SOURCE_BEFORE_TARGET_IN_MAPPING, false );
    }

    static void setPreferSourceBeforeTargetInMapping(@NotNull Project project, boolean value) {
        PropertiesComponent.getInstance( project )
            .setValue( PREFER_SOURCE_BEFORE_TARGET_IN_MAPPING, String.valueOf( value ), "false" );
    }
}
