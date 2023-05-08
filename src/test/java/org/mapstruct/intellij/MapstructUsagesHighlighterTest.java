/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
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

    public void testBuilderTargetRangeHighlighter() {
        RangeHighlighter[] rangeHighlighters = myFixture.testHighlightUsages(
            "CarMapperReturnTargetCarDtoWithBuilder.java"
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
