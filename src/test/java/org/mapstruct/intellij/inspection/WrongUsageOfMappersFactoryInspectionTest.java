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
package org.mapstruct.intellij.inspection;

import java.util.List;

import com.intellij.codeInsight.intention.IntentionAction;
import org.jetbrains.annotations.NotNull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class WrongUsageOfMappersFactoryInspectionTest extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<WrongUsageOfMappersFactoryInspection> getInspection() {
        return WrongUsageOfMappersFactoryInspection.class;
    }

    public void testWrongUsageOfMappersFactory() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
            .extracting( IntentionAction::getText )
            .as( "Intent Text" )
            .containsExactly(
                "Annotate interface 'NotMapStructMapper' as @Mapper",
                "Remove usage of Mappers factory",
                "Annotate interface 'NotMapStructMapper2' as @Mapper",
                "Remove usage of Mappers factory",
                "Remove 'spring' componentModel from 'SpringComponentModelMapper' @Mapper",
                "Remove usage of Mappers factory",
                "Remove 'jsr330' componentModel from 'Jsr330ComponentModelMapper' @Mapper",
                "Remove usage of Mappers factory",
                "Remove 'custom' componentModel from 'CustomComponentModelMapper' @Mapper",
                "Remove usage of Mappers factory"
            );

        myFixture.launchAction( allQuickFixes.get( 1 ) ); // Remove usage of Mappers factory
        myFixture.launchAction( allQuickFixes.get( 2 ) ); // Annotate NotMapStructMapper2
        myFixture.launchAction( allQuickFixes.get( 5 ) ); // Remove usage of Mappers factory
        myFixture.launchAction( allQuickFixes.get( 6 ) ); // Remove jsr330 componentModel
        myFixture.launchAction( allQuickFixes.get( 8 ) ); // Remove custom componentModel

        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
