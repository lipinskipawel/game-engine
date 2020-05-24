[![CircleCI Status](https://circleci.com/gh/lipinskipawel/game-engine.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/lipinskipawel/game-engine)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.lipinskipawel/game-engine/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.lipinskipawel/game-engine)

# Game engine
Engine for 2D football game

## Requirements
 - JDK 12
 
## Build from source
Use maven as a build tool and execute command `mvn clean package` to create jar file.

## How to release game-engine module
 - update your `.m2/settings.xml` to reflect your account settings
 - execute `cd game-engine`
 - execute `mvn release:prepare`
 - decided which version you will be releasing according to [semver](https://semver.org)
 - execute `mvn release:perform`
 - enter your passphrase
