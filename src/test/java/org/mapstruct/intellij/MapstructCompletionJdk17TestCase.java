/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecordComponent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mapstruct.intellij.testutil.TestUtils.createParameter;
import static org.mapstruct.intellij.testutil.TestUtils.createVariable;

/**
 * @author Filip Hrisafov
 */
public class MapstructCompletionJdk17TestCase extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/mapping";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        addDirectoryToProject( "dto" );
    }

    private void assertCarDtoRecordAutoComplete() {
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "make",
                "seatCount",
                "myDriver",
                "passengers",
                "price",
                "category",
                "available"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createVariable( "make", "String" ),
                createVariable( "seatCount", "int" ),
                createVariable( "myDriver", "PersonDtoRecord" ),
                createVariable( "passengers", "List<PersonDto>" ),
                createVariable( "price", "Long" ),
                createVariable( "category", "String" ),
                createVariable( "available", "boolean" )
            );
    }

    private void assertCarRecordAutoComplete() {
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "make",
                "numberOfSeats",
                "manufacturingDate",
                "driver",
                "passengers",
                "price",
                "category",
                "free"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createVariable( "make", "String" ),
                createVariable( "numberOfSeats", "int" ),
                createVariable( "manufacturingDate", "Date" ),
                createVariable( "driver", "Person" ),
                createVariable( "passengers", "List<Person>" ),
                createVariable( "price", "int" ),
                createVariable( "category", "Category" ),
                createVariable( "free", "boolean" )
            );
    }

    public void testCarMapperReturnTargetCarDtoRecord() {
        myFixture.copyFileToProject( "CarDtoRecord.java", "org/example/dto/CarDtoRecord.java" );
        myFixture.copyFileToProject( "PersonDtoRecord.java", "org/example/dto/PersonDtoRecord.java" );
        configureByTestName();
        assertCarDtoRecordAutoComplete();
    }

    public void testTargetPropertyReferencesRecordComponent() {
        myFixture.copyFileToProject( "CarDtoRecord.java", "org/example/dto/CarDtoRecord.java" );
        myFixture.copyFileToProject( "PersonDtoRecord.java", "org/example/dto/PersonDtoRecord.java" );
        myFixture.configureByFile( "CarMapperReferenceRecordTargetProperty.java" );
        PsiElement reference = myFixture.getElementAtCaret();
        assertThat( reference )
            .isInstanceOfSatisfying( PsiRecordComponent.class, recordComponent -> {
                assertThat( recordComponent.getName() ).isEqualTo( "seatCount" );
                assertThat( recordComponent.getType() ).isNotNull();
                assertThat( recordComponent.getType().getPresentableText() ).isEqualTo( "int" );
            } );
    }

    public void testNestedSecondLevelAutoCompleteRecordTargetProperty() {
        myFixture.copyFileToProject( "CarDtoRecord.java", "org/example/dto/CarDtoRecord.java" );
        myFixture.copyFileToProject( "PersonDtoRecord.java", "org/example/dto/PersonDtoRecord.java" );
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "name"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .usingElementComparatorIgnoringFields( "myIcon", "myTail" )
            .containsExactlyInAnyOrder(
                createParameter( "name", "String" )
            );
    }

    public void testCarMapperSimpleSingleSourceCarRecord() {
        myFixture.copyFileToProject( "CarRecord.java", "org/example/dto/CarRecord.java" );
        configureByTestName();
        assertCarRecordAutoComplete();
    }

    public void testSourcePropertyReferencesRecordComponent() {
        myFixture.copyFileToProject( "CarRecord.java", "org/example/dto/CarRecord.java" );
        myFixture.configureByFile( "CarMapperReferenceRecordSourceProperty.java" );
        PsiElement reference = myFixture.getElementAtCaret();
        assertThat( reference )
            .isInstanceOfSatisfying( PsiRecordComponent.class, recordComponent -> {
                assertThat( recordComponent.getName() ).isEqualTo( "numberOfSeats" );
                assertThat( recordComponent.getType() ).isNotNull();
                assertThat( recordComponent.getType().getPresentableText() ).isEqualTo( "int" );
            } );
    }

    public void testNestedSecondLevelAutoCompleteRecordSourceProperty() {
        myFixture.copyFileToProject( "CarDtoRecord.java", "org/example/dto/CarDtoRecord.java" );
        myFixture.copyFileToProject( "PersonDtoRecord.java", "org/example/dto/PersonDtoRecord.java" );
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "name"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createVariable( "name", "String" )
            );
    }
}
