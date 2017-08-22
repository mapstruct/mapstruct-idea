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
package org.mapstruct.intellij.testutil;

import javax.swing.Icon;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.util.PlatformIcons;

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
        return create( lookupString, typeText, PlatformIcons.VARIABLE_ICON, "" );
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
}
