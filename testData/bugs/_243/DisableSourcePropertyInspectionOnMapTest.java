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
abstract class Issue243Mapper {

    @Mapping(source = "exDate", target = "exDate")
    @Mapping(source = "payDate", target = "payDate")
    public abstract CorporateAction mapWithStringKeyMap(Map<String, String> rowValues);

    @Mapping(source = "exDate", target = "exDate")
    public abstract void updateWithStringKeyMap(Map<String, String> rowValues, @MappingTarget CorporateAction target);

    @Mapping(source = "exDate", target = "exDate")
    public abstract CorporateAction mapWithObjectValueMap(Map<String, Object> rowValues);

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

    @Mapping(source = "rowValues.exDate", target = "exDate")
    @Mapping(source = "payDate", target = "payDate")
    public abstract CorporateAction mapWithMultipleSourcesAndMapName(
        Map<String, String> rowValues,
        LocalDate payDate
    );
}

class CorporateAction {
    public LocalDate exDate;
    public LocalDate payDate;
}
