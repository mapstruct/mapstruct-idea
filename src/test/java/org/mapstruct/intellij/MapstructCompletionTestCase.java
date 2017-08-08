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
                "category"
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
                create( "category", "String" )
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
                "category"
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
                create( "category", "Category" )
            );
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

    private static LookupElementPresentation create(String lookupString, String typeText) {
        return LookupElementPresentation.renderElement( LookupElementBuilder.create( lookupString )
            .withIcon( PlatformIcons.VARIABLE_ICON )
            .withTailText( "" )
            .withTypeText( typeText ) );
    }
}
