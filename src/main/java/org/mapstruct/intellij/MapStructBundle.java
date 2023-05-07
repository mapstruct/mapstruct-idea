/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * Bundle that provides all messages, strings, etc that are used in the plugin.
 *
 * @author Filip Hrisafov
 */
public class MapStructBundle {
    public static String message(@NotNull @PropertyKey(resourceBundle = MapStructBundle.PATH_TO_BUNDLE) String key,
        @NotNull Object... params) {
        return AbstractBundle.message( getBundle(), key, params );
    }

    private static Reference<ResourceBundle> ourBundle;
    @NonNls
    private static final String PATH_TO_BUNDLE = "org.mapstruct.intellij.messages.MapStructBundle";

    private MapStructBundle() {
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = com.intellij.reference.SoftReference.dereference( ourBundle );
        if ( bundle == null ) {
            bundle = ResourceBundle.getBundle( PATH_TO_BUNDLE );
            ourBundle = new SoftReference<>( bundle );
        }
        return bundle;
    }
}
