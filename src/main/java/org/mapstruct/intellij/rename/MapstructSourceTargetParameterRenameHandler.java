/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
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
