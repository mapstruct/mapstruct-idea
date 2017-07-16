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
package org.mapstruct.intellij.util;

import java.beans.Introspector;
import java.util.Objects;

import com.intellij.codeInsight.completion.JavaLookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiLiteral;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiFormatUtil;
import com.intellij.psi.util.PsiFormatUtilBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.Mapping;

/**
 * @author Filip Hrisafov
 */
public class MapstructUtil {

    private static final String MAPPING_ANNOTATION_FQN = Mapping.class.getName();

    /**
     * @param annotation
     *
     * @return {@code true} if the annotation is the MapStruct {@literal org.mapstring.Mapping} annotation
     */
    public static boolean isMappingAnnotation(PsiAnnotation annotation) {
        return annotation != null && Objects.equals( annotation.getQualifiedName(), MAPPING_ANNOTATION_FQN );
    }

    public static LookupElement asLookup(@NotNull Pair<PsiMethod, PsiSubstitutor> pair) {
        PsiMethod method = pair.getFirst();
        PsiSubstitutor substitutor = pair.getSecond();

        String propertyName = getPropertyName( method );
        LookupElementBuilder builder = LookupElementBuilder.create(method, propertyName )
            .withIcon(method.getIcon( Iconable.ICON_FLAG_VISIBILITY))
            .withPresentableText(propertyName)
            .withTailText( PsiFormatUtil.formatMethod(method, substitutor,
                0,
                PsiFormatUtilBase.SHOW_NAME | PsiFormatUtilBase.SHOW_TYPE));
        final PsiType returnType = method.getReturnType();
        if (returnType != null) {
            builder = builder.withTypeText(substitutor.substitute(returnType).getPresentableText());
        }

        return builder;
    }

    public static boolean isPublic(@NotNull PsiMethod method) {
        return method.hasModifierProperty( PsiModifier.PUBLIC );
    }

    public static boolean isSetter(@NotNull PsiMethod method) {
        //TODO if we can use the AccessorNamingStrategy it would be awesome
        String methodName = method.getName();
        return methodName.startsWith( "set" );
    }

    public static boolean isGetter(@NotNull PsiMethod method) {
        //TODO if we can use the AccessorNamingStrategy it would be awesome
        String methodName = method.getName();
        return methodName.startsWith( "get" ) || methodName.startsWith( "is" );
    }

    @NotNull
    @NonNls
    private static String getPropertyName(@NotNull PsiMethod method) {
        //TODO if we can use the AccessorNamingStrategy it would be awesome
        String methodName = method.getName();
        return Introspector.decapitalize( methodName.substring( methodName.startsWith( "is" ) ? 2 : 3 ) );
    }
}
