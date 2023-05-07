/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.data;

public class UnmappedFluentTargetPropertiesData {
    public static class Source {

        private final String name;
        private final String matching;
        private final String moreSource;
        private final String onlyInSource;

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

        private final String testName;
        private final String matching;
        private final String moreTarget;

        public Target(String testName, String matching, String moreTarget) {
            this.testName = testName;
            this.matching = matching;
            this.moreTarget = moreTarget;
        }

        public String getTestName() {
            return testName;
        }

        public String getMatching() {
            return matching;
        }

        public String getMoreTarget() {
            return moreTarget;
        }
    }

}
