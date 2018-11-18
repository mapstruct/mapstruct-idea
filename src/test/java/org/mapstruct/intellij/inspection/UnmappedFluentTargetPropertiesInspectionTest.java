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
public class UnmappedFluentTargetPropertiesInspectionTest extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<UnmappedTargetPropertiesInspection> getInspection() {
        return UnmappedTargetPropertiesInspection.class;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject(
            "UnmappedFluentTargetPropertiesData.java",
            "org/example/data/UnmappedFluentTargetPropertiesData.java"
        );
    }

    public void testUnmappedFluentTargetProperties() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
            .extracting( IntentionAction::getText )
            .as( "Intent Text" )
            .containsExactly(
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'",
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'",
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'",
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'",
                "Ignore unmapped target property: 'testName'",
                "Add unmapped target property: 'testName'",
                "Ignore unmapped target property: 'matching'",
                "Add unmapped target property: 'matching'",
                "Ignore unmapped target property: 'moreTarget'",
                "Add unmapped target property: 'moreTarget'"
            );

        allQuickFixes.forEach( myFixture::launchAction );
        myFixture.checkResultByFile( testName + "_after.java" );
    }
}
