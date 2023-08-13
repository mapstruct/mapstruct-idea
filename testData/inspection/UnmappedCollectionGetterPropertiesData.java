/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class UnmappedCollectionGetterPropertiesData {
    public static class Source {

        private List<String> listSource;
        private Set<Strinf> setSource;
        private Map<String, String> mapSource;

        public List<String> getListSource() {
            return listSource;
        }

        public Set<Strinf> getSetSource() {
            return setSource;
        }

        public Map<String, String> getMapSource() {
            return mapSource;
        }
    }

    public static class Target {

        private List<String> listTarget;
        private Set<Strinf> setTarget;
        private Map<String, String> mapTarget;
        private String stringTarget;

        public List<String> getListTarget() {
            return listTarget;
        }

        public Set<Strinf> getSetTarget() {
            return setTarget;
        }

        public Map<String, String> getMapTarget() {
            return mapTarget;
        }

        public String getStringTarget() {
            return stringTarget;
        }
    }

}
