/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.testutil;

import java.util.Map;
import java.util.TreeMap;
import javax.swing.Icon;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.util.PlatformIcons;
import org.apache.commons.text.StringSubstitutor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Filip Hrisafov
 */
public final class TestUtils {

    private TestUtils() {
    }

    public static LookupElementPresentation createField(String lookupString, String typeText) {
        return create( lookupString, typeText, PlatformIcons.FIELD_ICON, null );
    }

    public static LookupElementPresentation createVariable(String lookupString, String typeText) {
        return create( lookupString, typeText, PlatformIcons.VARIABLE_ICON, null );
    }

    public static LookupElementPresentation createParameter(String lookupString, String typeText) {
        return create( lookupString, typeText, PlatformIcons.PARAMETER_ICON, null );
    }

    private static LookupElementPresentation create(String lookupString, String typeText, Icon icon, String tailText) {
        return LookupElementPresentation.renderElement( LookupElementBuilder.create( lookupString )
            .withIcon( icon )
            .withTailText( tailText )
            .withTypeText( typeText ) );
    }

    private static final String PREFIX = "/*{";
    private static final String SUFFIX = "}*/";
    public static final String MAPPER = "mapper";
    public static final String X_MAPPER_X = PREFIX + MAPPER + SUFFIX;
    public static final String MAPPING = "mapping";
    public static final String X_MAPPING_X = PREFIX + MAPPING + SUFFIX;

    public static String advancedFormat(String template, @NotNull String... keysAndValues) {
        if ( keysAndValues.length % 2 != 0 ) {
            throw new IllegalArgumentException( "use it like this: 'K1, V1, K2, V2, K3, V3, ...'" );
        }
        Map<String, String> map = new TreeMap<>();
        for ( int i = 0; i < keysAndValues.length; i += 2 ) {
            map.put( keysAndValues[i], keysAndValues[i + 1] );
        }
        return new StringSubstitutor( map, PREFIX, SUFFIX ).replace( template );
    }
}
