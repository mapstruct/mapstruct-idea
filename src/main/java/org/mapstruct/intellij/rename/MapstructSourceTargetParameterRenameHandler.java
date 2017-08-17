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
package org.mapstruct.intellij.rename;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.refactoring.rename.PsiElementRenameHandler;

import static com.intellij.patterns.StandardPatterns.or;
import static org.mapstruct.intellij.util.MapstructElementUtils.mappingElementPattern;

/**
 * @author Filip Hrisafov
 */
public class MapstructSourceTargetParameterRenameHandler extends PsiElementRenameHandler {

    private static final ElementPattern<PsiElement> MAPPING_SOURCE_OR_TARGET = or(
        mappingElementPattern( "source" ),
        mappingElementPattern( "target" )
    );

    @Override
    public boolean isAvailableOnDataContext(DataContext dataContext) {
        return MAPPING_SOURCE_OR_TARGET.accepts( findNameSuggestionContext( dataContext ) )
            && getElement( dataContext ) instanceof PsiMethod
            && super.isAvailableOnDataContext( dataContext );
    }

    private static PsiElement findNameSuggestionContext(DataContext dataContext) {
        final Editor editor = CommonDataKeys.EDITOR.getData( dataContext );
        final PsiFile file = CommonDataKeys.PSI_FILE.getData( dataContext );
        if ( editor == null || file == null ) {
            return null;
        }

        PsiElement nameSuggestionContext = file.findElementAt( editor.getCaretModel().getOffset() );
        if ( nameSuggestionContext == null && editor.getCaretModel().getOffset() > 0 ) {
            nameSuggestionContext = file.findElementAt( editor.getCaretModel().getOffset() - 1 );
        }
        return nameSuggestionContext;
    }
}
