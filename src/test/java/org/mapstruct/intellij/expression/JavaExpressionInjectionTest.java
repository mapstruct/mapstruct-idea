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
import static org.mapstruct.intellij.testutil.TestUtils.MAPPER;
import static org.mapstruct.intellij.testutil.TestUtils.MAPPING;
import static org.mapstruct.intellij.testutil.TestUtils.X_MAPPER_X;
import static org.mapstruct.intellij.testutil.TestUtils.X_MAPPING_X;
import static org.mapstruct.intellij.testutil.TestUtils.advancedFormat;

/**
 * @author Filip Hrisafov
 */
public class JavaExpressionInjectionTest extends MapstructBaseCompletionTestCase {

    @Language("java")
    private static final String CAR_MAPPER = "" +
        "import java.util.List;\n" +
        "\n" +
        "import org.mapstruct.Mapper;\n" +
        "import org.mapstruct.Mapping;\n" +
        "import org.mapstruct.Mappings;\n" +
        "import org.example.dto.CarDto;\n" +
        "import org.example.dto.Car;\n" +
        "\n" +
        "@Mapper(" + X_MAPPER_X + ")\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    " + X_MAPPING_X + "\n" +
        "    CarDto carToCarDto(Car car);\n" +
        "}";

    @Language("java")
    private static final String CAR_MAPPER_FROM_WRAPPER = "" +
        "import java.util.List;\n" +
        "\n" +
        "import org.mapstruct.Mapper;\n" +
        "import org.mapstruct.Mapping;\n" +
        "import org.mapstruct.Mappings;\n" +
        "import org.example.dto.CarDto;\n" +
        "import org.example.dto.Car;\n" +
        "import org.example.dto.Wrapper;\n" +
        "\n" +
        "@Mapper(" + X_MAPPER_X + ")\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    " + X_MAPPING_X + "\n" +
        "    CarDto carWrapperToCarDto(Wrapper<Car> carWrapper);\n" +
        "}";

    @Language("java")
    private static final String CAR_MAPPER_FROM_NUMBER_WRAPPER = "" +
        "import java.util.List;\n" +
        "\n" +
        "import org.mapstruct.Mapper;\n" +
        "import org.mapstruct.Mapping;\n" +
        "import org.mapstruct.Mappings;\n" +
        "import org.example.dto.CarDto;\n" +
        "import org.example.dto.Car;\n" +
        "import org.example.dto.NumberWrapper;\n" +
        "\n" +
        "@Mapper(" + X_MAPPER_X + ")\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    " + X_MAPPING_X + "\n" +
        "    <T extends Number> CarDto numberWrapperToCarDto(NumberWrapper<T> numberWrapper);\n" +
        "}";

    @Language("java")
    private static final String CAR_MAPPER_FROM_WRAPPER_WITH_ANNOTATION = "" +
        "import java.util.List;\n" +
        "\n" +
        "import org.mapstruct.Context;\n" +
        "import org.mapstruct.Mapper;\n" +
        "import org.mapstruct.Mapping;\n" +
        "import org.mapstruct.Mappings;\n" +
        "import org.example.dto.CarDto;\n" +
        "import org.example.dto.Car;\n" +
        "import org.example.dto.Wrapper;\n" +
        "\n" +
        "@Mapper(" + X_MAPPER_X + ")\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    " + X_MAPPING_X + "\n" +
        "    CarDto carWrapperToCarDto(@Context Wrapper<Car> carWrapper);\n" +
        "}";

    @Language("java")
    private static final String CAR_MAPPER_FROM_WRAPPER_WITH_MULTI_GENERICS = "" +
        "import java.util.List;\n" +
        "\n" +
        "import org.mapstruct.Mapper;\n" +
        "import org.mapstruct.Mapping;\n" +
        "import org.mapstruct.Mappings;\n" +
        "import org.example.dto.CarDto;\n" +
        "import org.example.dto.Car;\n" +
        "import org.example.dto.Wrapper;\n" +
        "import java.util.function.BiFunction;\n" +
        "\n" +
        "@Mapper(" + X_MAPPER_X + ")\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    " + X_MAPPING_X + "\n" +
        "    CarDto carWrapperToCarDto(Wrapper<BiFunction<String, Number, Car>> carWrapper);\n" +
        "}";

    @Language("java")
    private static final String CAR_MAPPER_MULTI_SOURCE = "" +
        "import java.util.List;\n" +
        "\n" +
        "import org.mapstruct.Mapper;\n" +
        "import org.mapstruct.Mapping;\n" +
        "import org.mapstruct.Mappings;\n" +
        "import org.example.dto.CarDto;\n" +
        "import org.example.dto.Car;\n" +
        "\n" +
        "@Mapper(" + X_MAPPER_X + ")\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    " + X_MAPPING_X + "\n" +
        "    CarDto carToCarDto(Car car, String make);\n" +
        "}";

