[![CircleCI](https://circleci.com/gh/lipinskipawel/game-engine.svg?style=svg)](https://circleci.com/gh/lipinskipawel/game-engine)

# Game engine
Engine for 2D football game

## Requirements
 - JDK 12
 
## Build from source
Use maven as a build tool and execute command `mvn clean package` to create jar file.

## How to release
 - make sure you are following [semver](https://semver.org) in term of API breaking changes
 - mark your version in pom by `-SNAPSHOT`
 - make sure to change `username` in the pom.xml or pass argument `-Dusername=your_scm_username`
 - execute `mvn release:prepare`
 - if you want to make another changes you have to execute `mvn release:clean`
 - execute `mvn release:perform`
 
 
