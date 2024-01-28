/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalInspectionTool;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hduelme
 */
public class FromMapMappingMapTypeInspectionTest extends BaseInspectionTest {

    @Override
    protected @NotNull Class<? extends LocalInspectionTool> getInspection() {
        return FromMapMappingMapTypeInspection.class;
    }

    public void testFromMapMappingMapTypeInspectionRawMap() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly(
                        "Replace Map with Map<String, String>",
                        "Replace HashMap with HashMap<String, String>",
                        "Replace Map with Map<String, String>",
                        "Replace HashMap with HashMap<String, String>"
                );

        allQuickFixes.forEach( myFixture::launchAction );
        myFixture.checkResultByFile( testName + "_after.java" );
    }

    public void testFromMapMappingMapTypeInspectionWrongKeyType() {
        doTest();
        String testName = getTestName( false );
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes )
                .extracting( IntentionAction::getText )
                .as( "Intent Text" )
                .containsExactly(
                        "Change key type to String", "Change key type to String",
                        "Change key type to String", "Change key type to String"
                );

        allQuickFixes.forEach( myFixture::launchAction );
        myFixture.checkResultByFile( testName + "_after.java" );
    }

    public void testFromMapMappingMapTypeToMapNoInspection() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes ).isEmpty();
    }

    public void testFromMapMappingMapTypeWithSourceParameterNoInspection() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();
        assertThat( allQuickFixes ).isEmpty();
     }
}
