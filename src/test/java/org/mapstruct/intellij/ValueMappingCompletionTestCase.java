/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiReference;
import org.intellij.lang.annotations.Language;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mapstruct.intellij.testutil.TestUtils.createField;

/**
 * @author Filip Hrisafov
 */
public class ValueMappingCompletionTestCase extends MapstructBaseCompletionTestCase {

    @Language("JAVA")
    private static final String SOURCE_VALUE_MAPPING_DYNAMIC = """
            import org.mapstruct.Mapper;
            import org.mapstruct.ValueMapping;
            import org.mapstruct.example.ExternalRoofType;
            import org.mapstruct.example.RoofType;

            @Mapper
            public interface RoofTypeMapper {

                %s\
                ExternalRoofType map(RoofType type);
            }""";

    @Language("JAVA")
    private static final String SOURCE_VALUE_MAPPINGS_DYNAMIC = """
            import org.mapstruct.Mapper;
            import org.mapstruct.ValueMapping;
            import org.mapstruct.ValueMappings;
            import org.mapstruct.example.ExternalRoofType;
            import org.mapstruct.example.RoofType;

            @Mapper
            public interface RoofTypeMapper {

                @ValueMappings({
            %s
            })
                ExternalRoofType map(RoofType type);
            }""";

    @Language("JAVA")
    private static final String SOURCE_VALUE_MAPPING = """
            import org.mapstruct.Mapper;
            import org.mapstruct.ValueMapping;
            import org.mapstruct.example.ExternalRoofType;
            import org.mapstruct.example.RoofType;

            @Mapper
            public interface RoofTypeMapper {

                @ValueMapping(source = "<caret>%s", target = "STANDARD")
                ExternalRoofType map(RoofType type);
            }""";

    @Language("JAVA")
    private static final String TARGET_VALUE_MAPPING = """
            import org.mapstruct.Mapper;
            import org.mapstruct.ValueMapping;
            import org.mapstruct.example.ExternalRoofType;
            import org.mapstruct.example.RoofType;

            @Mapper
            public interface RoofTypeMapper {

                @ValueMapping(source = "NORMAL", target = "<caret>%s")
                ExternalRoofType map(RoofType type);
            }""";

    @Override
    protected String getTestDataPath() {
        return "testData/valuemapping";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        addDirectoryToProject( "dto" );
    }

