/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.bugs._33;

import com.intellij.codeInsight.lookup.LookupElement;
import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class Issue33Test extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/bugs/_33";
    }

    public void testUnmappedTargetPropertyBooleanWithIsPrefix() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "isAdded"
            );
    }
}
