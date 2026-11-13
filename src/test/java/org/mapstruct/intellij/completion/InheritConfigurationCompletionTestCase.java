/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mapstruct.intellij.testutil.TestUtils.createMethod;

public class InheritConfigurationCompletionTestCase extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/completion/inheritconfiguration";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        addDirectoryToProject( "../../mapping/dto" );
    }

    public void testInheritConfigurationByNameCompletion() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder( "carDtoToCarIgnoringSeatCount", "carDtoToCarIgnoringManufacturingDate" );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createMethod(
                    "carDtoToCarIgnoringSeatCount",
                    "Car",
                    " InheritConfigurationCompletion#carDtoToCarIgnoringSeatCount(CarDto)"
                ),
                createMethod(
                    "carDtoToCarIgnoringManufacturingDate",
                    "Car",
                    " InheritConfigurationCompletion#carDtoToCarIgnoringManufacturingDate(CarDto)"
                )
            );
    }

    public void testInheritConfigurationWithMapperConfigCompletion() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder( "configSourceToTarget", "configSourceToTargetIgnoringSeats" );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createMethod(
                    "configSourceToTarget",
                    "Car",
                    " ConfigCompleteMapperConfig#configSourceToTarget(CarDto)"
                ),
                createMethod(
                    "configSourceToTargetIgnoringSeats",
                    "Car",
                    " ConfigCompleteMapperConfig#configSourceToTargetIgnoringSeats(CarDto)"
                )
            );
    }

    public void testInheritInverseConfigurationByNameCompletion() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder( "carToCarDto", "carToCarDtoIgnoringDriver" );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createMethod(
                    "carToCarDto",
                    "CarDto",
                    " InheritInverseConfigurationByNameCompletion#carToCarDto(Car)"
                ),
                createMethod(
                    "carToCarDtoIgnoringDriver",
                    "CarDto",
                    " InheritInverseConfigurationByNameCompletion#carToCarDtoIgnoringDriver(Car)"
                )
            );
    }

}
