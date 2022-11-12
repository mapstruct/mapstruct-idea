/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.inspection;

import java.util.List;

import com.intellij.codeInsight.intention.IntentionAction;
import org.jetbrains.annotations.NotNull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mapstruct.intellij.testutil.TestUtils.quickFixAnnotateClassMessage;
import static org.mapstruct.intellij.testutil.TestUtils.quickFixAnnotateInterfaceMessage;

/**
 * @author Filip Hrisafov
 */
public class MissingMapperOrMapperConfigAnnotationInspectionTest extends BaseInspectionTest {

    @NotNull
    @Override
    protected Class<MissingMapperOrMapperConfigAnnotationInspection> getInspection() {
        return MissingMapperOrMapperConfigAnnotationInspection.class;
    }

    public void testMissingMapperOrMapperConfig() {
        doTest();
    }

    public void testMissingMapperOrMapperConfigDecorator() {
        doTest();
    }

    public void testMissingMapperOrConfigIntent() {
        doTest();
        List<IntentionAction> allQuickFixes = myFixture.getAllQuickFixes();

        assertThat( allQuickFixes ).extracting( IntentionAction::getText )
            .containsExactly(
                quickFixAnnotateInterfaceMessage( "InterfaceWithMappingAnnotations", "Mapper" ),
                quickFixAnnotateInterfaceMessage( "InterfaceWithMappingAnnotations", "MapperConfig" ),
                quickFixAnnotateClassMessage( "ClassWithMappingAnnotations", "Mapper" ),
                quickFixAnnotateClassMessage( "ClassWithMappingAnnotations", "MapperConfig" )
            );

        myFixture.launchAction( allQuickFixes.get( 1 ) );
        myFixture.launchAction( allQuickFixes.get( 2 ) );
        myFixture.checkResultByFile( getTestName( false ) + "_after.java" );
    }
}
