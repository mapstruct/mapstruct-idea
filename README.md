# mapstruct-idea

An IntelliJ IDEA plugin for working with MapStruct

[![License](https://img.shields.io/badge/License-Apache%202.0-yellowgreen.svg)](https://github.com/mapstruct/mapstruct/blob/main/LICENSE.txt)

[![Build Status](https://github.com/mapstruct/mapstruct-idea/workflows/CI/badge.svg?branch=main)](https://github.com/mapstruct/mapstruct-idea/actions?query=workflow%3ACI+branch%3Amain)
[![Coverage Status](https://codecov.io/gh/mapstruct/mapstruct-idea/branch/main/graph/badge.svg)](https://codecov.io/gh/mapstruct/mapstruct-idea)

* [What is MapStruct?](#what-is-mapstruct)
* [Features](#features)
* [Requirements](#requirements)
* [Building from Source](#building-from-source)
* [Licensing](#licensing)

## What is MapStruct?

MapStruct is a Java [annotation processor](https://docs.oracle.com/javase/6/docs/technotes/guides/apt/index.html) for the generation of type-safe and performant mappers for Java bean classes.

To learn more about MapStruct have a look at the [mapstruct](https://github.com/mapstruct/mapstruct) repository or the [website](https://mapstruct.org/)

## Features

* Code completions
  * Completion of `target` and `source` properties in `@Mapping` annotation (nested properties also work)
  * Completion of `target` and `source` properties in `@ValueMapping` annotation
  * Completion of `componentModel` in `@Mapper` and `@MapperConfig` annotations
  * Completion of `qualifiedByName` in `@Mapping` annotation
* Go To Declaration for properties in `target` and `source` to setters / getters
* Go To Declaration for `Mapping#qualifiedByName`
* Find usages of properties in `target` and `source` and find usages of setters / getters in `@Mapping` annotations
* Highlighting properties in `target` and `source`
* Errors and Quick fixes:
  * `@Mapper` or `@MapperConfig` annotation missing
  * Unmapped target properties with quick fixes: Add unmapped target property and Ignore unmapped target property.
    Uses `unmappedTargetPolicy` to determine the severity that should be used
  * No `source` defined in `@Mapping` annotation
  * More than one `source` in `@Mapping` annotation defined with quick fixes: Remove `source`. Remove `constant`. Remove `expression`. Use `constant` as `defaultValue`. Use `expression` as `defaultExpression`. 
  * More than one default source in `@Mapping` annotation defined with quick fixes: Remove `defaultValue`. Remove `defaultExpression`.
  * `target` mapped more than once by `@Mapping` annotations with quick fixes: Remove annotation and change target property.
  * `*` used as a source in `@Mapping` annotation with quick fixes: Replace `*` with method parameter name.
  * Unknown reference inspection for `source` and `target` in `@Mapping` and `@ValueMapping` annotation. 
  * Unknown reference inspection for `qualifiedByName` in `@Mapping` annotation
 
## Requirements

The MapStruct plugin requires Java 11 or later

## Building from Source

Since the project has been migrated to the Gradle and [Gradle IntelliJ plugin][gradle-intellij-plugin],
the build process is much simpler. The only thing to build the plugin is to run:

    ./gradlew build
    
All required dependencies like Grammar-Kit, JFlex are downloaded in the background and triggered properly
during the build process. You can also test the plugin easily with running:

    ./gradlew runIde
    
All of the gradle tasks can be connected to the IntelliJ debugger, so the development process is very easy.

## Licensing

The MapStruct plugin is licensed under the Apache License, Version 2.0 (the "License"); you may not use it except in compliance with the License. You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.
