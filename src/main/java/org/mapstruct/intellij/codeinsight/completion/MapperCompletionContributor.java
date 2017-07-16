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
package org.mapstruct.intellij.codeinsight.completion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;

/**
 * @author Filip Hrisafov
 */
public class MapperCompletionContributor extends CompletionContributor {

    public MapperCompletionContributor() {

//        extend(
//            CompletionType.BASIC,
//            PsiJavaPatterns.psiAnnotation()
//                .qName( StandardPatterns.string().oneOf( "org.mapstruct.Mapper", "Mapper" ) )
//                .insideAnnotationAttribute( "componentModel", psiElement( PsiAnnotation.class ) ),
//            new MapperCompletionProvider()
//        );

        //TODO I don't know why it doesn't work only with "org.mapstruct.Mapper". For some reason
        // the qualified name in the tests is just Mapper
        extend(
            CompletionType.BASIC,
            psiElement()
                .insideAnnotationParam(
                    StandardPatterns.string().oneOf( "org.mapstruct.Mapper", "Mapper" ),
                    "componentModel"
                ),
            new MapperCompletionProvider()
        );
    }

    public static class MapperCompletionProvider extends CompletionProvider<CompletionParameters> {

        //TODO If we can somehow get access to all the AnnotationBasedComponentModelProcessor and extract
        // their getComponentModelIdentifier then we can use those. I don't know how to do this within the plugin
        private static final List<String> COMPONENT_MODEL_TYPES = Collections.unmodifiableList( Arrays.asList(
            "default",
            "cdi",
            "spring",
            "jsr330"
        ) );

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context,
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
