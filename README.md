[![CircleCI](https://circleci.com/gh/lipinskipawel/game-engine.svg?style=svg)](https://circleci.com/gh/lipinskipawel/game-engine)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.lipinskipawel/game-engine/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.lipinskipawel/game-engine)

# Game engine
Engine for 2D football game

## Requirements
 - JDK 12
 
## Build from source
Use maven as a build tool and execute command `mvn clean package` to create jar file.

## How to release, Work in progress
 - make sure you are following [semver](https://semver.org) in term of API breaking changes
 - mark your version in pom by `-SNAPSHOT`
 - make sure to change `username` in the pom.xml or pass argument `-Dusername=your_scm_username`
 - execute `mvn release:prepare`
 - if you want to make another changes you have to execute `mvn release:clean`
 - execute `mvn release:perform`
 - execute `mvn clean deploy -P releases`
 
## Links
 - Pre-release snapshots are available from [Sonatype's snapshot repository](https://oss.sonatype.org/content/repositories/snapshots/com/github/lipinskipawel/game-engine/). 
