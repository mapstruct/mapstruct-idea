/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

package org.mapstruct.intellij.inspection.inheritance;

import java.util.List;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.lang.annotation.HighlightSeverity;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.inspection.BaseInspectionTest;
import org.mapstruct.intellij.inspection.UnmappedTargetPropertiesInspection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oliver Erhart
 */
public class InheritConfigurationInspectionTest extends BaseInspectionTest {

    @Override
    protected String getTestDataPath() {
        return "testData/inspection/inheritance";
    }

    @NotNull
    @Override
    protected Class<UnmappedTargetPropertiesInspection> getInspection() {
        return UnmappedTargetPropertiesInspection.class;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        addDirectoryToProject( "../../mapping/dto" );
    }

    public void testInheritConfigurationByInheritanceMapper() {
        doTest();
    }

    public void testInheritConfigurationByMapperConfigMapper() {
        doTest();
    }

    public void testInheritConfigurationByNameMapper() {

        configureByTestName();

        myFixture.enableInspections( getInspection() );

        List<HighlightInfo> warnings = myFixture.doHighlighting( HighlightSeverity.WARNING );

        assertThat( warnings )
            .as( "'carDtoIntoCar' must show no warning, because it should have been inherited" )
            .hasSize( 1 )
            .extracting( HighlightInfo::getText )
            .containsOnly( "carDtoToCarIgnoringSeatCount" );
    }

    public void testInheritConfigurationNotInheritedByUsedMapper() {
        doTest();
    }

    public void testInheritConfigurationBySuperMapperMapper() {
        doTest();
    }

    public void testInheritConfigurationInSameClassMapper() {
        doTest();
    }

    public void testInheritConfigurationInSameClassOfUpdateMethodMapper() {
        doTest();
    }

    public void testInheritConfigurationInSameClassWithContextMapper() {
        doTest();
    }

}
