/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */

import org.mapstruct.Mapper;
import org.example.data.UnmappedSuperBuilderMultiLevelTargetPropertiesData.PersonEntity;

@Mapper
interface MultiLevelMapper {

    PersonEntity <warning descr="Unmapped target properties: age, id, name">map</warning>(String input);

}
