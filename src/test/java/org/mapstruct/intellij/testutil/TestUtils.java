/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.testutil;

import javax.swing.Icon;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.java.analysis.JavaAnalysisBundle;
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
        return create( lookupString, typeText, PlatformIcons.VARIABLE_ICON, null );
    }

    public static LookupElementPresentation createMethod(String lookupString, String typeText, String tailText) {
        return create( lookupString, typeText, PlatformIcons.METHOD_ICON, tailText );
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

    public static String quickFixAnnotateInterfaceMessage(String interfaceName, String annotationName) {
        return JavaAnalysisBundle
            .message(
                "inspection.i18n.quickfix.annotate.element.as",
                "interface",
                interfaceName,
                annotationName
            );
    }

    public static String quickFixAnnotateClassMessage(String className, String annotationName) {
        return JavaAnalysisBundle
            .message(
                "inspection.i18n.quickfix.annotate.element.as",
                "class",
                className,
                annotationName
            );
    }
}
