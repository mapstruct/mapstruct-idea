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
