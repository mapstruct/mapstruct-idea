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
package org.mapstruct.intellij;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class MapperCompletionContributorTest extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/mapper";
    }

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
