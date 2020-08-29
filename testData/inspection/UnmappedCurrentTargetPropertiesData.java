/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.data;

public class UnmappedCurrentTargetPropertiesData {

    public static class Source {

        private NestedSource nested;

        public NestedSource getNested() {
            return nested;
        }

        public void setNested(NestedSource nested) {
            this.nested = nested;
        }
    }

    public static class NestedSource {

        private String name;
        private String matching;
        private String moreSource;
        private String onlyInSource;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMatching() {
            return matching;
        }

        public void setMatching(String matching) {
            this.matching = matching;
        }

        public String getMoreSource() {
            return moreSource;
        }

        public void setMoreSource(String moreSource) {
            this.moreSource = moreSource;
        }

        public String getOnlyInSource() {
            return onlyInSource;
        }

        public void setOnlyInSource(String onlyInSource) {
            this.onlyInSource = onlyInSource;
        }
    }

    public static class Target {

        public static final String EMPTY_STRING = "";

        private String testName;
        private String matching;
        private String moreTarget;

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public String getMatching() {
            return matching;
        }

        public void setMatching(String matching) {
            this.matching = matching;
        }

        public String getMoreTarget() {
            return moreTarget;
        }

        public void setMoreTarget(String moreTarget) {
            this.moreTarget = moreTarget;
        }
    }

}
