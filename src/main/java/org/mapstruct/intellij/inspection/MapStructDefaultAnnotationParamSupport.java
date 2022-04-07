/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInspection.DefaultAnnotationParamInspection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.util.MapstructUtil;

/**
 * @author Filip Hrisafov
 */
public class MapStructDefaultAnnotationParamSupport
    implements DefaultAnnotationParamInspection.IgnoreAnnotationParamSupport {

    @Override
    public boolean ignoreAnnotationParam(@Nullable String annotationFQN, @NotNull String annotationParameterName) {
        if ( MapstructUtil.MAPPING_ANNOTATION_FQN.equals( annotationFQN ) ) {
            return annotationParameterName.equals( "constant" ) || annotationParameterName.equals( "defaultValue" );
        }

        return false;
    }
}
