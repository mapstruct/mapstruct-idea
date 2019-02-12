/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij;

import java.util.Collection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.usageView.UsageInfo;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class MapstructMethodUsagesSearcherTest extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/usages";
    }

    public void testFindUsagesSourceReferenceMethod() {
        myFixture.configureByFiles( "RenameSourceReference.java" );
        Collection<UsageInfo> usages = myFixture.findUsages( myFixture.getElementAtCaret() );
        assertThat( usages ).hasSize( 1 );
        UsageInfo usageInfo = usages.iterator().next();
        PsiElement element = usageInfo.getElement();
        assertThat( element )
            .isInstanceOfSatisfying( PsiLiteralExpression.class, expression -> {
                PsiReference[] references = expression.getReferences();
                assertThat( references ).hasSize( 1 );
                assertThat( references[0] )
                    .isInstanceOfSatisfying( PsiReferenceBase.class, psiReferenceBase -> {
                        assertThat( psiReferenceBase.getValue() ).isEqualTo( "name" );
                    } );
            } );
    }

    public void testFindUsagesTargetReferenceMethod() {
        myFixture.configureByFiles( "RenameTargetReference.java" );
        Collection<UsageInfo> usages = myFixture.findUsages( myFixture.getElementAtCaret() );
        assertThat( usages ).hasSize( 1 );
        UsageInfo usageInfo = usages.iterator().next();
        PsiElement element = usageInfo.getElement();
        assertThat( element )
            .isInstanceOfSatisfying( PsiLiteralExpression.class, expression -> {
                PsiReference[] references = expression.getReferences();
                assertThat( references ).hasSize( 1 );
                assertThat( references[0] )
                    .isInstanceOfSatisfying( PsiReferenceBase.class, psiReferenceBase -> {
                        assertThat( psiReferenceBase.getValue() ).isEqualTo( "testName" );
                    } );
            } );
    }

    public void testFindUsagesFluentTargetReferenceMethod() {
        myFixture.configureByFiles( "RenameFluentTargetReference.java" );
        Collection<UsageInfo> usages = myFixture.findUsages( myFixture.getElementAtCaret() );
        assertThat( usages ).hasSize( 1 );
        UsageInfo usageInfo = usages.iterator().next();
        PsiElement element = usageInfo.getElement();
        assertThat( element )
            .isInstanceOfSatisfying( PsiLiteralExpression.class, expression -> {
                PsiReference[] references = expression.getReferences();
                assertThat( references ).hasSize( 1 );
                assertThat( references[0] )
                    .isInstanceOfSatisfying( PsiReferenceBase.class, psiReferenceBase -> {
                        assertThat( psiReferenceBase.getValue() ).isEqualTo( "testName" );
                    } );
            } );
    }

    public void testFindUsagesBuilderTargetReferenceMethod() {
        myFixture.configureByFiles( "RenameBuilderTargetReference.java" );
        Collection<UsageInfo> usages = myFixture.findUsages( myFixture.getElementAtCaret() );
        assertThat( usages ).hasSize( 1 );
        UsageInfo usageInfo = usages.iterator().next();
        PsiElement element = usageInfo.getElement();
        assertThat( element )
            .isInstanceOfSatisfying( PsiLiteralExpression.class, expression -> {
                PsiReference[] references = expression.getReferences();
                assertThat( references ).hasSize( 1 );
                assertThat( references[0] )
                    .isInstanceOfSatisfying( PsiReferenceBase.class, psiReferenceBase -> {
                        assertThat( psiReferenceBase.getValue() ).isEqualTo( "testName" );
                    } );
            } );
    }

    public void testIssue10Mapper() {
        myFixture.configureByFiles( getTestName( false ) + ".java" );
        Collection<UsageInfo> usages = myFixture.findUsages( myFixture.getElementAtCaret() );
        assertThat( usages ).isEmpty();
    }

    public void testFindUsagesForOnlyGetMethodOnSource() {
        myFixture.configureByFiles( "OnlyGetMethodOnSource.java" );
        Collection<UsageInfo> usages = myFixture.findUsages( myFixture.getElementAtCaret() );
        assertThat( usages ).isEmpty();
    }

    public void testFindUsagesForOnlySetMethodOnTarget() {
        myFixture.configureByFiles( "OnlySetMethodOnTarget.java" );
        Collection<UsageInfo> usages = myFixture.findUsages( myFixture.getElementAtCaret() );
        assertThat( usages ).isEmpty();
    }

    public void testRenameSourceReferenceMethod() {
        myFixture.configureByFiles( "RenameSourceReference.java" );
        myFixture.renameElementAtCaret( "getAnotherName" );
        myFixture.checkResultByFile( "RenameSourceReferenceAfter.java" );
    }

    public void testRenameTargetReferenceMethod() {
        myFixture.configureByFiles( "RenameTargetReference.java" );
        myFixture.renameElementAtCaret( "setNewName" );
        myFixture.checkResultByFile( "RenameTargetReferenceAfter.java" );
    }

    public void testRenameFluentTargetReferenceMethod() {
        myFixture.configureByFiles( "RenameFluentTargetReference.java" );
        myFixture.renameElementAtCaret( "newName" );
        myFixture.checkResultByFile( "RenameFluentTargetReferenceAfter.java" );
    }

    public void testRenameBuilderTargetReferenceMethod() {
        myFixture.configureByFiles( "RenameBuilderTargetReference.java" );
        myFixture.renameElementAtCaret( "newName" );
        myFixture.checkResultByFile( "RenameBuilderTargetReferenceAfter.java" );
    }

    public void testRenamePublicTargetReferenceMethod() {
        myFixture.configureByFiles( "RenamePublicTargetReference.java" );
        myFixture.renameElementAtCaret( "newName" );
        myFixture.checkResultByFile( "RenamePublicTargetReferenceAfter.java" );
    }

    public void testRenameSourceParameterReference() {
        myFixture.configureByFiles( "RenameSourceParameterReference.java" );
        myFixture.renameElementAtCaret( "param" );
        myFixture.checkResultByFile( "RenameSourceParameterReferenceAfter.java" );
    }

    public void testRenameTargetParameterReference() {
        myFixture.configureByFiles( "RenameTargetParameterReference.java" );
        myFixture.renameElementAtCaret( "newTarget" );
        myFixture.checkResultByFile( "RenameTargetParameterReferenceAfter.java" );
    }
}
