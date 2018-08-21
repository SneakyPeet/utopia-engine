(ns utopia-engine.core
  (:require [clojure.spec.alpha :as s]
            [utopia-engine.game-engine :refer [game action]]
            [utopia-engine.search :as search]))


(def new-game
  (let [initial-state {:last-action :start-game}
        start-action (search/search-action initial-state)]
    (game initial-state [start-action])))



(comment 
  (let [state-0   new-game
        execute-1 (-> state-0 :possible-actions first :execute)
        state-1   (execute-1)
        execute-2 (-> state-1 :possible-actions first :execute)
        state-2   (execute-2)]
    state-2))
