/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.util.patterns;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor;
import org.jetbrains.kotlin.idea.caches.resolve.ResolutionUtils;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.KtAnnotationEntry;
import org.jetbrains.kotlin.resolve.BindingContext;
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode;

/**
 * @author Filip Hrisafov
 */
public class KtAnnotationEntryPattern extends PsiElementPattern<KtAnnotationEntry, KtAnnotationEntryPattern> {

    static final KtAnnotationEntryPattern KT_ANNOTATION_ENTRY_PATTERN = new KtAnnotationEntryPattern();

    private KtAnnotationEntryPattern() {
        super( KtAnnotationEntry.class );
    }

    public KtAnnotationEntryPattern qName(ElementPattern<String> pattern) {
        return with( new PatternCondition<KtAnnotationEntry>( "qName" ) {
            @Override
            public boolean accepts(@NotNull KtAnnotationEntry ktAnnotation, ProcessingContext context) {
                AnnotationDescriptor descriptor = ResolutionUtils.analyze(
                    ktAnnotation,
                    BodyResolveMode.PARTIAL_FOR_COMPLETION
                ).get( BindingContext.ANNOTATION, ktAnnotation );

                if ( descriptor == null ) {
                    return false;
                }

                FqName fqName = descriptor.getFqName();
                if ( fqName == null ) {
                    return false;
                }
                return pattern.accepts( fqName.asString(), context );
            }
        } );
    }

}
