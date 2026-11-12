### Features

### Enhancements

* Support generics in `@Mapping` source and target references (#83)
* Do not report unknown source properties when a `Map` is used as the source parameter (#243)

### Bugs

* Completion in `java(...)` expressions does not work for record targets (#245)
* Kotlin references fail with `NoClassDefFoundError` in IntelliJ IDEA 2026.3 (#281)
* `componentModel` completion shows unrelated suggestions (#275)

### Documentation

### Build

* Release the plugin through a GitHub Actions workflow
* Sign the plugin and attest the build provenance of the released plugin
* Verify the dependencies of the plugin distribution and the Gradle plugins
* Test against IntelliJ IDEA 2026.1, 2026.2 and 2026.3
* Update Gradle to 9.8.2

### Minimum Supported IntelliJ

* 2026.1