    @Language("java")
    private static final String GENERIC_MAPPER = "" +
        "import java.util.List;\n" +
        "\n" +
        "import org.mapstruct.Mapper;\n" +
        "import org.mapstruct.Mapping;\n" +
        "import org.mapstruct.Mappings;\n" +
        "import org.example.dto.CarDto;\n" +
        "import org.example.dto.Car;\n" +
        "\n" +
        "@Mapper(" + X_MAPPER_X + ")\n" +
        "public interface CarMapper<T, R> {\n" +
        "\n" +
        "    " + X_MAPPING_X + "\n" +
        "    CarDto carToCarDto(Car car, String make);\n" +
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
        String mapper = advancedFormat( CAR_MAPPER, MAPPING, mapping );
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
        withoutJavaExpression( "expression" );
        withoutJavaExpression( "defaultExpression" );
    }

    protected void withoutJavaExpression(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"car<caret>\")\n";
        @Language("java")
        String mapper = advancedFormat( CAR_MAPPER, MAPPING, mapping );
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
        String mapper = advancedFormat( CAR_MAPPER, MAPPING, mapping );
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

    public void testExpressionWithTargetDefinedMapperInMappings() {
        withTargetDefinedMapperInMappings( "expression" );
        withTargetDefinedMapperInMappings( "defaultExpression" );
    }

    protected void withTargetDefinedMapperInMappings(String attribute) {
        String mapping = "@Mappings(\n" +
            "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(car.<caret>)\")\n" +
            ")\n";
        @Language("java")
        String mapper = advancedFormat( CAR_MAPPER, MAPPING, mapping );
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
        String mapper = advancedFormat( CAR_MAPPER,
            MAPPER, "imports = Collections.class",
            MAPPING, mapping
        );
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
        String mapper = advancedFormat( CAR_MAPPER, MAPPING, mapping );
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

    public void testExpressionWithMultiSourceParameters() {
        withMultiSourceParameters( "expression" );
        withMultiSourceParameters( "defaultExpression" );
    }

    protected void withMultiSourceParameters(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(car.<caret>)\")\n";
        @Language("java")
        String mapper = advancedFormat( CAR_MAPPER_MULTI_SOURCE, MAPPING, mapping );
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

    public void testExpressionWithGenericSourceParameters() {
        withGenericSourceParameters( "expression" );
        withGenericSourceParameters( "defaultExpression" );
    }

    protected void withGenericSourceParameters(String attribute) {
        String mapping =
            "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(carWrapper.getValue().<caret>)\")\n";
        @Language("java")
        String mapper = advancedFormat( CAR_MAPPER_FROM_WRAPPER, MAPPING, mapping );
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

    public void testExpressionWithSourceParameterWithAnnotations() {
        withExpressionWithSourceParameterWithAnnotations( "expression" );
        withExpressionWithSourceParameterWithAnnotations( "defaultExpression" );
    }

    protected void withExpressionWithSourceParameterWithAnnotations(String attribute) {
        String mapping =
            "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(carWrapper.getValue().<caret>)\")\n";
        @Language("java")
        String mapper = advancedFormat( CAR_MAPPER_FROM_WRAPPER_WITH_ANNOTATION, MAPPING, mapping );
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

    public void testExpressionWithSourceParameterWithMultipleGenerics() {
        withExpressionWithSourceParameterWithMultipleGenerics( "expression" );
        withExpressionWithSourceParameterWithMultipleGenerics( "defaultExpression" );
    }

    protected void withExpressionWithSourceParameterWithMultipleGenerics(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute +
            " = \"java(carWrapper.getValue().apply(null, null).<caret>)\")\n";
        @Language("java")
        String mapper = advancedFormat( CAR_MAPPER_FROM_WRAPPER_WITH_MULTI_GENERICS, MAPPING, mapping );
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

    public void testExpressionWithGenericMethod() {
        withExpressionWithGenericMethod( "expression" );
        withExpressionWithGenericMethod( "defaultExpression" );
    }

    protected void withExpressionWithGenericMethod(String attribute) {
        String mapping = "@Mapping(target = \"seatCount\", " + attribute + " = \"java(numberWrapper.<caret>)\")\n";
        @Language("java")
        String mapper = advancedFormat( CAR_MAPPER_FROM_NUMBER_WRAPPER, MAPPING, mapping );
        PsiFile file = configureMapperByText( mapper );

        assertThat( myFixture.completeBasic() )
            .extracting( LookupElementPresentation::renderElement )
            .extracting( LookupElementPresentation::getItemText )
            .contains(
                "getValue"
            );

        PsiElement elementAt = file.findElementAt( myFixture.getCaretOffset() );
        assertThat( elementAt )
            .isNotNull()
            .isInstanceOf( PsiJavaToken.class );
        assertThat( elementAt.getText() ).isEqualTo( ";" );
    }

    public void testExpressionWithGenericMapper() {
        withGenericMapper( "expression" );
        withGenericMapper( "defaultExpression" );
    }

    protected void withGenericMapper(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(car.<caret>)\")\n";
        @Language("java")
        String mapper = advancedFormat( GENERIC_MAPPER, MAPPING, mapping );
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

    private PsiFile configureMapperByText(@Language("java") String text) {
        return myFixture.configureByText( JavaFileType.INSTANCE, text );
    }
}
