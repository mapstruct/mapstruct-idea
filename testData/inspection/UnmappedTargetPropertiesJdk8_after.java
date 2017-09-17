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

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.example.data.UnmappedTargetPropertiesData.Target;
import org.example.data.UnmappedTargetPropertiesData.Source;

interface NotMapStructMapper {

    Target map(Source source);
}

@Mapper
interface SingleMappingsMapper {

    @Mappings({
            @Mapping(target = "moreTarget", source = "moreSource"),
            @Mapping(target = "testName", ignore = true),
            @Mapping(target = "testName", source = "")
    })
    Target map(Source source);
}

@Mapper
interface SingleMappingMapper {

    @Mapping(target = "moreTarget", source = "")
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = "testName", source = "name")
    Target map(Source source);
}

@Mapper
interface NoMappingMapper {

    @Mapping(target = "testName", source = "")
    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", source = "")
    @Mapping(target = "moreTarget", ignore = true)
    Target map(Source source);

    @org.mapstruct.InheritInverseConfiguration
    Source reverse(Target target);
}

@Mapper
interface AllMappingMapper {

    @Mapping(target = "testName", source = "name")
    @Mapping(target = "moreTarget", source = "moreSource")
    Target mapWithAllMapping(Source source);
}

@Mapper
interface UpdateMapper {

    @Mapping(target = "testName", source = "")
    @Mapping(target = "testName", ignore = true)
    @Mapping(target = "moreTarget", source = "moreSource")
    void update(@MappingTarget Target target, Source source);
}

@Mapper
interface MultiSourceUpdateMapper {

    @Mapping(target = "moreTarget", source = "")
    @Mapping(target = "moreTarget", ignore = true)
    @Mapping(target = "matching", source = "")
    @Mapping(target = "matching", ignore = true)
    void update(@MappingTarget Target moreTarget, Source source, String testName, @Context String matching);
}