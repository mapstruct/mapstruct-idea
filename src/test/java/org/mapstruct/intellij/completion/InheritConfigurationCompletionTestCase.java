/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Oliver Erhart
 */
public class InheritConfigurationCompletionTestCase extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/completion/inherit";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        addDirectoryToProject( "../../mapping/dto" );
    }

    public void testInheritMultipleMethodsCarMapper() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "mapTo",
                "mapToBase"
                );
    }

    public void testInheritReverseWithExplicitInheritance() {
        configureByTestName();

        assertThat( myItems )
            .extracting( LookupElement::getLookupString )
            .containsExactlyInAnyOrder(
                "toCarEntity",
                "toCarEntityWithFixedAuditTrail",
                "baseDtoToEntity"
            );
    }

}
