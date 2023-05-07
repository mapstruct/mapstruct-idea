/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.codeinsight.completion;

import java.util.List;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.StandardPatterns.or;
import static org.mapstruct.intellij.util.MapstructElementUtils.mapperConfigElementPattern;
import static org.mapstruct.intellij.util.MapstructElementUtils.mapperElementPattern;

/**
 * @author Filip Hrisafov
 */
public class ComponentModelCompletionContributor extends CompletionContributor {

    public ComponentModelCompletionContributor() {

        extend(
            CompletionType.BASIC,
            or(
                mapperElementPattern( "componentModel" ),
                mapperConfigElementPattern( "componentModel" )
            ),
            new ComponentModelCompletionProvider()
        );
    }

    public static class ComponentModelCompletionProvider extends CompletionProvider<CompletionParameters> {

        //TODO If we can somehow get access to all the AnnotationBasedComponentModelProcessor and extract
        // their getComponentModelIdentifier then we can use those. I don't know how to do this within the plugin
        private static final List<String> COMPONENT_MODEL_TYPES = List.of( "default", "cdi", "spring", "jsr330" );

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context,
            @NotNull CompletionResultSet result) {
            PsiElement position = parameters.getPosition();

            if ( !( position.getParent() instanceof PsiLiteralExpression ) ) {
                //We should only return if we are in a literal expression, i.e. inside the quotes
                return;
            }

            for ( String type : COMPONENT_MODEL_TYPES ) {
                if ( result.getPrefixMatcher().isStartMatch( type ) ) {
                    result.addElement( LookupElementBuilder.create( type ) );
                }
            }
        }

    }
}
