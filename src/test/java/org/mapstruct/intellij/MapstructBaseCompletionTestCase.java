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
package org.mapstruct.intellij;

import com.intellij.codeInsight.completion.LightFixtureCompletionTestCase;
import com.intellij.openapi.util.text.StringUtil;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

/**
 * Base completion test case for MapStruct.
 *
 * @author Filip Hrisafov
 */
public abstract class MapstructBaseCompletionTestCase extends LightFixtureCompletionTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addEnvironmentClasses();
    }

    protected void addEnvironmentClasses() {
        //TODO maybe there is a better way to do this. Maybe unpack the dependency somewhere in build
        // and add the entire API.
        //CHECKSTYLE:OFF
        //@formatter:off
        addEnvironmentClass( "package org.mapstruct;\n" +
            "public enum ReportingPolicy {\n" +
            "    IGNOREPORTING_POLICY,\n" +
            "    WARN,\n" +
            "    ERROR;\n" +
            "}" );
        addEnvironmentClass( "package org.mapstruct;\n" +
            "public enum CollectionMappingStrategy {\n" +
            "    ACCESSOR_ONLY,\n" +
            "    SETTER_PREFERRED,\n" +
            "    ADDER_PREFERRED,\n" +
            "    TARGET_IMMUTABLE;\n" +
            "}" );
        addEnvironmentClass( "package org.mapstruct;\n" +
            "public enum NullValueMappingStrategy {\n" +
            "    RETURN_NULL,\n" +
            "    RETURN_DEFAULT;\n" +
            "}" );
        addEnvironmentClass( "package org.mapstruct;\n" +
            "public enum MappingInheritanceStrategy {\n" +
            "    EXPLICIT,\n" +
            "    AUTO_INHERIT_FROM_CONFIG;\n" +
            "}" );
        addEnvironmentClass( "package org.mapstruct;\n" +
            "public enum NullValueCheckStrategy {\n" +
            "    ON_IMPLICIT_CONVERSION,\n" +
            "    ALWAYS;\n" +
            "}" );

        addEnvironmentClass( "package org.mapstruct;\n" +
            "import static org.mapstruct.NullValueCheckStrategy.ON_IMPLICIT_CONVERSION;\n" +
            "public @interface Mapper {\n" +
            "    Class<?>[] uses() default { };\n" +
            "    Class<?>[] imports() default { };\n" +
            "    ReportingPolicy unmappedTargetPolicy() default ReportingPolicy.WARN;\n" +
            "    String componentModel() default \"default\";\n" +
            "    String implementationName() default \"<CLASS_NAME>Impl\";\n" +
            "    String implementationPackage() default \"<PACKAGE_NAME>\";\n" +
            "    Class<?> config() default void.class;\n" +
            "    CollectionMappingStrategy collectionMappingStrategy() default CollectionMappingStrategy.ACCESSOR_ONLY;\n" +
            "    NullValueMappingStrategy nullValueMappingStrategy() default NullValueMappingStrategy.RETURN_NULL;\n" +
            "    MappingInheritanceStrategy mappingInheritanceStrategy() default MappingInheritanceStrategy.EXPLICIT;" +
            "    NullValueCheckStrategy nullValueCheckStrategy() default ON_IMPLICIT_CONVERSION;\n" +
            "    boolean disableSubMappingMethodsGeneration() default false;\n" +
            "}" );
        addEnvironmentClass( "package org.mapstruct;\n" +
            "import static org.mapstruct.NullValueCheckStrategy.ON_IMPLICIT_CONVERSION;\n" +
            "public @interface MapperConfig {\n" +
            "    Class<?>[] uses() default { };\n" +
            "    ReportingPolicy unmappedTargetPolicy() default ReportingPolicy.WARN;\n" +
            "    String componentModel() default \"default\";\n" +
            "    String implementationName() default \"<CLASS_NAME>Impl\";\n" +
            "    String implementationPackage() default \"<PACKAGE_NAME>\";\n" +
            "    CollectionMappingStrategy collectionMappingStrategy() default CollectionMappingStrategy.ACCESSOR_ONLY;\n" +
            "    NullValueMappingStrategy nullValueMappingStrategy() default NullValueMappingStrategy.RETURN_NULL;\n" +
            "    MappingInheritanceStrategy mappingInheritanceStrategy() default MappingInheritanceStrategy.EXPLICIT;" +
            "    NullValueCheckStrategy nullValueCheckStrategy() default ON_IMPLICIT_CONVERSION;\n" +
            "    boolean disableSubMappingMethodsGeneration() default false;\n" +
            "}" );
        addEnvironmentClass( "package org.mapstruct;\n" +
            "import java.lang.annotation.Annotation;\n" +
            "public @interface Mapping {\n" +
            "    String target();\n" +
            "    String source() default \"\";\n" +
            "    String dateFormat() default \"\";\n" +
            "    String numberFormat() default \"\";\n" +
            "    String constant() default \"\";\n" +
            "    String expression() default \"\";\n" +
            "    boolean ignore() default false;\n" +
            "    Class<? extends Annotation>[] qualifiedBy() default { };\n" +
            "    String[] qualifiedByName() default { };\n" +
            "    Class<?> resultType() default void.class;\n" +
            "    String[] dependsOn() default { };\n" +
            "    String defaultValue() default \"\";\n" +
            "}" );

        addEnvironmentClass( "package org.mapstruct;\n" +
            "import java.lang.annotation.Annotation;\n" +
            "public @interface ValueMapping {\n" +
            "    String target();\n" +
            "    String source();\n" +
            "}" );

        addEnvironmentClass( "package org.mapstruct;\n" +
            "public @interface MappingTarget {\n" +
            "}" );
        addEnvironmentClass( "package org.mapstruct;\n" +
            "public @interface Context {\n" +
            "}" );
        //@formatter:on
        //CHECKSTYLE:ON
    }

    protected void addEnvironmentClass(@Language("JAVA") @NotNull String classText) {
        myFixture.addClass( classText );
    }

    protected void addDirectoryToProject(@NotNull String directory) {
        myFixture.copyDirectoryToProject( directory, StringUtil.getShortName( directory, '/' ) );
    }
}
