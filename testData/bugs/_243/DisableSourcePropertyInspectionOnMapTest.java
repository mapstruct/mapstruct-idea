/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import java.time.LocalDate;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
abstract class Issue243Mapper {

    @Mapping(source = "exDate", target = "exDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "payDate", target = "payDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "recordDate", target = "recordDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "annDate", target = "annDate", dateFormat = "yyyy-MM-dd")
    public abstract CorporateAction toCorporateAction(Map<String, String> rowValues);
}

class CorporateAction {
    public LocalDate exDate;
    public LocalDate payDate;
    public LocalDate recordDate;
    public LocalDate annDate;
}
