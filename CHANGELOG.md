# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## Unreleased

### Removed
- The MiniMax class is no longer a part of the API
- The DummyBoardEvaluator is no longer a part of the API

### Added
- Default method providing builder for MoveStrategy

## 3.5.0 - 2020.12.12
### Removed
- The direction class is no more coupled with [neuristic] library

[neuristic]: https://github.com/lipinskipawel/neuristic

### Added
- Ability to provide default depth for supported Board Evaluators
- Add third method in MoveStrategy with the timeout argument

## 3.4.0 - 2020-04-29
### Added
- Add non-binary transformation for BoardInterface
- Add alpha-beta version of MiniMax algorithm

### Fixed
- Make SmartBoardEvaluator aware when Player First hit a goal

## 3.3.0
### Added
- Method for BoardInterface which take winner of the game
- Better BoardEvaluator which is SmartBoardEvaluator

### Fixed
- Stop searching for best move when the game is over in MiniMax
- BoardInterface nextPlayerToMove
- Returning null in MiniMax#execute 

## 3.2.0
### Added
- Add BoardEvaluator

## 3.1.0
### Added
- Mark Move class with Serializable

### Fixed
- BoardInterface undo method 

## 3.0.1
### Fixed
- getPlayer method to return proper Player

## 3.0.0
### Removed
- Rename package to com.github.lipinskipawel
- Rename BoardInterface2 to BoardInterface
- Rename Point2 to Point

### Added
- Add possibility for retrieving all moves made by players

## 2.0.0
### Removed
- Remove deprecated code from 1.1.1 version

### Added
- Add module-info for declaration of the API

## 1.1.1
### Deprecated
- Deprecate legacy engine

## 1.1.0
### Added
- Add Matrix operations

## 1.0.0
### Added
- Add integration with [CircleCi]
- Add license, README
- Import engine from the [LAN-Game] project

[CircleCi]: https://circleci.com/gh/lipinskipawel/game-engine
[LAN-Game]: https://github.com/lipinskipawel/LAN-game
