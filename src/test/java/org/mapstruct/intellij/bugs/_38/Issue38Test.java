/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.bugs._38;

import com.intellij.codeInsight.lookup.LookupElement;
import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class Issue38Test extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/bugs/_38";
    }

    public void testUnmappedTargetWithPublicStaticProperty() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "isbn"
            );
    }

    public void testUnmappedSourceWithPublicStaticProperty() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "isbn"
            );
    }
}
