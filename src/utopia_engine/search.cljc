(ns utopia-engine.search
  (:require [utopia-engine.game-engine :refer :all]))


(defn search-action [game-state]
  (action :search "Search the Wilderness" game-state))


(defn- search-region-action [region game-state]
  (action :search-region (str "Search " region) game-state region))


(defn- search-roll-action [game-state]
  (action :search-roll "Assign Search Die" game-state))


(defmethod execute :search [_ universe game-state]
  (let [searchable-regions (:regions universe)
        search-actions (map #(search-region-action % game-state) searchable-regions)]
    (cons no-state-change search-actions)))


(defmethod execute :search-region [_ _ game-state region]
  [(assoc-in game-state [:regions region] "test")
   (search-roll-action game-state)])
