/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.intellij.util.MapstructUtil;

import static org.mapstruct.intellij.util.MapstructUtil.canDescendIntoType;

/**
 * A base reference to target / source annotation.
 *
 * @author Filip Hrisafov
 */
abstract class MapstructBaseReference extends BaseReference {

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

    @Nullable
    @Override
    public final PsiElement resolve() {
        String value = getValue();
        if ( value.isEmpty() ) {
            return null;
        }

        if ( previous != null ) {
            PsiType psiType = canDescendIntoType( previous.resolvedType() ) ? previous.resolvedType() : null;
            return psiType == null ? null : resolveInternal( value, psiType );
        }

        PsiMethod mappingMethod = getMappingMethod();

        return mappingMethod == null ? null : resolveInternal( value, mappingMethod );
    }

    /**
     * Resolved the reference from the {@code value} for the reference {@code psiClass}
     *
     * @param value the value in the annotation (never empty)
     * @param psiType the type in which the {@code value} needs to be found
     *
     * @return the resolved {@link PsiElement}
     */
    @Nullable
    abstract PsiElement resolveInternal(@NotNull String value, @NotNull PsiType psiType);

    /**
     * Resolve the reference from the {@code value} for the {@code mappingMethod}
     *
     * @param value the value in the annotation (never empty)
     * @param mappingMethod the method that is under mapping
     *
     * @return the resolved {@link PsiElement}
     */
    @Nullable
    abstract PsiElement resolveInternal(@NotNull String value, @NotNull PsiMethod mappingMethod);

    @NotNull
    @Override
    public final Object[] getVariants() {
        if ( previous != null ) {
            PsiType resolvedType = previous.resolvedType();

            return !canDescendIntoType( resolvedType ) ? LookupElement.EMPTY_ARRAY :
                getVariantsInternal( resolvedType );
        }

        PsiMethod mappingMethod = getMappingMethod();
        return mappingMethod == null ? LookupElement.EMPTY_ARRAY : getVariantsInternal( mappingMethod );
    }

    /**
     * Find all the variants for the given {@code psiClass}.
     *
     * @param psiType the type for which variants need to be returned
     *
     * @return all the variants for the provided {@code psiType}
     */
    @NotNull
    abstract Object[] getVariantsInternal(@NotNull PsiType psiType);

    /**
     * Find all the variants for the given {@code mappingMethod}.
     *
     * @param mappingMethod the mapping method for which variants need to be returned
     *
     * @return all the variants for the provided {@code mappingMethod}
     */
    @NotNull
    abstract Object[] getVariantsInternal(@NotNull PsiMethod mappingMethod);

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
        PsiElement reference = resolve();
        if ( reference instanceof PsiMethod ) {
            return super.handleElementRename( MapstructUtil.getPropertyName( newElementName ) );
        }
        else {
            return super.handleElementRename( newElementName );
        }
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
