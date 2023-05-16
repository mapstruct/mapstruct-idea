/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
import org.mapstruct.*;
import org.example.TargetNoPropertyMethode;

@Mapper
public interface MapExpression {

    @Mapping(target = "active", defaultExpression = "java(<caret>)")
    TargetNoPropertyMethode mapDefaultExpression(String string)
}