/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class IgnoredTargetsCompletionTestCase extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/completion/ignored";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        addDirectoryToProject( "../../mapping/dto" );
    }

    private void assertCarDtoAutoComplete() {
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "make",
                "seatCount",
                "manufacturingYear",
                "myDriver",
                "passengers",
                "price",
                "category",
                "available"
            );
    }

    public void testIgnoredTargetsNoPrefix() {
        configureByTestName();
        assertCarDtoAutoComplete();
    }

    public void testIgnoredTargetsWithPrefix() {
        configureByTestName();
        assertCarDtoAutoComplete();
    }

    public void testIgnoredTargetsMultipleTargets() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "manufacturingYear",
                "myDriver",
                "passengers",
                "price",
                "category",
                "available"
            );
    }

    public void testIgnoredTargetsWithPrefixMultipleTargets() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "seatCount",
                "manufacturingYear",
                "myDriver",
                "passengers",
                "price",
                "category",
                "available"
            );
    }

    public void testIgnoredTargetsWithMapping() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "manufacturingYear",
                "myDriver",
                "passengers",
                "price",
                "category",
                "available"
            );
    }

    public void testIgnoredTargetsWithPrefixAndMapping() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "manufacturingYear",
                "myDriver",
                "passengers",
                "price",
                "category",
                "available"
            );
    }

    public void testIgnoredTargetsSingleNoArray() {
        configureByTestName();
        assertCarDtoAutoComplete();
    }

    public void testIgnoredTargetsReferenceTarget() {
        myFixture.configureByFile( "IgnoredTargetsReferenceTarget.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiMethod.class, method -> {
                assertThat( method.getName() ).isEqualTo( "setSeatCount" );
            } );
    }

    public void testIgnoredTargetsReferenceTargetWithPrefix() {
        myFixture.configureByFile( "IgnoredTargetsReferenceTargetWithPrefix.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiMethod.class, method -> {
                assertThat( method.getName() ).isEqualTo( "setMake" );
            } );
    }

    public void testIgnoredTargetsReferenceUnknownTarget() {
        myFixture.configureByFile( "IgnoredTargetsReferenceUnknownTarget.java" );
        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testIgnoredTargetsReferenceNestedTarget() {
        myFixture.configureByFile( "IgnoredTargetsReferenceNestedTarget.java" );
        PsiElement reference = myFixture.getElementAtCaret();

        assertThat( reference )
            .isInstanceOfSatisfying( PsiMethod.class, method -> {
                assertThat( method.getName() ).isEqualTo( "setName" );
            } );
    }

    public void testIgnoredTargetsWithInvalidPrefix() {
        myFixture.configureByFile( "IgnoredTargetsWithInvalidPrefix.java" );
        complete();
        assertThat( myItems ).isEmpty();
    }

    public void testIgnoredTargetsMultipleAnnotations() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "manufacturingYear",
                "myDriver",
                "passengers",
                "price",
                "category",
                "available"
            );
    }

    public void testIgnoredTargetsWithIgnoredList() {
        configureByTestName();
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "manufacturingYear",
                "myDriver",
                "passengers",
                "price",
                "category",
                "available"
            );
    }

    public void testIgnoredTargetsNoPrefixKotlin() {
        configureByFile( "/" + getTestName( false ) + ".kt" );
        assertCarDtoAutoComplete();
    }

    public void testIgnoredTargetsWithIgnoredListKotlin() {
        configureByFile( "/" + getTestName( false ) + ".kt" );
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "manufacturingYear",
                "myDriver",
                "passengers",
                "price",
                "category",
                "available"
            );
    }
}