    public void testSourceValueMappingVariants() {
        myFixture.configureByText( JavaFileType.INSTANCE, String.format( SOURCE_VALUE_MAPPING, "NORMAL" ) );
        complete();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "OPEN",
                "BOX",
                "GAMBREL",
                "NORMAL"
            );
        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingElementComparatorIgnoringFields( "myIcon" )
            .containsExactlyInAnyOrder(
                createField( "OPEN", "RoofType" ),
                createField( "BOX", "RoofType" ),
                createField( "GAMBREL", "RoofType" ),
                createField( "NORMAL", "RoofType" )
            );
    }

    public void testSourceValueMappingWithExisting() {
        String source = String.format(
            SOURCE_VALUE_MAPPING_DYNAMIC,
                """
                        @ValueMapping(source = "GAMBREL", target = "NORMAL")
                        @ValueMapping(source = "<caret>%s", target = "STANDARD")
                        """
        );
        myFixture.configureByText( JavaFileType.INSTANCE, source );
        complete();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "OPEN",
                "BOX",
                "NORMAL"
            );
        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingElementComparatorIgnoringFields( "myIcon" )
            .containsExactlyInAnyOrder(
                createField( "OPEN", "RoofType" ),
                createField( "BOX", "RoofType" ),
                createField( "NORMAL", "RoofType" )
            );
    }

    public void testSourceValueMappingsWithExisting() {
        String source = String.format(
            SOURCE_VALUE_MAPPINGS_DYNAMIC,
                """
                        @ValueMapping(source = "GAMBREL", target = "NORMAL"),
                        @ValueMapping(source = "<caret>%s", target = "STANDARD")
                        """
        );
        myFixture.configureByText( JavaFileType.INSTANCE, source );
        complete();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "OPEN",
                "BOX",
                "NORMAL"
            );
        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingElementComparatorIgnoringFields( "myIcon" )
            .containsExactlyInAnyOrder(
                createField( "OPEN", "RoofType" ),
                createField( "BOX", "RoofType" ),
                createField( "NORMAL", "RoofType" )
            );
    }

    public void testSourceValueMappingAllValuesAlreadyMapped() {
        String source = String.format(
            SOURCE_VALUE_MAPPING_DYNAMIC,
                """
                        @ValueMapping(source = "OPEN", target = "NORMAL")
                        @ValueMapping(source = "BOX", target = "NORMAL")
                        @ValueMapping(source = "GAMBREL", target = "NORMAL")
                        @ValueMapping(source = "NORMAL", target = "NORMAL")
                        @ValueMapping(source = "<caret>%s", target = "STANDARD")
                        """
        );
        myFixture.configureByText( JavaFileType.INSTANCE, source );
        complete();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .isEmpty();
        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingElementComparatorIgnoringFields( "myIcon" )
            .isEmpty();
    }

    public void testSourceValueMappingsAllValuesAlreadyMapped() {
        String source = String.format(
            SOURCE_VALUE_MAPPINGS_DYNAMIC,
                """
                        @ValueMapping(source = "OPEN", target = "NORMAL"),
                        @ValueMapping(source = "BOX", target = "NORMAL"),
                        @ValueMapping(source = "GAMBREL", target = "NORMAL"),
                        @ValueMapping(source = "NORMAL", target = "NORMAL"),
                        @ValueMapping(source = "<caret>%s", target = "STANDARD")
                        """
        );
        myFixture.configureByText( JavaFileType.INSTANCE, source );
        complete();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .isEmpty();
        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingElementComparatorIgnoringFields( "myIcon" )
            .isEmpty();
    }

    public void testSourceValueMappingResolveToEnum() {
        myFixture.configureByText( JavaFileType.INSTANCE, String.format( SOURCE_VALUE_MAPPING, "NORMAL" ) );

        PsiElement reference = myFixture.getElementAtCaret();
        assertThat( reference )
            .isInstanceOfSatisfying( PsiEnumConstant.class, enumConstant -> {
                assertThat( enumConstant.getName() ).isEqualTo( "NORMAL" );
                assertThat( enumConstant.getContainingClass() ).isNotNull();
                assertThat( enumConstant.getContainingClass().getName() ).isEqualTo( "RoofType" );
            } );
    }

    public void testSourceValueMappingSourceParameterIsNotEnum() {
        String source = String.format( SOURCE_VALUE_MAPPING, "NORMAL" );
        source = source.replace( "map(RoofType type)", "map(Integer type)" );
        myFixture.configureByText( JavaFileType.INSTANCE, source );

        complete();
        assertThat( myItems ).isEmpty();

        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testSourceValueMappingReferenceIsNotEnumField() {
        String source = String.format( SOURCE_VALUE_MAPPING, "DUMMY" );
        myFixture.configureByText( JavaFileType.INSTANCE, source );

        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testSourceValueMappingReferenceIsEmpty() {
        String source = String.format( SOURCE_VALUE_MAPPING, "" );
        myFixture.configureByText( JavaFileType.INSTANCE, source );

        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testTargetValueMappingVariants() {
        myFixture.configureByText( JavaFileType.INSTANCE, String.format( TARGET_VALUE_MAPPING, "STANDARD" ) );
        complete();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "OPEN",
                "BOX",
                "GAMBREL",
                "STANDARD"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingElementComparatorIgnoringFields( "myIcon" )
            .containsExactlyInAnyOrder(
                createField( "OPEN", "ExternalRoofType" ),
                createField( "BOX", "ExternalRoofType" ),
                createField( "GAMBREL", "ExternalRoofType" ),
                createField( "STANDARD", "ExternalRoofType" )
            );
    }

    public void testTargetValueMappingResolveToEnum() {
        myFixture.configureByText( JavaFileType.INSTANCE, String.format( TARGET_VALUE_MAPPING, "STANDARD" ) );

        PsiElement reference = myFixture.getElementAtCaret();
        assertThat( reference )
            .isInstanceOfSatisfying( PsiEnumConstant.class, enumConstant -> {
                assertThat( enumConstant.getName() ).isEqualTo( "STANDARD" );
                assertThat( enumConstant.getContainingClass() ).isNotNull();
                assertThat( enumConstant.getContainingClass().getName() ).isEqualTo( "ExternalRoofType" );
            } );
    }

    public void testTargetValueMappingNoReturnType() {
        String source = String.format( TARGET_VALUE_MAPPING, "STANDARD" );
        source = source.replace( "ExternalRoofType map", "void map" );
        myFixture.configureByText( JavaFileType.INSTANCE, source );

        complete();
        assertThat( myItems ).isEmpty();

        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testTargetValueMappingReturnTypeIsNotEnum() {
        String source = String.format( TARGET_VALUE_MAPPING, "STANDARD" );
        source = source.replace( "ExternalRoofType map", "Integer map" );
        myFixture.configureByText( JavaFileType.INSTANCE, source );

        complete();
        assertThat( myItems ).isEmpty();

        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testTargetValueMappingReferenceIsNotEnumField() {
        String source = String.format( TARGET_VALUE_MAPPING, "DUMMY" );
        myFixture.configureByText( JavaFileType.INSTANCE, source );

        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testTargetValueMappingReferenceIsEmpty() {
        String source = String.format( TARGET_VALUE_MAPPING, "" );
        myFixture.configureByText( JavaFileType.INSTANCE, source );

        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testMethodIsNotValueMapping() {
        String source = String.format( TARGET_VALUE_MAPPING, "STANDARD" );
        source = source.replace( "RoofType type", "" );
        myFixture.configureByText( JavaFileType.INSTANCE, source );

        complete();
        assertThat( myItems ).isEmpty();

        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }

    public void testMethodIsNotValueMapping2() {
        String source = String.format( TARGET_VALUE_MAPPING, "STANDARD" );
        source = source.replace( "RoofType type", "RoofType type1, RoofType type2" );
        myFixture.configureByText( JavaFileType.INSTANCE, source );

        complete();
        assertThat( myItems ).isEmpty();

        PsiReference reference = myFixture.getFile().findReferenceAt( myFixture.getCaretOffset() );
        assertThat( reference ).isNotNull();
        assertThat( reference.resolve() ).isNull();
    }
}
