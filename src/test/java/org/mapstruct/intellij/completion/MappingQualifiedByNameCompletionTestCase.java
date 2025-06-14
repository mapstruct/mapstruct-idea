/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mapstruct.intellij.testutil.TestUtils.createMethod;

public class MappingQualifiedByNameCompletionTestCase extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/completion/qualifiedbyname";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        addDirectoryToProject( "../../mapping/dto" );
        addDirectoryToProject( "mapper" );
    }

    public void testMappingQualifiedByNameLocalNamedReference() {
        configureByTestName();
        assertQualifiedByNameLocalReferenceAutoComplete();
    }

    private void assertQualifiedByNameLocalReferenceAutoComplete() {
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder( "numberToZero", "doubleSeatCount" );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createMethod( "numberToZero", "Long", " CarMapper#setToZero(int)" ),
                createMethod( "doubleSeatCount", "int", " CarMapper#multiplyByFactor(Double, int)" )
            );
    }

    public void testMappingQualifiedByNameLocalAndExternalNamedReference() {
        configureByTestName();
        assertQualifiedByNameLocalAndExternalReferenceAutoComplete();
    }

    private void assertQualifiedByNameLocalAndExternalReferenceAutoComplete() {
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder( "numberToZero", "doubleSeatCount", "trimString", "unwrapOptional" );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                // local methods
                createMethod( "numberToZero", "Long", " CarMapper#setToZero(int)" ),
                createMethod( "doubleSeatCount", "int", " CarMapper#multiplyByFactor(Double, int)" ),
                // methods of mappers from @Mapper(uses = ...)
                createMethod( "trimString", "String", " StringMapper#trim(String)" ),
                // methods of mappers from referenced @MapperConfig(uses = ...)
                createMethod( "unwrapOptional", "T", " OptionalMapper#unwrapOptional(Optional<T>)" )
            );
    }

    public void testMappingQualifiedByNameInsideMapperConfigCompletion() {
        configureByTestName();
        assertQualifiedByNameInsideMapperConfigAutoComplete();
    }

    private void assertQualifiedByNameInsideMapperConfigAutoComplete() {
        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsOnly( "unwrapOptional" );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .describedAs( "methods of mappers from @MapperConfig(uses = ...)" )
            .containsOnly(
                createMethod( "unwrapOptional", "T", " OptionalMapper#unwrapOptional(Optional<T>)" )
            );
    }

    public void testMappingQualifiedByNameWithAllPossibleVisibilities() {
        configureByTestName();
        assertAutoCompleteOfValidVisibilities();
    }

    private void assertAutoCompleteOfValidVisibilities() {

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "internalModifierPackagePrivate",
                "internalModifierProtected",
                "internalModifierPublic",
                "superClassModifierPackagePrivate",
                "superClassModifierProtected",
                "superClassModifierPublic",
                "samePackageModifierPackagePrivate",
                "samePackageModifierProtected",
                "samePackageModifierPublic",
                "externalPackageModifierPublic"
            );

        assertThat( myItems )
            .extracting( LookupElementPresentation::renderElement )
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                createMethod(
                    "internalModifierPackagePrivate",
                    "String",
                    " CarMapper#internalModifierPackagePrivate(String)"
                ),
                createMethod( "internalModifierProtected", "String", " CarMapper#internalModifierProtected(String)" ),
                createMethod( "internalModifierPublic", "String", " CarMapper#internalModifierPublic(String)" ),
                createMethod(
                    "superClassModifierPackagePrivate",
                    "String",
                    " BaseMapper#superClassModifierPackagePrivate(String)"
                ),
                createMethod(
                    "superClassModifierProtected",
                    "String",
                    " BaseMapper#superClassModifierProtected(String)"
                ),
                createMethod( "superClassModifierPublic", "String", " BaseMapper#superClassModifierPublic(String)" ),
                createMethod(
                    "samePackageModifierPackagePrivate",
                    "String",
                    " SamePackageMapper#samePackageModifierPackagePrivate(String)"
                ),
                createMethod(
                    "samePackageModifierProtected",
                    "String",
                    " SamePackageMapper#samePackageModifierProtected(String)"
                ),
                createMethod(
                    "samePackageModifierPublic",
                    "String",
                    " SamePackageMapper#samePackageModifierPublic(String)"
                ),
                createMethod(
                    "externalPackageModifierPublic",
                    "String",
                    " ExternalMapper#externalPackageModifierPublic(String)"
                )
            );
    }

}
