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
