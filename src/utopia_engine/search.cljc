(ns utopia-engine.search
  (:require [utopia-engine.game-engine :refer :all]))


(defn search-action [game-state]
  (action :search "Search the Wilderness" game-state))
