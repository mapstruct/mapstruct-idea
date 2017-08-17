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
package org.mapstruct.intellij.codeinsight.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.util.MapstructUtil;

/**
 * A base reference to target / source annotation.
 *
 * @author Filip Hrisafov
 */
abstract class MapstructBaseReference extends PsiReferenceBase<PsiLiteral> {

    private final MapstructBaseReference previous;

    /**
     * Create a reference.
     *
     * @param element the literal where the text is
     * @param previous the previous reference ({@code null} if there is no previous reference)
     * @param rangeInElement the range in the {@code element} for which this reference is valid
     */
    MapstructBaseReference(@NotNull PsiLiteral element,
        @Nullable MapstructBaseReference previous, TextRange rangeInElement) {
        super( element, rangeInElement );
        this.previous = previous;
    }

    /**
     * @return the class that should be used for doing auto-completion against
     */
    @Nullable
    PsiClass getRelevantClass() {
        PsiClass relevantClass = null;
        if ( previous != null ) {
            relevantClass = PsiUtil.resolveClassInType( previous.resolvedType() );
        }
        if ( relevantClass != null ) {
            return relevantClass;
        }
        PsiMethod mappingMethod = PsiTreeUtil.getParentOfType( getElement(), PsiMethod.class );
        if ( mappingMethod == null ) {
            return null;
        }
        return getRelevantClass( mappingMethod );
    }

    /**
     * @param mappingMethod the method that is being mapped
     *
     * @return the class that should be used for doing auto-completion against
     */
    abstract PsiClass getRelevantClass(@NotNull PsiMethod mappingMethod);

    /**
     * Should return the type that can be used for continuing the auto-completion. For example for source it is the
     * return type of the getter, whereas for the target it is the type of the parameter in the setter.
     *
     * @return the type of the resolved {@link PsiElement}
     */
    @Nullable
    abstract PsiType resolvedType();

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return super.handleElementRename( MapstructUtil.getPropertyName( newElementName ) );
    }

    private static ElementManipulator<PsiLiteral> getManipulator(PsiLiteral psiLiteral) {
        return ElementManipulators.getNotNullManipulator( psiLiteral );
    }

    /**
     * Create all the references for the provided {@code psiLiteral}.
     *
     * @param psiLiteral the literal that contain
     * @param creator The creator that should be used to create new references
     * @param <T> the type of the reference that needs to be created
     *
     * @return the array of all the references
     */
    static <T extends MapstructBaseReference> PsiReference[] create(PsiLiteral psiLiteral,
        ReferenceCreator<T> creator) {
        ElementManipulator<PsiLiteral> manipulator = getManipulator( psiLiteral );
        TextRange rangeInElement = manipulator.getRangeInElement( psiLiteral );
        String targetValue = rangeInElement.substring( psiLiteral.getText() );
        String[] parts = targetValue.split( "\\." );
        if ( parts.length == 0 ) {
            return PsiReference.EMPTY_ARRAY;
        }
        int nextStart = rangeInElement.getStartOffset();

        PsiReference[] references = new PsiReference[parts.length];
        T lastReference = null;

        for ( int i = 0; i < parts.length; i++ ) {
            String part = parts[i];
            int endOffset = nextStart + part.length();
            if ( i != 0 ) {
                nextStart++;
                endOffset++;
            }
            lastReference = creator.create( psiLiteral, lastReference, new TextRange( nextStart, endOffset ) );
            nextStart = endOffset;
            references[i] = lastReference;
        }

        return references;
    }
}
