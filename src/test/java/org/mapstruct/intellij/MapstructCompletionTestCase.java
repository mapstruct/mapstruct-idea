/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mapstruct.intellij.testutil.TestUtils.createParameter;
import static org.mapstruct.intellij.testutil.TestUtils.createVariable;

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

    private void assertCarDtoAutoComplete() {
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
                createVariable( "myDriver", "PersonDto" ),
                createVariable( "passengers", "List<PersonDto>" ),
                createVariable( "price", "Long" ),
                createVariable( "category", "String" ),
                createVariable( "available", "boolean" )
            );
    }

    private void assertCarDtoAutoCompleteKt() {
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
                createVariable( "myDriver", "PersonDtoKt" ),
                createVariable( "passengers", "List<PersonDtoKt>" ),
                createVariable( "price", "Long" ),
                createVariable( "category", "String" ),
                createVariable( "available", "boolean" )
            );
    }

    private void assertCarDtoWithBuilderAutoComplete() {
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
                createVariable( "myDriver", "PersonDtoWithBuilder" ),
                createVariable( "passengers", "List<PersonDtoWithBuilder>" ),
                createVariable( "price", "Long" ),
                createVariable( "category", "String" ),
                createVariable( "available", "boolean" )
            );
    }

    private void assertCarDtoPublicAutoComplete() {
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
                createVariable( "myDriver", "PersonDto" ),
                createVariable( "passengers", "List<PersonDto>" ),
                createVariable( "price", "Long" ),
                createVariable( "category", "String" ),
                createVariable( "available", "boolean" )
            );
    }

    private void assertCarAutoComplete() {
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

    private void assertFluentPersonDtoAutoComplete() {
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "name",
                "address"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createVariable( "name", "String" ),
                createVariable( "address", "String" )
            );
    }

    private void assertCarDtoWithConstructorAutoComplete() {
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
            .usingElementComparatorIgnoringFields( "myIcon", "myTail" )
            .containsExactlyInAnyOrder(
                createParameter( "make", "String" ),
                createParameter( "seatCount", "int" ),
                createParameter( "myDriver", "PersonDtoWithConstructor" ),
                createParameter( "passengers", "List<PersonDtoWithConstructor>" ),
                createParameter( "price", "Long" ),
                createParameter( "category", "String" ),
                createParameter( "available", "boolean" )
            );
    }

    private void assertCarDtoWithConstructorAndSettersAutoComplete() {
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
            .usingElementComparatorIgnoringFields( "myIcon", "myTail" )
            .containsExactlyInAnyOrder(
                createParameter( "make", "String" ),
                createParameter( "seatCount", "int" ),
                createParameter( "myDriver", "PersonDtoWithConstructor" ),
                createParameter( "passengers", "List<PersonDtoWithConstructor>" ),
                createParameter( "price", "Long" ),
                createVariable( "category", "String" ),
                createVariable( "available", "boolean" )
            );
    }

    private void assertNestedSecondLevelAutoCompleteProperty() {
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

    public void testCarMapperReturnTargetCarDtoKotlin() {
        configureByFile( "/" + getTestName( false ) + ".kt" );
        assertCarDtoAutoCompleteKt();
    }

    public void testCarMapperReturnTargetCarDto() {
        configureByTestName();
        assertCarDtoAutoComplete();
    }

    public void testCarMapperReturnTargetFluentCarDto() {
        configureByTestName();
        assertCarDtoAutoComplete();
    }

    public void testCarMapperReturnTargetCarDtoWithBuilder() {
        configureByTestName();
        assertCarDtoWithBuilderAutoComplete();
    }

    public void testCarMapperReturnTargetCarDtoPublic() {
        configureByTestName();
        assertCarDtoPublicAutoComplete();
    }

    public void testCarMapperReturnTargetCarDtoWithConstructor() {
        configureByTestName();
        assertCarDtoWithConstructorAutoComplete();
    }

    public void testCarMapperReturnTargetCarDtoWithConstructorAndSetters() {
        configureByTestName();
        assertCarDtoWithConstructorAndSettersAutoComplete();
    }

    public void testCarMapperReturnTargetCarDtoWithConstructorAndEmptyConstructor() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "price",
                "category"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createVariable( "price", "Long" ),
                createVariable( "category", "String" )
            );
    }

    public void testCarMapperReturnTargetCarDtoWithMultipleConstructorsAndAnnotatedWithDefault() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "make",
                "seatCount",
                "price"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .usingElementComparatorIgnoringFields( "myIcon", "myTail" )
            .containsExactlyInAnyOrder(
                createParameter( "make", "String" ),
                createParameter( "seatCount", "int" ),
                createParameter( "price", "Long" )
            );
    }

    public void testPersonMapperReturnTargetFluentPersonDto() {
        configureByTestName();
        assertFluentPersonDtoAutoComplete();
    }

    public void testCarMapperUpdateTargetCarDto() {
        configureByTestName();
        assertCarDtoAutoComplete();
    }

    public void testCarMapperUpdateTargetCarDtoWithBuilder() {
        configureByTestName();
        assertCarDtoWithBuilderAutoComplete();
    }

    public void testCarMapperUpdateTargetCarDtoPublic() {
        configureByTestName();
        assertCarDtoPublicAutoComplete();
    }

    public void testCarMapperUpdateTargetFluentCarDto() {
        configureByTestName();
        assertCarDtoAutoComplete();
    }

    public void testCarMapperUpdateTargetCarDto2() {
        configureByTestName();
        assertCarDtoAutoComplete();
    }

    public void testNestedFirstLevelAutoCompleteTargetProperty() {
        configureByTestName();
        assertCarDtoAutoComplete();
    }

    public void testTargetPropertyAutoCompleteAfterTargetParameter() {
        configureByTestName();
        assertCarDtoAutoComplete();
    }

    public void testNestedSecondLevelAutoCompleteTargetProperty() {
        configureByTestName();
        assertNestedSecondLevelAutoCompleteProperty();
    }

    public void testNestedSecondLevelAutoCompleteTargetPublicProperty() {
        configureByTestName();
        assertNestedSecondLevelAutoCompleteProperty();
    }

    public void testNestedSecondLevelAutoCompleteBuilderTargetProperty() {
        configureByTestName();
        assertNestedSecondLevelAutoCompleteProperty();
    }

    public void testNestedSecondLevelAutoCompleteConstructorTargetProperty() {
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

    public void testCarMapperSimpleSingleSourceCar() {
        configureByTestName();
        assertCarAutoComplete();
    }

    public void testCarMapperSimpleSingleSourceCarPublic() {
        configureByTestName();
        assertCarAutoComplete();
    }

    public void testCarMapperUpdateSourceCar() {
        configureByTestName();
        assertCarAutoComplete();
    }

    public void testCarMapperUpdateSourceCar2() {
        configureByTestName();
        assertCarAutoComplete();
    }

    public void testNestedFirstLevelAutoCompleteSourceProperty() {
        configureByTestName();
        assertCarAutoComplete();
    }

    public void testSourcePropertyAutoCompleteAfterSourceParameter() {
        configureByTestName();
        assertCarAutoComplete();
    }

    public void testMultipleSourceParametersUpdateMapping() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "source1",
                "source2",
                "doors"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            //For some reason the icon is empty in the returned items. However, in actual completion it is OK
            .usingElementComparatorIgnoringFields( "myIcon" )
            .containsExactlyInAnyOrder(
                createParameter( "source1", "Car" ),
                createParameter( "source2", "Car" ),
                createParameter( "doors", "Long" )
            );
    }

    public void testNestedSecondLevelAutoCompleteSourceProperty() {
        configureByTestName();
        assertNestedSecondLevelAutoCompleteProperty();
    }

    public void testNestedSecondLevelAutoCompleteSourcePublicProperty() {
        configureByTestName();
        assertNestedSecondLevelAutoCompleteProperty();
    }

    public void testFluentGenericTargetMapper() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "id",
                "value"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createVariable( "id", "String" ),
                createVariable( "value", "String" )
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

    public void testCarMapperReferencePublicTargetProperty() {
        myFixture.configureByFile( "CarMapperReferencePublicTargetProperty.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
                .isInstanceOfSatisfying( PsiField.class, field -> {
                    assertThat( field.getName() ).isEqualTo( "seatCount" );
                    assertThat( field.getPresentation() ).isNotNull();
                    assertThat( field.getPresentation().getPresentableText() ).isEqualTo( "seatCount" );
                    assertThat( field.getType() ).isNotNull();
                    assertThat( field.getType().getPresentableText() ).isEqualTo( "int" );
                } );
    }

    public void testCarMapperReferencePublicSourceProperty() {
        myFixture.configureByFile( "CarMapperReferencePublicSourceProperty.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiField.class, fields -> {
                assertThat( fields.getName() ).isEqualTo( "numberOfSeats" );
                assertThat( fields.getPresentation() ).isNotNull();
                assertThat( fields.getPresentation().getPresentableText() ).isEqualTo( "numberOfSeats" );
            } );
    }

    public void testCarMapperReferencePublicStaticFieldSourceProperty() {
        myFixture.configureByFile( "CarMapperReferencePublicStaticFieldSourceProperty.java" );
        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testCarMapperReferenceProtectedSourceProperty() {
        myFixture.configureByFile( "CarMapperReferenceProtectedSourceProperty.java" );
        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testCarMapperReferenceTargetPropertyInCarDtoWithBuilder() {
        myFixture.configureByFile( "CarMapperReferenceBuilderTargetProperty.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiMethod.class, method -> {
                assertThat( method.getName() ).isEqualTo( "seatCount" );
                assertThat( method.getPresentation() ).isNotNull();
                assertThat( method.getPresentation().getPresentableText() ).isEqualTo( "seatCount(int)" );
                assertThat( method.getReturnType() ).isNotNull();
                assertThat( method.getReturnType().getPresentableText() ).isEqualTo( "Builder" );
            } );
    }

    public void testCarMapperReferenceTargetPropertyInCarDtoWithConstructor() {
        myFixture.configureByFile( "CarMapperReferenceConstructorTargetProperty.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiParameter.class, parameter -> {
                assertThat( parameter.getName() ).isEqualTo( "seatCount" );
                assertThat( parameter.getType().getPresentableText() ).isEqualTo( "int" );
                PsiMethod constructor = PsiTreeUtil.getParentOfType( parameter, PsiMethod.class );
                assertThat( constructor ).isNotNull();
                assertThat( constructor.isConstructor() ).isTrue();
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

    public void testNestedFirstLevelReferenceSourceProperty() {
        myFixture.configureByFile( "NestedFirstLevelReferenceSourceProperty.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiMethod.class, method -> {
                assertThat( method.getName() ).isEqualTo( "getDriver" );
                assertThat( method.getPresentation() ).isNotNull();
                assertThat( method.getPresentation().getPresentableText() ).isEqualTo( "getDriver()" );
                assertThat( method.getParameterList().getParametersCount() ).isEqualTo( 0 );
                assertThat( method.getReturnType() ).isNotNull();
                assertThat( method.getReturnType().getPresentableText() ).isEqualTo( "Person" );
                PsiClass person = PsiTreeUtil.getParentOfType( method, PsiClass.class );
                assertThat( person ).isNotNull();
                assertThat( person.getName() ).isEqualTo( "Car" );

            } );
    }

    public void testNestedFirstLevelReferenceTargetProperty() {
        myFixture.configureByFile( "NestedFirstLevelReferenceTargetProperty.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiMethod.class, method -> {
                assertThat( method.getName() ).isEqualTo( "setMyDriver" );
                assertThat( method.getPresentation() ).isNotNull();
                assertThat( method.getPresentation().getPresentableText() ).isEqualTo( "setMyDriver(PersonDto)" );
                assertThat( method.getParameterList().getParametersCount() ).isEqualTo( 1 );
                assertThat( method.getReturnType() ).isNotNull();
                assertThat( method.getReturnType().getPresentableText() ).isEqualTo( "void" );
                PsiClass person = PsiTreeUtil.getParentOfType( method, PsiClass.class );
                assertThat( person ).isNotNull();
                assertThat( person.getName() ).isEqualTo( "CarDto" );

            } );
    }

    public void testNestedSecondLevelReferenceSourceProperty() {
        myFixture.configureByFile( "NestedSecondLevelReferenceSourceProperty.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiMethod.class, method -> {
                assertThat( method.getName() ).isEqualTo( "getName" );
                assertThat( method.getPresentation() ).isNotNull();
                assertThat( method.getPresentation().getPresentableText() ).isEqualTo( "getName()" );
                assertThat( method.getParameterList().getParametersCount() ).isEqualTo( 0 );
                assertThat( method.getReturnType() ).isNotNull();
                assertThat( method.getReturnType().getPresentableText() ).isEqualTo( "String" );
                PsiClass person = PsiTreeUtil.getParentOfType( method, PsiClass.class );
                assertThat( person ).isNotNull();
                assertThat( person.getName() ).isEqualTo( "Person" );

            } );
    }

    public void testNestedSecondLevelReferenceTargetProperty() {
        myFixture.configureByFile( "NestedSecondLevelReferenceTargetProperty.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiMethod.class, method -> {
                assertThat( method.getName() ).isEqualTo( "setName" );
                assertThat( method.getPresentation() ).isNotNull();
                assertThat( method.getPresentation().getPresentableText() ).isEqualTo( "setName(String)" );
                assertThat( method.getParameterList().getParametersCount() ).isEqualTo( 1 );
                assertThat( method.getReturnType() ).isNotNull();
                assertThat( method.getReturnType().getPresentableText() ).isEqualTo( "void" );
                PsiClass person = PsiTreeUtil.getParentOfType( method, PsiClass.class );
                assertThat( person ).isNotNull();
                assertThat( person.getName() ).isEqualTo( "PersonDto" );

            } );
    }

    public void testTargetPropertyReferencesTargetParameter() {
        myFixture.configureByFile( "TargetPropertyReferencesTargetParameter.java" );
        PsiElement reference = myFixture.getElementAtCaret();
        assertThat( reference )
            .isInstanceOfSatisfying( PsiParameter.class, parameter -> {
                assertThat( parameter.getName() ).isEqualTo( "target" );
                assertThat( parameter.getType().getPresentableText() ).isEqualTo( "CarDto" );
                PsiMethod mappingMethod = PsiTreeUtil.getParentOfType( parameter, PsiMethod.class );
                assertThat( mappingMethod ).isNotNull();
                assertThat( mappingMethod.getName() ).isEqualTo( "update" );
                assertThat( mappingMethod.getReturnType() ).isNotNull();
                assertThat( mappingMethod.getReturnType().getPresentableText() ).isEqualTo( "void" );
            } );
    }

    public void testTargetWithCollectionGetterMapper() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "myStringList",
                "myStringSet"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createVariable( "myStringList", "List<String>" ),
                createVariable( "myStringSet", "Set<String>" )
            );

        PsiElement reference = myFixture.getElementAtCaret();
        assertThat( reference )
            .isInstanceOfSatisfying(
                PsiMethod.class, method -> {
                    assertThat( method.getName() ).isEqualTo( "getMyStringList" );
                    assertThat( method.getPresentation() ).isNotNull();
                    assertThat( method.getPresentation().getPresentableText() ).isEqualTo( "getMyStringList()" );
                    assertThat( method.getParameterList().getParametersCount() ).isEqualTo( 0 );
                    assertThat( method.getReturnType() ).isNotNull();
                    assertThat( method.getReturnType().getPresentableText() ).isEqualTo( "List<String>" );
                }
            );
    }

    public void testTargetWithMapGetterMapper() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "myMap"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createVariable( "myMap", "Map<String, String>" )
            );

        PsiElement reference = myFixture.getElementAtCaret();
        assertThat( reference )
            .isInstanceOfSatisfying(
                PsiMethod.class, method -> {
                    assertThat( method.getName() ).isEqualTo( "getMyMap" );
                    assertThat( method.getPresentation() ).isNotNull();
                    assertThat( method.getPresentation().getPresentableText() ).isEqualTo( "getMyMap()" );
                    assertThat( method.getParameterList().getParametersCount() ).isEqualTo( 0 );
                    assertThat( method.getReturnType() ).isNotNull();
                    assertThat( method.getReturnType().getPresentableText() ).isEqualTo( "Map<String, String>" );
                }
            );
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

    public void testSourcePropertyReferencesSourceParameter() {
        myFixture.configureByFile( "SourcePropertyReferencesSourceParameter.java" );
        PsiElement reference = myFixture.getElementAtCaret();
        assertThat( reference )
            .isInstanceOfSatisfying( PsiParameter.class, parameter -> {
                assertThat( parameter.getName() ).isEqualTo( "source" );
                assertThat( parameter.getType().getPresentableText() ).isEqualTo( "Car" );
                PsiMethod mappingMethod = PsiTreeUtil.getParentOfType( parameter, PsiMethod.class );
                assertThat( mappingMethod ).isNotNull();
                assertThat( mappingMethod.getName() ).isEqualTo( "map" );
                assertThat( mappingMethod.getReturnType() ).isNotNull();
                assertThat( mappingMethod.getReturnType().getPresentableText() ).isEqualTo( "CarDto" );
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

    public void testFluentCarMapperReferenceEmptyTargetProperty() {
        myFixture.configureByFile( "FluentCarMapperReferenceEmptyTargetProperty.java" );
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

    public void testFluentCarMapperReferenceUnknownTargetProperty() {
        myFixture.configureByFile( "FluentCarMapperReferenceUnknownTargetProperty.java" );
        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testCarMapperReferencePublicStaticFieldTargetProperty() {
        myFixture.configureByFile( "CarMapperReferencePublicStaticFieldTargetProperty.java" );
        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testCarMapperReferenceProtectedTargetProperty() {
        myFixture.configureByFile( "CarMapperReferenceProtectedTargetProperty.java" );
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

    public void testUnknownNestedSourceProperty() {
        configureByFile( "DeepNestedUnknownSourceProperty.java" );
        assertThat( myItems ).isEmpty();

        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testUnknownNestedTargetProperty() {
        configureByFile( "DeepNestedUnknownTargetProperty.java" );
        assertThat( myItems ).isEmpty();

        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testMultipleSourceParametersSourceIsNotParameter() {
        myFixture.configureByFile( "MultipleSourceParametersSourceIsNotParameter.java" );
        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testSourcePropertyIsList() {
        configureByTestName();
        assertThat( myItems ).isEmpty();
    }

    public void testSourcePropertyIsArray() {
        configureByTestName();
        assertThat( myItems ).isEmpty();
    }

    public void testSourcePropertyIsMap() {
        configureByTestName();
        assertThat( myItems ).isEmpty();
    }

    public void testNestedSecondLevelSourcePropertyIsIterable() {
        configureByTestName();
        assertThat( myItems ).isEmpty();
    }

    public void testTargetPropertyIsArray() {
        configureByTestName();
        assertThat( myItems ).isEmpty();
    }

    public void testTargetPropertyIsIterable() {
        configureByTestName();
        assertThat( myItems ).isEmpty();
    }

    public void testTargetPropertyIsMap() {
        configureByTestName();
        assertThat( myItems ).isEmpty();
    }

    public void testNestedSecondLevelTargetPropertyIsIterable() {
        configureByTestName();
        assertThat( myItems ).isEmpty();
    }

    public void testOverriddenSource() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "id"
            );
    }

    public void testOverriddenTarget() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "id"
            );
    }

    public void testMapperWithBuilderWithSingleConstructor() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "address",
                "city"
            );
    }

    public void testMapperWithBuilderWithMultipleConstructors() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "address",
                "city"
            );
    }

    public void testMapperWithGenericBuilder() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "address",
                "city"
            );
    }

    public void testMapperWithSuperBuilder() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "baseValue",
                "value"
            );

        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiMethod.class, method -> {
                assertThat( method.getName() ).isEqualTo( "baseValue" );
                assertThat( method.getPresentation() ).isNotNull();
                assertThat( method.getPresentation().getPresentableText() ).isEqualTo( "baseValue(String)" );
                assertThat( method.getParameterList().getParametersCount() ).isEqualTo( 1 );
                assertThat( method.getReturnType() ).isNotNull();
            } );
    }

    public void testMapperWithBuilderAndBeanMappingDisabledBuilder() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "targetValue"
            );
    }

    public void testMapperWithBuilderAndMapperDisabledBuilder() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "targetValue"
            );
    }

    public void testMapperWithBuilderAndMapperDisabledBuilderAndBeanMappingEnable() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "builderValue"
            );
    }

    public void testMapperWithBuilderAndMapperDisabledBuilderAndBeanMappingOther() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "targetValue"
            );
    }

    public void testMapperWithBuilderAndMapperConfigDisabledBuilder() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "targetValue"
            );
    }

    public void testMapperWithBuilderAndMapperConfigDisabledBuilderAndMapperEnable() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "builderValue"
            );
    }

}
