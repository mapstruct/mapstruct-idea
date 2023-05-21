/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.intellij.bugs._110;

import org.mapstruct.intellij.MapstructBaseCompletionTestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author hdulme
 */
public class MethodeWithNoParametersTest extends MapstructBaseCompletionTestCase {

    @Override
    protected String getTestDataPath() {
        return "testData/bugs/_110";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.copyFileToProject( "TargetNoPropertyMethods.java",
                "org/example/TargetNoPropertyMethode.java" );
    }

    public void testExpressionCompletion() {
        myFixture.configureByFile( "MapExpression.java" );
        assertThat( myFixture.completeBasic() ).isEmpty();
    }

    public void testDefaultExpressionCompletion() {
        myFixture.configureByFile( "MapDefaultExpression.java" );
        assertThat( myFixture.completeBasic() ).isEmpty();
    }

    public void testTargetCompletion() {
        myFixture.configureByFile( "TargetCompletion.java" );
        assertThat( myFixture.completeBasic() ).isEmpty();
    }
}
