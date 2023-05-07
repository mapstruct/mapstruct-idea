/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.data;

public class UnmappedTargetPropertiesData {
    public static class Source {

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

    public static class TargetWithInnerObject {
        private Target testTarget;

        public Target getTestTarget() {
            return testTarget;
        }

        public void setTestTarget(Target testTarget) {
            this.testTarget = testTarget;
        }
    }

}
