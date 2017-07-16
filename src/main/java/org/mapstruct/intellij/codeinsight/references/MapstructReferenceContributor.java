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

import java.util.Objects;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.filters.position.FilterPattern;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.util.MapstructUtil;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author Filip Hrisafov
 */
public class MapstructReferenceContributor extends PsiReferenceContributor {
    private static PsiElementPattern.Capture<PsiLiteral> elementPattern(String parameterName) {
        return psiElement( PsiLiteral.class ).and( new FilterPattern( new MappingAnnotationFilter( parameterName ) ) );
    }

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
            elementPattern( "target" ),
            new MappingTargetReferenceProvider( MapstructTargetReference::new )
        );
        registrar.registerReferenceProvider(
            elementPattern( "source" ),
            new MappingTargetReferenceProvider( MapstructSourceReference::new )
        );
    }

    private static class MappingAnnotationFilter implements ElementFilter {

        private final String myParameterName;

        public MappingAnnotationFilter(@NotNull @NonNls String parameterName) {
            myParameterName = parameterName;
        }

        public boolean isAcceptable(Object element, PsiElement context) {
            PsiNameValuePair pair = PsiTreeUtil.getParentOfType(
                context,
                PsiNameValuePair.class,
                false,
                PsiMember.class,
                PsiStatement.class
            );
            if ( pair == null || !Objects.equals( myParameterName, pair.getName() ) ) {
                return false;
            }

            PsiAnnotation annotation = PsiTreeUtil.getParentOfType( pair, PsiAnnotation.class );
            return MapstructUtil.isMappingAnnotation( annotation );
        }

        public boolean isClassAcceptable(Class hintClass) {
            return PsiLiteral.class.isAssignableFrom( hintClass );
        }
    }
}
