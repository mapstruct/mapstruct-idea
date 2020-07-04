/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.expression;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiAnnotationParameterList;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.intellij.util.MapstructElementUtils;
import org.mapstruct.intellij.util.MapstructUtil;

/**
 * @author Filip Hrisafov
 */
public class JavaExpressionInjector implements MultiHostInjector {

    private static final Pattern JAVA_EXPRESSION = Pattern.compile( "\"java\\(.*\\)\"" );

    private static final ElementPattern<PsiElement> PATTERN =
        StandardPatterns.or(
            MapstructElementUtils.mappingElementPattern( "expression" ),
            MapstructElementUtils.mappingElementPattern( "defaultExpression" )
        );

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {

        if ( PATTERN.accepts( context ) && context instanceof PsiLiteralExpression &&
            JAVA_EXPRESSION.matcher( context.getText() ).matches() ) {

            // Context is the PsiLiteralExpression
            // In order to reach the method have the following steps to do:
            // PsiLiteralExpression - "java(something)"
            // PsiNameValuePair - expression = "java(something)"
            // PsiAnnotationParameterList - target = "", expression = "java(something)"
            // PsiAnnotation - @Mapping(target = "", expression = "java(something)")
            // PsiModifierList
            // PsiMethod

            PsiAnnotationParameterList annotationParameterList = PsiTreeUtil.getParentOfType(
                context,
                PsiAnnotationParameterList.class
            );
            if ( annotationParameterList == null ) {
                return;
            }
            PsiType targetType = null;
            for ( PsiNameValuePair attribute : annotationParameterList.getAttributes() ) {
                if ( "target" .equals( attribute.getAttributeName() ) ) {
                    PsiAnnotationMemberValue attributeValue = attribute.getValue();
                    if ( attributeValue != null ) {
                        PsiReference[] references = ReferenceProvidersRegistry.getReferencesFromProviders(
                            attributeValue );
                        if ( references.length > 0 ) {
                            PsiElement resolved = references[0].resolve();
                            if ( resolved instanceof PsiMethod ) {
                                targetType = ( (PsiMethod) resolved ).getParameterList().getParameters()[0].getType();
                            }
                            else if ( resolved instanceof PsiParameter ) {
                                targetType = ( (PsiParameter) resolved ).getType();
                            }
                        }
                    }
                    break;
                }
            }

            if ( targetType == null ) {
                return;
            }

            PsiMethod method = PsiTreeUtil.getParentOfType( annotationParameterList, PsiMethod.class );;
            if ( method == null ) {
                return;
            }

            PsiClass mapperClass = PsiTreeUtil.getParentOfType( method, PsiClass.class );
            if ( mapperClass == null ) {
                return;
            }
            StringBuilder importsBuilder = new StringBuilder();
            StringBuilder prefixBuilder = new StringBuilder();

            prefixBuilder.append( "public class " )
                .append( mapperClass.getName() ).append( "Impl" )
                .append( " implements " ).append( mapperClass.getQualifiedName() ).append( "{ " )
                .append( "public " ).append( method.getReturnType().getCanonicalText() ).append( " " )
                .append( method.getName() ).append( "(" );

            PsiParameter[] parameters = method.getParameterList().getParameters();
            for ( int i = 0; i < parameters.length; i++ ) {
                if ( i != 0 ) {
                    prefixBuilder.append( "," );
                }

                PsiParameter parameter = parameters[i];
                PsiType parameterType = parameter.getType();
                PsiClass parameterClass = PsiUtil.resolveClassInType( parameterType );

                if ( parameterClass == null ) {
                    return;
                }

                importsBuilder.append( "import " ).append( parameterClass.getQualifiedName() ).append( ";\n" );

                prefixBuilder.append( parameterType.getCanonicalText() ).append( " " ).append( parameter.getName() );
            }

            prefixBuilder.append( ") {" )
                .append( targetType.getCanonicalText() ).append( " __target__ =" );


            PsiClass targetClass = PsiUtil.resolveClassInType( targetType );
            if ( targetClass != null ) {
                importsBuilder.append( "import " ).append( targetClass.getQualifiedName() ).append( ";\n" );
            }
            if ( targetType instanceof PsiClassType ) {
                for ( PsiType typeParameter : ( (PsiClassType) targetType ).getParameters() ) {
                    PsiClass typeClass = PsiUtil.resolveClassInType( typeParameter );
                    if ( typeClass != null ) {
                        importsBuilder.append( "import " ).append( typeClass.getQualifiedName() ).append( ";\n" );
                    }
                }
            }

            PsiAnnotation mapper = mapperClass.getAnnotation( MapstructUtil.MAPPER_ANNOTATION_FQN );
            if ( mapper != null ) {
                for ( PsiNameValuePair attribute : mapper.getParameterList().getAttributes() ) {
                    if ( "imports" .equals( attribute.getName() ) ) {
                        for ( PsiAnnotationMemberValue importValue : AnnotationUtil.arrayAttributeValues(
                            attribute.getValue() ) ) {

                            if ( importValue instanceof PsiJavaCodeReferenceElement ) {
                                importsBuilder.append( "import " )
                                    .append( ( (PsiJavaCodeReferenceElement) importValue ).getQualifiedName() )
                                    .append( ";" );
                            }
                        }
                    }
                }
            }

            registrar.startInjecting( JavaLanguage.INSTANCE )
                .addPlace(
                    importsBuilder.toString() + prefixBuilder.toString(),
                    ";} }",
                    (PsiLanguageInjectionHost) context,
                    new TextRange( 6, context.getTextRange().getLength() - 2 )
                )
                .doneInjecting();
        }
    }

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList( PsiAnnotationMemberValue.class );
    }
}
