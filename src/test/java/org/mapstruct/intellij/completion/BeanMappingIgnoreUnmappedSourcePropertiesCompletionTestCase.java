/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mapstruct.intellij.testutil.TestUtils.createVariable;

/**
 * @author Filip Hrisafov
 */
public class BeanMappingIgnoreUnmappedSourcePropertiesCompletionTestCase extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/completion/beanmapping";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        addDirectoryToProject( "../../mapping/dto" );
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

    public void testBeanMappingIgnoreSourcePropertiesSingleCarMapper() {
        configureByTestName();
        assertCarAutoComplete();
    }

    public void testCarMapperReferenceSourcePropertyInMulti() {
        myFixture.configureByFile( "BeanMappingIgnoreSourcePropertiesSingleReferenceCarMapper.java" );
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

    public void testBeanMappingIgnoreSourcePropertiesMultiCarMapper() {
        configureByTestName();
        assertCarAutoComplete();
    }

    public void testCarMapperReferenceSourcePropertyInSingle() {
        myFixture.configureByFile( "BeanMappingIgnoreSourcePropertiesMultiReferenceCarMapper.java" );
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

}
