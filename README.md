![build workflow](https://github.com/lipinskipawel/game-engine/actions/workflows/build.yml/badge.svg)
[![CircleCI Status](https://circleci.com/gh/lipinskipawel/game-engine.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/lipinskipawel/game-engine)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.lipinskipawel/game-engine/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.lipinskipawel/game-engine)

# Game engine

It is lightweight zero-dependency engine for 2D football game.

## Requirements

- JDK 11

## Build from source

Use Gradle as a build tool and execute command `gradle build` to create jar file.

## How to release game-engine module

Using Gradle

- update your `gradle.properties` files to reflect gpg settings
- decide which version you will be releasing according to [semver] and update [build.gradle.kts]
- execute `gradle publish`
- update [changelog] about new version
- update version in [build.gradle.kts]
- tag current commit with a tag "v`released-version`"

[semver]: https://semver.org
[changelog]: CHANGELOG.md

### Example gradle.properties file

```
signing.gnupg.executable=gpg
signing.gnupg.keyName=abc
signing.gnupg.passphrase=abc
OSSRH_USERNAME=abc
OSSRH_PASSWORD=abc
```

signing.gnupg.keyName - last 8 characters of public key
