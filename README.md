# Utopia Engine

A clojure implementation of <https://boardgamegeek.com/boardgame/75223/utopia-engine>

This is an experiment in building a system that is 100% driven by data.

## Goals

* The user should not have to learn an api
* All data relevant to the user will be contained in a simple game-data map
* No arguments when executing functions

## Usage

### Starting a game

`utopia-engine.core/new-game` is a map representing a new game.

### Playing a game

The `:possible-actions` field on the game-data map is a vector of actions that can be executed by the user.

An **action* is a map containing a type keyword, a string description and an execute function.

The execute function takes no arguments and returns a game map

```
;; example action
{:type :search,
 :description "Search the Wilderness",
 :execute #function[utopia-engine.game-engine/action/fn--14022]}
```

### Game Data

The game-data map contains 3 fields

* `:possible-actions` as described above
* `:universe` representing info about the utopia engine universe. This data never changes
* `:game-state` the state of the game that gets updated when actions are executed
