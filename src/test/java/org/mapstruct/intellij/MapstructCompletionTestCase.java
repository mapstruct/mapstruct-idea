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

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.util.PlatformIcons;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class MapstructCompletionTestCase extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/mapping";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        addDirectoryToProject( "dto" );
    }

    public void testCarMapperReturnTargetCarDto() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "make",
                "seatCount",
                "manufacturingYear",
                "driver",
                "passengers",
                "price",
                "category",
                "available"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                create( "make", "String" ),
                create( "seatCount", "int" ),
                create( "manufacturingYear", "String" ),
                create( "driver", "PersonDto" ),
                create( "passengers", "List<PersonDto>" ),
                create( "price", "Long" ),
                create( "category", "String" ),
                create( "available", "boolean" )
            );
    }

    public void testCarMapperUpdateTargetCarDto() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "make",
                "seatCount",
                "manufacturingYear",
                "driver",
                "passengers",
                "price",
                "category",
                "available"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                create( "make", "String" ),
                create( "seatCount", "int" ),
                create( "manufacturingYear", "String" ),
                create( "driver", "PersonDto" ),
                create( "passengers", "List<PersonDto>" ),
                create( "price", "Long" ),
                create( "category", "String" ),
                create( "available", "boolean" )
            );
    }

    public void testCarMapperUpdateTargetCarDto2() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "make",
                "seatCount",
                "manufacturingYear",
                "driver",
                "passengers",
                "price",
                "category",
                "available"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                create( "make", "String" ),
                create( "seatCount", "int" ),
                create( "manufacturingYear", "String" ),
                create( "driver", "PersonDto" ),
                create( "passengers", "List<PersonDto>" ),
                create( "price", "Long" ),
                create( "category", "String" ),
                create( "available", "boolean" )
            );
    }

    public void testCarMapperSimpleSingleSourceCar() {
        configureByTestName();

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
                create( "make", "String" ),
                create( "numberOfSeats", "int" ),
                create( "manufacturingDate", "Date" ),
                create( "driver", "Person" ),
                create( "passengers", "List<Person>" ),
                create( "price", "int" ),
                create( "category", "Category" ),
                create( "free", "boolean" )
            );
    }

    public void testCarMapperUpdateSourceCar() {
        configureByTestName();

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
                create( "make", "String" ),
                create( "numberOfSeats", "int" ),
                create( "manufacturingDate", "Date" ),
                create( "driver", "Person" ),
                create( "passengers", "List<Person>" ),
                create( "price", "int" ),
                create( "category", "Category" ),
                create( "free", "boolean" )
            );
    }

    public void testCarMapperUpdateSourceCar2() {
        configureByTestName();

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
                create( "make", "String" ),
                create( "numberOfSeats", "int" ),
                create( "manufacturingDate", "Date" ),
                create( "driver", "Person" ),
                create( "passengers", "List<Person>" ),
                create( "price", "int" ),
                create( "category", "Category" ),
                create( "free", "boolean" )
            );
    }

    public void testVariantsCarMapperNoSourceClass() {
        myFixture.configureByFile( "CarMapperNoSourceClass.java" );
        complete();
        assertThat( myItems ).isEmpty();
    }

    public void testVariantsCarMapperNoTargetClass() {
        myFixture.configureByFile( "CarMapperNoTargetClass.java" );
        complete();
        assertThat( myItems ).isEmpty();
    }

    public void testVariantsMapperSourceClassNoMethods() {
        myFixture.configureByFile( "MapperSourceClassNoMethods.java" );
        complete();
        assertThat( myItems ).isEmpty();
    }

    public void testVariantsMapperTargetClassNoMethods() {
        myFixture.configureByFile( "MapperTargetClassNoMethods.java" );
        complete();
        assertThat( myItems ).isEmpty();
    }

    public void testCarMapperReferenceTargetPropertyInCarDto() {
        myFixture.configureByFile( "CarMapperReferenceTargetProperty.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiMethod.class, method -> {
                assertThat( method.getName() ).isEqualTo( "setSeatCount" );
                assertThat( method.getPresentation() ).isNotNull();
                assertThat( method.getPresentation().getPresentableText() ).isEqualTo( "setSeatCount(int)" );
                assertThat( method.getReturnType() ).isNotNull();
                assertThat( method.getReturnType().getPresentableText() ).isEqualTo( "void" );
            } );
    }

    public void testCarMapperReferenceSourcePropertyInCarDto() {
        myFixture.configureByFile( "CarMapperReferenceSourceProperty.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiMethod.class, method -> {
                assertThat( method.getName() ).isEqualTo( "getNumberOfSeats" );
                assertThat( method.getPresentation() ).isNotNull();
                assertThat( method.getPresentation().getPresentableText() ).isEqualTo( "getNumberOfSeats()" );
                assertThat( method.getParameterList().getParametersCount() ).isEqualTo( 0 );
                assertThat( method.getReturnType() ).isNotNull();
                assertThat( method.getReturnType().getPresentableText() ).isEqualTo( "int" );
            } );
    }

    public void testCarMapperReferenceBooleanSourceCar() {
        myFixture.configureByFile( "CarMapperReferenceBooleanSourceCar.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiMethod.class, method -> {
                assertThat( method.getName() ).isEqualTo( "isFree" );
                assertThat( method.getPresentation() ).isNotNull();
                assertThat( method.getPresentation().getPresentableText() ).isEqualTo( "isFree()" );
                assertThat( method.getParameterList().getParametersCount() ).isEqualTo( 0 );
                assertThat( method.getReturnType() ).isNotNull();
                assertThat( method.getReturnType().getPresentableText() ).isEqualTo( "boolean" );
            } );
    }

    public void testCarMapperReferenceEmptySourceProperty() {
        myFixture.configureByFile( "CarMapperReferenceEmptySourceProperty.java" );
        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testCarMapperReferenceEmptyTargetProperty() {
        myFixture.configureByFile( "CarMapperReferenceEmptyTargetProperty.java" );
        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testCarMapperReferenceUnknownSourceProperty() {
        myFixture.configureByFile( "CarMapperReferenceUnknownSourceProperty.java" );
        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testCarMapperReferenceUnknownTargetProperty() {
        myFixture.configureByFile( "CarMapperReferenceUnknownTargetProperty.java" );
        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testReferenceCarMapperNoSourceClass() {
        myFixture.configureByFile( "CarMapperNoSourceClass.java" );
        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testReferenceCarMapperNoTargetClass() {
        myFixture.configureByFile( "CarMapperNoTargetClass.java" );
        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testMappingNotOnMethodForSource() {
        configureByTestName();
        assertThat( myItems ).isEmpty();
    }

    public void testMappingNotOnMethodForTarget() {
        configureByTestName();
        assertThat( myItems ).isEmpty();
    }

    private static LookupElementPresentation create(String lookupString, String typeText) {
        return LookupElementPresentation.renderElement( LookupElementBuilder.create( lookupString )
            .withIcon( PlatformIcons.VARIABLE_ICON )
            .withTailText( "" )
            .withTypeText( typeText ) );
    }
}
