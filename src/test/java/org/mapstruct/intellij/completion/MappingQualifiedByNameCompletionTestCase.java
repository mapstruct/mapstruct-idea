/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mapstruct.intellij.testutil.TestUtils.createMethod;

public class MappingQualifiedByNameCompletionTestCase extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/completion/qualifiedbyname";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        addDirectoryToProject( "../../mapping/dto" );
        addDirectoryToProject( "mapper" );
    }

    public void testMappingQualifiedByNameLocalNamedReference() {
        configureByTestName();
        assertQualifiedByNameLocalReferenceAutoComplete();
    }

    private void assertQualifiedByNameLocalReferenceAutoComplete() {
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder( "numberToZero", "doubleSeatCount" );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createMethod( "numberToZero", "Long", " CarMapper#setToZero(...)" ),
                createMethod( "doubleSeatCount", "int", " CarMapper#multiplyByTwo(...)" )
            );
    }

    public void testMappingQualifiedByNameLocalAndExternalNamedReference() {
        configureByTestName();
        assertQualifiedByNameLocalAndExternalReferenceAutoComplete();
    }

    private void assertQualifiedByNameLocalAndExternalReferenceAutoComplete() {
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder( "numberToZero", "doubleSeatCount", "trimString" );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createMethod( "numberToZero", "Long", " CarMapper#setToZero(...)" ),
                createMethod( "doubleSeatCount", "int", " CarMapper#multiplyByTwo(...)" ),
                createMethod( "trimString", "String", " StringMapper#trim(...)" )
            );
    }

}
