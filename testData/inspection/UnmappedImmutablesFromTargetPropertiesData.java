/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at https://www.apache.org/licenses/LICENSE-2.0
 */
package org.example.data;

public class UnmappedImmutablesFromTargetPropertiesData {
	public static class Target {

		private String targetTestName;

		public String getTargetTestName() {
			return targetTestName;
		}

		public void setTargetTestName(String targetTestName) {
			this.targetTestName = targetTestName;
		}

		public static Builder builder() {
			return new Builder();
		}

		public static class Builder {

			public final Builder from(Target instance) {
				Objects.requireNonNull(instance, "instance");
				builderTestName(instance.getTargetTestName());
				return this;
			}

			public Builder builderTestName(String testName) {

			}

			public Target build() {
				return null;
			}
		}
	}

}
