/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import java.time.LocalDate;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
abstract class Issue243ErroneousMapper {

    @Mapping(source = "<error descr="Unknown property 'exDate'">exDate</error>", target = "exDate")
    public abstract CorporateAction mapWithIntegerKeyMap(Map<Integer, String> rowValues);

    @Mapping(source = "<error descr="Unknown property 'exDate'">exDate</error>", target = "exDate")
    public abstract void updateWithIntegerKeyMap(
        Map<Integer, String> rowValues,
        @MappingTarget CorporateAction target
    );

    @Mapping(source = "<error descr="Unknown property 'exDate'">exDate</error>", target = "exDate")
    @Mapping(source = "payDate", target = "payDate")
    public abstract CorporateAction mapWithMultipleSources(
        Map<String, String> rowValues,
        LocalDate payDate
    );
}

class CorporateAction {
    public LocalDate exDate;
    public LocalDate payDate;
}
