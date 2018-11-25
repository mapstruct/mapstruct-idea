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

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

interface NotMapStructMapper {

    Target map(Source source);
}

@Mapper
interface NotMapStructMapper2 {

    NotMapStructMapper2 INSTANCE = Mappers.getMapper( NotMapStructMapper2.class );

    Target map(Source source);
}

@Mapper
interface DefaultComponentModelMapper {

    DefaultComponentModelMapper INSTANCE = Mappers.getMapper( DefaultComponentModelMapper.class );

    Target map(Source source);
}

@Mapper(componentModel = "default")
interface DefaultComponentModelMapper2 {

    DefaultComponentModelMapper2 INSTANCE = Mappers.getMapper( DefaultComponentModelMapper2.class );

    Target map(Source source);
}

@Mapper(componentModel = "spring")
interface SpringComponentModelMapper {

    Target map(Source source);
}

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface Jsr330ComponentModelMapper {

    Jsr330ComponentModelMapper INSTANCE = Mappers.getMapper( Jsr330ComponentModelMapper.class );

    Target map(Source source);
}

@Mapper()
interface CustomComponentModelMapper {

    CustomComponentModelMapper INSTANCE = Mappers.getMapper( CustomComponentModelMapper.class );

    Target map(Source source);
}

class Source {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

class Target {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}