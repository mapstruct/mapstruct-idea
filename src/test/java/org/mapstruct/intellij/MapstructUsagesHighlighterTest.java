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

import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class MapstructUsagesHighlighterTest extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/mapping";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        addDirectoryToProject( "dto" );
    }

    public void testSourceRangeHighlighter() {
        RangeHighlighter[] rangeHighlighters = myFixture.testHighlightUsages( "CarMapperSimpleSingleSourceCar.java" );
        assertThat( rangeHighlighters )
            .isNotEmpty()
            .hasSize( 1 );
        RangeHighlighter rangeHighlighter = rangeHighlighters[0];
        assertThat( rangeHighlighter.getTargetArea() ).isEqualTo( HighlighterTargetArea.EXACT_RANGE );
        int caretOffset = myFixture.getCaretOffset();
        assertThat( rangeHighlighter.getStartOffset() ).isEqualTo( caretOffset );
        assertThat( rangeHighlighter.getEndOffset() ).isEqualTo( caretOffset + "numberOfSeats".length() );
    }

    public void testTargetRangeHighlighter() {
        RangeHighlighter[] rangeHighlighters = myFixture.testHighlightUsages( "CarMapperReturnTargetCarDto.java" );
        assertThat( rangeHighlighters )
            .isNotEmpty()
            .hasSize( 1 );
        RangeHighlighter rangeHighlighter = rangeHighlighters[0];
        assertThat( rangeHighlighter.getTargetArea() ).isEqualTo( HighlighterTargetArea.EXACT_RANGE );
        int caretOffset = myFixture.getCaretOffset();
        assertThat( rangeHighlighter.getStartOffset() ).isEqualTo( caretOffset );
        assertThat( rangeHighlighter.getEndOffset() ).isEqualTo( caretOffset + "seatCount".length() );
    }

    public void testFluentTargetRangeHighlighter() {
        RangeHighlighter[] rangeHighlighters = myFixture.testHighlightUsages(
            "CarMapperReturnTargetFluentCarDto.java"
        );
        assertThat( rangeHighlighters )
            .isNotEmpty()
            .hasSize( 1 );
        RangeHighlighter rangeHighlighter = rangeHighlighters[0];
        assertThat( rangeHighlighter.getTargetArea() ).isEqualTo( HighlighterTargetArea.EXACT_RANGE );
        int caretOffset = myFixture.getCaretOffset();
        assertThat( rangeHighlighter.getStartOffset() ).isEqualTo( caretOffset );
        assertThat( rangeHighlighter.getEndOffset() ).isEqualTo( caretOffset + "seatCount".length() );
    }
}
