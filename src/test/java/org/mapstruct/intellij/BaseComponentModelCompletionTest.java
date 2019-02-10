/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public abstract class BaseComponentModelCompletionTest extends MapstructBaseCompletionTestCase {

    public void testCompletionInsideQuotes() {
        configureByTestName();
        List<String> strings = myFixture.getLookupElementStrings();
        assertThat( strings )
            .as( "Inside quotes" )
            .containsExactlyInAnyOrder( "default", "cdi", "spring", "jsr330" );
    }

    public void testCompletionInsideQuotesStartsWithC() {
        configureByTestName();
        List<String> strings = myFixture.getLookupElementStrings();
        assertThat( strings ).as( "Inside quotes" )
            .contains( "cdi" )
            .doesNotContain( "default", "spring", "jsr330" );
    }

    public void testCompletionNoQuotes() {
        configureByTestName();
        List<String> strings = myFixture.getLookupElementStrings();
        assertThat( strings )
            .as( "No quotes completion" )
            .doesNotContain( "default", "cdi", "spring", "jsr330" );
    }

    public void testCompletionNoQuotesStartsWithS() {
        configureByTestName();
        List<String> strings = myFixture.getLookupElementStrings();
        assertThat( strings )
            .as( "No quotes completion" )
            .doesNotContain( "default", "cdi", "spring", "jsr330" );
    }
}
