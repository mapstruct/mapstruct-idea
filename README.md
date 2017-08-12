# mapstruct-idea

An IntelliJ IDEA plugin for working with MapStruct

[![License](https://img.shields.io/badge/License-Apache%202.0-yellowgreen.svg)](https://github.com/mapstruct/mapstruct/blob/master/LICENSE.txt)

[![Build Status](https://travis-ci.org/mapstruct/mapstruct-idea.svg?branch=master)](https://travis-ci.org/mapstruct/mapstruct)
[![Coverage Status](https://codecov.io/gh/mapstruct/mapstruct-idea/branch/master/graph/badge.svg)](https://codecov.io/gh/mapstruct/mapstruct-idea)

* [What is MapStruct?](#what-is-mapstruct)
* [Requirements](#requirements)
* [Building from Source](#building-from-source)
* [Licensing](#licensing)

## What is MapStruct?

MapStruct is a Java [annotation processor](http://docs.oracle.com/javase/6/docs/technotes/guides/apt/index.html) for the generation of type-safe and performant mappers for Java bean classes.

To learn more about MapStruct have a look at the [mapstruct](https://github.com/mapstruct/mapstruct) repository or the [website](http://mapstruct.org/)

## Requirements

The MapStruct plugin requires Java 1.8 or later

## Building from Source

Since the project has been migrated to the Gradle and [Gradle IntelliJ plugin][gradle-intellij-plugin],
the build process is much simpler. The only thing to build the plugin is to run:

    ./gradlew build
    
All required dependencies like Grammar-Kit, JFlex are downloaded in the background and triggered properly
during the build process. You can also test the plugin easily with running:

    ./gradlew runIde
    
All of the gradle tasks can be connected to the IntelliJ debugger, so the development process is very easy.

## Licensing

The MapStruct plugin is licensed under the Apache License, Version 2.0 (the "License"); you may not use it except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
