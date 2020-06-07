/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.expression;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.intellij.lang.annotations.Language;
import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class JavaExpressionInjectionTest extends MapstructBaseCompletionTestCase {

    private static final String CAR_MAPPER = "import java.util.List;\n" +
        "\n" +
        "import org.mapstruct.Mapper;\n" +
        "import org.mapstruct.Mapping;\n" +
        "import org.example.dto.CarDto;\n" +
        "import org.example.dto.Car;\n" +
        "\n" +
        "@Mapper(%s)\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    %s" +
        "    CarDto carToCarDto(Car car);\n" +
        "}";

    @Override
    protected String getTestDataPath() {
        return "testData/expression";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        addDirectoryToProject( "dto" );
    }

    public void testExpressionWithNoTargetDefinedMapper() {
        noTargetDefinedMapper( "expression" );
        noTargetDefinedMapper( "defaultExpression" );
    }

    protected void noTargetDefinedMapper(String attribute) {
        String mapping = "@Mapping(target = \"\", " + attribute + " = \"java(car.<caret>)\")\n";
        @Language("java")
        String mapper = String.format( CAR_MAPPER, "", mapping );
        PsiFile file = configureMapperByText( mapper );

        assertThat( myFixture.completeBasic() )
            .extracting( LookupElementPresentation::renderElement )
            .extracting( LookupElementPresentation::getItemText )
            .isEmpty();

        PsiElement elementAt = file.findElementAt( myFixture.getCaretOffset() );
        assertThat( elementAt )
            .isNotNull()
            .isInstanceOf( PsiJavaToken.class );
        assertThat( elementAt.getText() ).isEqualTo( "\"java(car.)\"" );
    }

    public void testExpressionWithoutJavaExpression() {
        withoutJavaExpresion( "expression" );
        withoutJavaExpresion( "defaultExpression" );
    }

    protected void withoutJavaExpresion(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"car<caret>\")\n";
        @Language("java")
        String mapper = String.format( CAR_MAPPER, "", mapping );
        PsiFile file = configureMapperByText( mapper );

        PsiElement elementAt = file.findElementAt( myFixture.getCaretOffset() );
        assertThat( elementAt )
            .isNotNull()
            .isInstanceOf( PsiJavaToken.class );
        assertThat( elementAt.getText() ).isEqualTo( "\"car\"" );
    }

    public void testExpressionWithTargetDefinedMapper() {
        withTargetDefinedMapper( "expression" );
        withTargetDefinedMapper( "defaultExpression" );
    }

    protected void withTargetDefinedMapper(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(car.<caret>)\")\n";
        @Language("java")
        String mapper = String.format( CAR_MAPPER, "", mapping );
        PsiFile file = configureMapperByText( mapper );

        assertThat( myFixture.completeBasic() )
            .extracting( LookupElementPresentation::renderElement )
            .extracting( LookupElementPresentation::getItemText )
            .contains(
                "getMake",
                "setMake",
                "getManufacturingDate",
                "setManufacturingDate",
                "getNumberOfSeats",
                "setNumberOfSeats"
            );

        assertThat( myFixture.complete( CompletionType.SMART ) )
            .extracting( LookupElementPresentation::renderElement )
            .extracting( LookupElementPresentation::getItemText )
            .containsExactlyInAnyOrder( "getMake", "toString" );

        PsiElement elementAt = file.findElementAt( myFixture.getCaretOffset() );
        assertThat( elementAt )
            .isNotNull()
            .isInstanceOf( PsiJavaToken.class );
        assertThat( elementAt.getText() ).isEqualTo( ";" );
    }

    public void testExpressionWithMapperWithImports() {
        withMapperWithImports( "expression" );
        withMapperWithImports( "defaultExpression" );
    }

    protected void withMapperWithImports(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(Collections<caret>)\")\n";
        @Language("java")
        String mapper = String.format( CAR_MAPPER, "imports = Collections.class", mapping );
        PsiFile file = configureMapperByText( mapper );

        assertThat( myFixture.completeBasic() )
            .extracting( LookupElementPresentation::renderElement )
            .extracting( LookupElementPresentation::getItemText )
            .contains(
                "Collections"
            );

        PsiElement elementAt = file.findElementAt( myFixture.getCaretOffset() - 1 );
        assertThat( elementAt )
            .isNotNull()
            .isInstanceOf( PsiIdentifier.class );
        assertThat( elementAt.getText() ).isEqualTo( "Collections" );

        PsiReference[] references = ReferenceProvidersRegistry.getReferencesFromProviders( elementAt );
        assertThat( references ).isEmpty();

    }

    public void testExpressionWithMapperWithoutImports() {
        withMapperWithoutImports( "expression" );
        withMapperWithoutImports( "defaultExpression" );
    }

    protected void withMapperWithoutImports(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(Collections<caret>)\")\n";
        @Language("java")
        String mapper = String.format( CAR_MAPPER, "", mapping );
        PsiFile file = configureMapperByText( mapper );

        assertThat( myFixture.completeBasic() )
            .extracting( LookupElementPresentation::renderElement )
            .extracting( LookupElementPresentation::getItemText )
            .contains(
                "Collections"
            );

        PsiElement elementAt = file.findElementAt( myFixture.getCaretOffset() - 1 );
        assertThat( elementAt )
            .isNotNull()
            .isInstanceOf( PsiIdentifier.class );
        assertThat( elementAt.getText() ).isEqualTo( "Collections" );

        PsiReference[] references = ReferenceProvidersRegistry.getReferencesFromProviders( elementAt );
        assertThat( references ).isEmpty();

    }

    private PsiFile configureMapperByText(@Language("java") String text) {
        return myFixture.configureByText( JavaFileType.INSTANCE, text );
    }
}
