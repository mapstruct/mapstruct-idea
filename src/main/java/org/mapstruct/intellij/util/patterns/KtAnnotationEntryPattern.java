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
import org.jetbrains.kotlin.psi.KtAnnotationEntry;
import org.jetbrains.uast.UAnnotation;
import org.jetbrains.uast.UastContextKt;

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
                UAnnotation uElement = UastContextKt.toUElement( ktAnnotation, UAnnotation.class );
                if ( uElement == null ) {
                    return false;
                }
                String name = uElement.getQualifiedName();
                return pattern.accepts( name, context );
            }
        } );
    }

}
