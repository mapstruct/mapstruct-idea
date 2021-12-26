/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.expression;

import java.util.Map;
import java.util.TreeMap;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.intention.impl.QuickEditAction;
import com.intellij.codeInsight.intention.impl.QuickEditHandler;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.intellij.lang.annotations.Language;
import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
public class JavaExpressionInjectionTest extends MapstructBaseCompletionTestCase {

    private static final String PREFIX = "/*{";
    private static final String SUFFIX = "}*/";
    private static final String MAPPER_PARAM_NAME = "mapper";
    private static final String MAPPER = PREFIX + MAPPER_PARAM_NAME + SUFFIX;
    private static final String MAPPING_PARAM_NAME = "mapping";
    private static final String MAPPING = PREFIX + MAPPING_PARAM_NAME + SUFFIX;

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
        "@Mapper(" + MAPPER + ")\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    " + MAPPING + "\n" +
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
        "@Mapper(" + MAPPER + ")\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    " + MAPPING + "\n" +
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
        "@Mapper(" + MAPPER + ")\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    " + MAPPING + "\n" +
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
        "@Mapper(" + MAPPER + ")\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    " + MAPPING + "\n" +
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
        "@Mapper(" + MAPPER + ")\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    " + MAPPING + "\n" +
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
        "@Mapper(" + MAPPER + ")\n" +
        "public interface CarMapper {\n" +
        "\n" +
        "    " + MAPPING + "\n" +
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
        "@Mapper(" + MAPPER + ")\n" +
        "public interface CarMapper<T, R> {\n" +
        "\n" +
        "    " + MAPPING + "\n" +
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
        String mapper = formatMapper( CAR_MAPPER, mapping );
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
        String mapper = formatMapper( CAR_MAPPER, mapping );
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
        String mapper = formatMapper( CAR_MAPPER, mapping );
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
        String mapper = formatMapper( CAR_MAPPER, mapping );
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
        String mapper = formatMapper( CAR_MAPPER, mapping, "imports = java.util.Collections.class" );
        configureMapperByText( mapper );

