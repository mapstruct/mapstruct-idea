/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.data;

public class UnmappedSuperBuilderTargetPropertiesData {
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

        protected Target(TargetBuilder<?, ?> b) {
            this.testName = b.testName;
            this.matching = b.matching;
            this.moreTarget = b.moreTarget;
        }

        public static TargetBuilder<?, ?> builder() {
            return new TargetBuilderImpl();
        }

        public String getTestName() {
            return this.testName;
        }

        public String getMatching() {
            return this.matching;
        }

        public String getMoreTarget() {
            return this.moreTarget;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public void setMatching(String matching) {
            this.matching = matching;
        }

        public void setMoreTarget(String moreTarget) {
            this.moreTarget = moreTarget;
        }

        public static abstract class TargetBuilder<C extends Target, B extends TargetBuilder<C, B>> {
            private String testName;
            private String matching;
            private String moreTarget;

            public B testName(String testName) {
                this.testName = testName;
                return self();
            }

            public B matching(String matching) {
                this.matching = matching;
                return self();
            }

            public B moreTarget(String moreTarget) {
                this.moreTarget = moreTarget;
                return self();
            }

            protected abstract B self();

            public abstract C build();
        }

        private static final class TargetBuilderImpl extends TargetBuilder<Target, TargetBuilderImpl> {
            private TargetBuilderImpl() {
            }

            protected TargetBuilderImpl self() {
                return this;
            }

            public Target build() {
                return new Target( this );
            }
        }
    }

}
