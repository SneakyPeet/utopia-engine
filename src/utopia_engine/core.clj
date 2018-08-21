(ns utopia-engine.core
  (:require [clojure.spec.alpha :as s]
            [utopia-engine.game-engine :refer [game action]]
            [utopia-engine.search :as search]))


(def new-game
  (let [initial-state {:last-action :start-game}
        start-action (search/search-action initial-state)]
    (game initial-state [start-action])))