        assertJavaFragment( "import CarMapper;\n" +
            "import java.util.Collections;\n" +
            "import org.example.dto.Car;\n" +
            "\n" +
            "@SuppressWarnings(\"unused\")\n" +
            "abstract class CarMapperImpl\n" +
            "    implements CarMapper {\n" +
            "\n" +
            "    void __test__(\n" +
            "        Car car\n" +
            "    ) {\n" +
            "        String __target__ = Collections;\n" +
            "    }\n" +
            "}" );

    }

    public void testExpressionWithMapperWithCustomImports() {
        withMapperWithCustomImports( "expression" );
        withMapperWithCustomImports( "defaultExpression" );
    }

    protected void withMapperWithCustomImports(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(Utils<caret>)\")\n";
        @Language("java")
        String mapper = formatMapper( CAR_MAPPER, mapping, "imports = org.example.dto.Utils.class" );
        configureMapperByText( mapper );

        assertJavaFragment( "import CarMapper;\n" +
            "import org.example.dto.Car;\n" +
            "import org.example.dto.Utils;\n" +
            "\n" +
            "@SuppressWarnings(\"unused\")\n" +
            "abstract class CarMapperImpl\n" +
            "    implements CarMapper {\n" +
            "\n" +
            "    void __test__(\n" +
            "        Car car\n" +
            "    ) {\n" +
            "        String __target__ = Utils;\n" +
            "    }\n" +
            "}" );
    }

    public void testExpressionWithMapperWithoutImports() {
        withMapperWithoutImports( "expression" );
        withMapperWithoutImports( "defaultExpression" );
    }

    protected void withMapperWithoutImports(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(Collections<caret>)\")\n";
        @Language("java")
        String mapper = formatMapper( CAR_MAPPER, mapping );
        configureMapperByText( mapper );

        assertJavaFragment( "import CarMapper;\n" +
            "import org.example.dto.Car;\n" +
            "\n" +
            "@SuppressWarnings(\"unused\")\n" +
            "abstract class CarMapperImpl\n" +
            "    implements CarMapper {\n" +
            "\n" +
            "    void __test__(\n" +
            "        Car car\n" +
            "    ) {\n" +
            "        String __target__ = Collections;\n" +
            "    }\n" +
            "}" );

    }

    public void testExpressionWithMultiSourceParameters() {
        withMultiSourceParameters( "expression" );
        withMultiSourceParameters( "defaultExpression" );
    }

    protected void withMultiSourceParameters(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(car.<caret>)\")\n";
        @Language("java")
        String mapper = formatMapper( CAR_MAPPER_MULTI_SOURCE, mapping );
        configureMapperByText( mapper );

        assertJavaFragment( "import CarMapper;\n" +
            "import org.example.dto.Car;\n" +
            "\n" +
            "@SuppressWarnings(\"unused\")\n" +
            "abstract class CarMapperImpl\n" +
            "    implements CarMapper {\n" +
            "\n" +
            "    void __test__(\n" +
            "        Car car,\n" +
            "        String make\n" +
            "    ) {\n" +
            "        String __target__ = car.;\n" +
            "    }\n" +
            "}" );
    }

    public void testExpressionWithGenericSourceParameters() {
        withGenericSourceParameters( "expression" );
        withGenericSourceParameters( "defaultExpression" );
    }

    protected void withGenericSourceParameters(String attribute) {
        String mapping =
            "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(carWrapper.getValue().<caret>)\")\n";
        @Language("java")
        String mapper = formatMapper( CAR_MAPPER_FROM_WRAPPER, mapping );
        configureMapperByText( mapper );

        assertJavaFragment( "import CarMapper;\n" +
            "import org.example.dto.Car;\n" +
            "import org.example.dto.Wrapper;\n" +
            "\n" +
            "@SuppressWarnings(\"unused\")\n" +
            "abstract class CarMapperImpl\n" +
            "    implements CarMapper {\n" +
            "\n" +
            "    void __test__(\n" +
            "        Wrapper<Car> carWrapper\n" +
            "    ) {\n" +
            "        String __target__ = carWrapper.getValue().;\n" +
            "    }\n" +
            "}" );
    }

    public void testExpressionWithSourceParameterWithAnnotations() {
        withExpressionWithSourceParameterWithAnnotations( "expression" );
        withExpressionWithSourceParameterWithAnnotations( "defaultExpression" );
    }

    protected void withExpressionWithSourceParameterWithAnnotations(String attribute) {
        String mapping =
            "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(carWrapper.getValue().<caret>)\")\n";
        @Language("java")
        String mapper = formatMapper( CAR_MAPPER_FROM_WRAPPER_WITH_ANNOTATION, mapping );
        configureMapperByText( mapper );

        assertJavaFragment( "import CarMapper;\n" +
            "import org.example.dto.Car;\n" +
            "import org.example.dto.Wrapper;\n" +
            "\n" +
            "@SuppressWarnings(\"unused\")\n" +
            "abstract class CarMapperImpl\n" +
            "    implements CarMapper {\n" +
            "\n" +
            "    void __test__(\n" +
            "        @Context\n" +
            "        Wrapper<Car> carWrapper\n" +
            "    ) {\n" +
            "        String __target__ = carWrapper.getValue().;\n" +
            "    }\n" +
            "}" );
    }

    public void testExpressionWithSourceParameterWithMultipleGenerics() {
        withExpressionWithSourceParameterWithMultipleGenerics( "expression" );
        withExpressionWithSourceParameterWithMultipleGenerics( "defaultExpression" );
    }

    protected void withExpressionWithSourceParameterWithMultipleGenerics(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute +
            " = \"java(carWrapper.getValue().apply(null, null).<caret>)\")\n";
        @Language("java")
        String mapper = formatMapper( CAR_MAPPER_FROM_WRAPPER_WITH_MULTI_GENERICS, mapping );
        configureMapperByText( mapper );

        assertJavaFragment( "import CarMapper;\n" +
            "import java.util.function.BiFunction;\n" +
            "import org.example.dto.Car;\n" +
            "import org.example.dto.Wrapper;\n" +
            "\n" +
            "@SuppressWarnings(\"unused\")\n" +
            "abstract class CarMapperImpl\n" +
            "    implements CarMapper {\n" +
            "\n" +
            "    void __test__(\n" +
            "        Wrapper<BiFunction<String, Number, Car>> carWrapper\n" +
            "    ) {\n" +
            "        String __target__ = carWrapper.getValue().apply(null, null).;\n" +
            "    }\n" +
            "}" );
    }

    public void testExpressionWithGenericMethod() {
        withExpressionWithGenericMethod( "expression" );
        withExpressionWithGenericMethod( "defaultExpression" );
    }

    protected void withExpressionWithGenericMethod(String attribute) {
        String mapping = "@Mapping(target = \"seatCount\", " + attribute + " = \"java(numberWrapper.<caret>)\")\n";
        @Language("java")
        String mapper = formatMapper( CAR_MAPPER_FROM_NUMBER_WRAPPER, mapping );
        configureMapperByText( mapper );

        assertJavaFragment( "import CarMapper;\n" +
            "import org.example.dto.NumberWrapper;\n" +
            "\n" +
            "@SuppressWarnings(\"unused\")\n" +
            "abstract class CarMapperImpl\n" +
            "    implements CarMapper {\n" +
            "\n" +
            "    <T extends Number> void __test__(\n" +
            "        NumberWrapper<T> numberWrapper\n" +
            "    ) {\n" +
            "        int __target__ = numberWrapper.;\n" +
            "    }\n" +
            "}" );
    }

    public void testExpressionWithGenericMapper() {
        withGenericMapper( "expression" );
        withGenericMapper( "defaultExpression" );
    }

    protected void withGenericMapper(String attribute) {
        String mapping = "@Mapping(target = \"manufacturingYear\", " + attribute + " = \"java(car.<caret>)\")\n";
        @Language("java")
        String mapper = formatMapper( GENERIC_MAPPER, mapping );
        configureMapperByText( mapper );

        assertJavaFragment( "import CarMapper;\n" +
            "import org.example.dto.Car;\n" +
            "\n" +
            "@SuppressWarnings(\"unused\")\n" +
            "abstract class CarMapperImpl<T, R>\n" +
            "    implements CarMapper<T, R> {\n" +
            "\n" +
            "    void __test__(\n" +
            "        Car car,\n" +
            "        String make\n" +
            "    ) {\n" +
            "        String __target__ = car.;\n" +
            "    }\n" +
            "}" );
    }

    public void testExpressionWithClassMapper() {
        withClassMapper( "expression" );
        withClassMapper( "defaultExpression" );
    }

    protected void withClassMapper(String attribute) {
        configureMapperByText(
            "import org.mapstruct.Mapper;\n" +
            "import org.mapstruct.Mapping;\n" +
            "import org.example.dto.CarDto;\n" +
            "import org.example.dto.Car;\n" +
            "\n" +
            "@Mapper\n" +
            "public abstract class CarMapper {\n" +
            "\n" +
            "    @Mapping( target = \"manufacturingYear\", " + attribute + " = \"java(car.<caret>)\")\n" +
            "    CarDto carToCarDto(Car car);\n" +
            "}" );

        assertJavaFragment( "import CarMapper;\n" +
            "import org.example.dto.Car;\n" +
            "\n" +
            "@SuppressWarnings(\"unused\")\n" +
            "abstract class CarMapperImpl\n" +
            "    extends CarMapper {\n" +
            "\n" +
            "    void __test__(\n" +
            "        Car car\n" +
            "    ) {\n" +
            "        String __target__ = car.;\n" +
            "    }\n" +
            "}" );
    }

    private PsiFile configureMapperByText(@Language("java") String text) {
        return myFixture.configureByText( JavaFileType.INSTANCE, text );
    }

    private static String formatMapper(String mapperTemplate, String mapping) {
        return formatMapper( mapperTemplate, mapping, "" );
    }

    private static String formatMapper(String mapperTemplate, String mapping, String mapper) {
        Map<String, String> map = new TreeMap<>();
        map.put( MAPPING_PARAM_NAME, StringUtils.defaultIfEmpty( mapping, "" ) );
        map.put( MAPPER_PARAM_NAME, StringUtils.defaultIfEmpty( mapper, "" ) );
        return new StringSubstitutor( map, PREFIX, SUFFIX ).replace( mapperTemplate );
    }

    private void assertJavaFragment(@Language("java") String expectedJavaFragment) {
        myFixture.launchAction( new QuickEditAction() );
        QuickEditHandler editHandler = myFixture.getFile().getUserData( QuickEditAction.QUICK_EDIT_HANDLER );
        assertThat( editHandler ).as( "Quick Edit Handler" ).isNotNull();
        assertThat( editHandler.getNewFile().getText() )
            .isEqualTo( expectedJavaFragment );
    }
}
