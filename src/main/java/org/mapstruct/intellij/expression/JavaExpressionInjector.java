/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.expression;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
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

    private void importIfNecessary(PsiClass cls, @NotNull Set<String> imports) {
        if ( cls != null
            && cls.getQualifiedName() != null
            && !cls.getQualifiedName().startsWith( "java.lang." )
        ) {
            imports.add( cls.getQualifiedName() );
        }
    }

    private void appendType(@NotNull StringBuilder sb, @NotNull Set<String> imports, @NotNull PsiType type) {
        importIfNecessary( PsiUtil.resolveClassInType( type ), imports );
        if ( !( type instanceof PsiClassType ) ) {
            sb.append( type.getPresentableText() );
            return;
        }
        PsiClassType ct = (PsiClassType) type;
        sb.append( ct.getName() );
        PsiType[] typeParameters = ct.getParameters();
        if ( typeParameters.length == 0 ) {
            return;
        }
        sb.append( '<' );
        for ( int i = 0; i < typeParameters.length; ++i ) {
            if ( i != 0 ) {
                sb.append( ", " );
            }
            appendType( sb, imports, typeParameters[i] );
        }
        sb.append( '>' );
    }

    private void appendClassSimple(@NotNull StringBuilder sb, @NotNull Set<String> imports, @NotNull PsiClass cls) {
        importIfNecessary( cls, imports );
        sb.append( cls.getName() );
        PsiTypeParameter[] typeParameters = cls.getTypeParameters();
        if ( typeParameters.length == 0 ) {
            return;
        }
        sb.append( '<' );
        for ( int i = 0; i < typeParameters.length; ++i ) {
            if ( i != 0 ) {
                sb.append( ", " );
            }
            appendClassSimple( sb, imports, typeParameters[i] );
        }
        sb.append( '>' );
    }

    private void appendClassImpl(@NotNull StringBuilder sb, @NotNull Set<String> imports, @NotNull PsiClass cls) {
        importIfNecessary( cls, imports );
        sb.append( cls.getName() ).append( "Impl" );
        appendTypeParametersHard( sb, imports, cls.getTypeParameters() );
    }

    private boolean appendTypeParametersHard(
        @NotNull StringBuilder sb, @NotNull Set<String> imports, PsiTypeParameter[] typeParameters
    ) {
        if ( typeParameters.length == 0 ) {
            return false;
        }
        sb.append( "<" );
        for ( int i = 0; i < typeParameters.length; ++i ) {
            if ( i != 0 ) {
                sb.append( ", " );
            }
            sb.append( typeParameters[i].getName() );
            PsiClassType[] ext = typeParameters[i].getExtendsListTypes();
            if ( ext.length == 0 ) {
                continue;
            }
            sb.append( " extends " );
            for ( int j = 0; j < ext.length; ++j ) {
                if ( j != 0 ) {
                    sb.append( ", " );
                }
                appendType( sb, imports, ext[j] );
            }
        }
        sb.append( ">" );
        return true;
    }

    private void appendNesting(StringBuilder sb, int level) {
        for ( int i = 0; i < level; i++ ) {
            sb.append( "    " );
        }
    }

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {

        if ( PATTERN.accepts( context ) && context instanceof PsiLiteralExpression &&
            JAVA_EXPRESSION.matcher( context.getText() ).matches() ) {

            // Context is the PsiLiteralExpression
            // In order to reach the method have the following steps to take:
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
                if ( "target".equals( attribute.getAttributeName() ) ) {
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
                            else if ( resolved instanceof PsiField ) {
                                targetType = ( (PsiField) resolved ).getType();
                            }
                        }
                    }
                    break;
                }
            }

            if ( targetType == null ) {
                return;
            }

            PsiMethod method = PsiTreeUtil.getParentOfType( annotationParameterList, PsiMethod.class );
            if ( method == null ) {
                return;
            }

            PsiClass mapperClass = PsiTreeUtil.getParentOfType( method, PsiClass.class );
            if ( mapperClass == null ) {
                return;
            }

            SortedSet<String> imports = new TreeSet<>();
            StringBuilder prefixBuilder = new StringBuilder();

            prefixBuilder.append( "\n@SuppressWarnings(\"unused\")" );
            prefixBuilder.append( "\nabstract class " );
            appendClassImpl( prefixBuilder, imports, mapperClass );
            prefixBuilder.append( "\n" );
            appendNesting( prefixBuilder, 1 );
            prefixBuilder.append( mapperClass.isInterface() ? "implements " : "extends " );
            appendClassSimple( prefixBuilder, imports, mapperClass );
            prefixBuilder.append( " {\n\n" );
            appendNesting( prefixBuilder, 1 );
            if ( appendTypeParametersHard( prefixBuilder, imports, method.getTypeParameters() ) ) {
                prefixBuilder.append( " " );
            }
            appendType( prefixBuilder, imports, targetType );
            prefixBuilder.append( " __test__(\n" );

            PsiParameter[] parameters = method.getParameterList().getParameters();
            for ( int i = 0; i < parameters.length; i++ ) {
                if ( i != 0 ) {
                    prefixBuilder.append( ",\n" );
                }

                PsiParameter parameter = parameters[i];
                PsiType parameterType = parameter.getType();
                for ( PsiAnnotation a : parameter.getAnnotations() ) {
                    appendNesting( prefixBuilder, 2 );
                    prefixBuilder.append( a.getText() ).append( "\n" );
                }
                appendNesting( prefixBuilder, 2 );
                appendType( prefixBuilder, imports, parameterType );
                prefixBuilder.append( " " ).append( parameter.getName() );
            }

            prefixBuilder.append( "\n" );
            appendNesting( prefixBuilder, 1 );
            prefixBuilder.append( ") {\n" );
            appendNesting( prefixBuilder, 2 );
            prefixBuilder.append( "return " );

            PsiAnnotation mapper = mapperClass.getAnnotation( MapstructUtil.MAPPER_ANNOTATION_FQN );
            if ( mapper != null ) {
                for ( PsiNameValuePair attribute : mapper.getParameterList().getAttributes() ) {
                    if ( "imports".equals( attribute.getName() ) ) {
                        for ( PsiAnnotationMemberValue importValue : AnnotationUtil.arrayAttributeValues(
                            attribute.getValue() ) ) {

                            if ( importValue instanceof PsiJavaCodeReferenceElement ) {
                                imports.add( ( (PsiJavaCodeReferenceElement) importValue ).getQualifiedName() );
                            }
                            else if ( importValue instanceof PsiClassObjectAccessExpression ) {
                                PsiJavaCodeReferenceElement referenceElement =
                                    ( (PsiClassObjectAccessExpression) importValue ).getOperand()
                                        .getInnermostComponentReferenceElement();
                                if ( referenceElement != null ) {
                                    imports.add( referenceElement.getQualifiedName() );
                                }
                            }
                        }
                    }
                }
            }

            registrar.startInjecting( JavaLanguage.INSTANCE )
                .addPlace(
                    imports.stream().map( imp -> "import " + imp + ";" ).collect( Collectors.joining( "\n", "", "\n" ) )
                        + prefixBuilder,
                    ";\n    }\n}",
                    (PsiLanguageInjectionHost) context,
                    new TextRange( "\"java(".length(), context.getTextRange().getLength() - ")\"".length() )
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
