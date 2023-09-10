/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;

@Mapper(config = MapstructIssue2318Mapper.Config.class)
interface MapstructIssue2318Mapper {

    @MapperConfig
    interface Config {

        @Mapping(target = "parentValue1", source = "holder")
        TargetParent mapParent(SourceParent parent);

        @InheritConfiguration(name = "mapParent")
        @Mapping(target = "childValue", source = "value")
        @Mapping(target = "parentValue2", source = "holder.parentValue2")
        TargetChild mapChild(SourceChild child);
    }

    @InheritConfiguration(name = "mapChild")
    TargetChild mapChild(SourceChild child);

    default String parentValue1(SourceParent.Holder holder) {
        return holder.getParentValue1();
    }

    class SourceParent {
        private Holder holder;

        public Holder getHolder() {
            return holder;
        }

        public void setHolder(Holder holder) {
            this.holder = holder;
        }

        public static class Holder {
            private String parentValue1;
            private Integer parentValue2;

            public String getParentValue1() {
                return parentValue1;
            }

            public void setParentValue1(String parentValue1) {
                this.parentValue1 = parentValue1;
            }

            public Integer getParentValue2() {
                return parentValue2;
            }

            public void setParentValue2(Integer parentValue2) {
                this.parentValue2 = parentValue2;
            }
        }
    }

    class SourceChild extends SourceParent {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    class TargetParent {
        private String parentValue1;
        private Integer parentValue2;

        public String getParentValue1() {
            return parentValue1;
        }

        public void setParentValue1(String parentValue1) {
            this.parentValue1 = parentValue1;
        }

        public Integer getParentValue2() {
            return parentValue2;
        }

        public void setParentValue2(Integer parentValue2) {
            this.parentValue2 = parentValue2;
        }
    }

    class TargetChild extends TargetParent {
        private String childValue;

        public String getChildValue() {
            return childValue;
        }

        public void setChildValue(String childValue) {
            this.childValue = childValue;
        }
    }
}
