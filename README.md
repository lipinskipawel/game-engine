![build master](https://github.com/lipinskipawel/game-engine/actions/workflows/build.yml/badge.svg?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.lipinskipawel/game-engine/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.lipinskipawel/game-engine)

# Game engine

It is lightweight zero-dependency engine for 2D football game.

## Requirements

- JDK 11

## Build from source

Use Gradle as a build tool and execute command `./gradlew build` to create jar file.

## How to release game-engine module

Release process is semi-automated. It begins by adding a tag version (e.g. `v6.1.0`) to a specific commit and assigning
version to `version` in `build.gradle.kts`. Tagged commit will be release as a new version of library. Versioning is
made according to [semver]. Every change has to be described in the [changelog] file.

Next release have to be triggered manually by Github action release.

After that Github workflow will react on the release and publish library to Maven Central Repository.

[semver]: https://semver.org
[changelog]: CHANGELOG.md
