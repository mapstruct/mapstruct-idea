/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.data;

public class UnmappedFluentTargetPropertiesData {
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

        private String testName;
        private String matching;
        private String moreTarget;

        public String getTestName() {
            return testName;
        }

        public Target testName(String testName) {
            this.testName = testName;
            return this;
        }

        public String getMatching() {
            return matching;
        }

        public Target matching(String matching) {
            this.matching = matching;
            return this;
        }

        public String getMoreTarget() {
            return moreTarget;
        }

        public Target moreTarget(String moreTarget) {
            this.moreTarget = moreTarget;
            return this;
        }
    }

}
