/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.references;

import java.util.function.Function;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
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
    private final String value;

    /**
     * Create a reference.
     *
     * @param element the literal where the text is
     * @param previous the previous reference ({@code null} if there is no previous reference)
     * @param rangeInElement the range in the {@code element} for which this reference is valid
     */
    MapstructBaseReference(@NotNull PsiElement element,
        @Nullable MapstructBaseReference previous, TextRange rangeInElement, String value) {
        super( element, rangeInElement );
        this.previous = previous;
        this.value = value;
    }

    @Override
    @NotNull
    public String getValue() {
        if ( value != null ) {
            return value;
        }
        return super.getValue();
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
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        PsiElement reference = resolve();
        if ( reference instanceof PsiMethod ) {
            return super.handleElementRename( MapstructUtil.getPropertyName( newElementName ) );
        }
        else {
            return super.handleElementRename( newElementName );
        }
    }

    private static ElementManipulator<PsiElement> getManipulator(PsiElement psiElement) {
        return ElementManipulators.getNotNullManipulator( psiElement );
    }

    /**
     * Create all the references for the provided {@code psiLiteral}.
     *
     * @param psiElement the literal that contain
     * @param creator The creator that should be used to create new references
     * @param supportsNested whether the target value can have nested references
     * @param <T> the type of the reference that needs to be created
     *
     * @return the array of all the references
     */
    static <T extends MapstructBaseReference> PsiReference[] create(PsiElement psiElement,
        ReferenceCreator<T> creator, boolean supportsNested) {
        String targetValue;
        Function<String, TextRange> rangeCreator;
        if ( psiElement instanceof PsiReferenceExpression psiReferenceExpression ) {
            PsiElement resolvedPsiElement = psiReferenceExpression.resolve();

            PsiLiteralExpression expression = PsiTreeUtil.findChildOfType(
                resolvedPsiElement,
                PsiLiteralExpression.class
            );

            if ( expression == null ) {
                return PsiReference.EMPTY_ARRAY;
            }

            ElementManipulator<PsiElement> manipulator = getManipulator( expression );
            TextRange rangeInElement = manipulator.getRangeInElement( expression );
            targetValue = rangeInElement.substring( expression.getText() );
            rangeCreator = part -> TextRange.EMPTY_RANGE;

        }
        else {
            ElementManipulator<PsiElement> manipulator = getManipulator( psiElement );
            TextRange rangeInElement = manipulator.getRangeInElement( psiElement );
            targetValue = rangeInElement.substring( psiElement.getText() );

            rangeCreator = new RangeCreator( rangeInElement.getStartOffset() );

        }

        String[] parts = supportsNested ? targetValue.split( "\\." ) : new String[] { targetValue };
        if ( parts.length == 0 ) {
            return PsiReference.EMPTY_ARRAY;
        }

        PsiReference[] references = new PsiReference[parts.length];
        T lastReference = null;

        for ( int i = 0; i < parts.length; i++ ) {
            String part = parts[i];
            lastReference = creator.create( psiElement, lastReference, rangeCreator.apply( part ), part );
            references[i] = lastReference;
        }

        return references;
    }

    private static class RangeCreator implements Function<String, TextRange> {

        private int nextStart;
        private boolean first = true;

        private RangeCreator(int nextStart) {
            this.nextStart = nextStart;
        }

        @Override
        public TextRange apply(String part) {
            int endOffset = nextStart + part.length();
            if ( !first ) {
                nextStart++;
                endOffset++;
            }
            else {
                first = false;
            }
            TextRange range = new TextRange( nextStart, endOffset );
            nextStart = endOffset;
            return range;
        }
    }
}
