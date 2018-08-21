(ns utopia-engine.game-engine
  (:require [clojure.spec.alpha :as s]
            [utopia-engine.universe :refer [universe]]))


(s/def :execute/result (s/coll-of map? :min-count 1))
(s/def :execute/new-state map?)
(s/def :action/type keyword?)
(s/def :action/description string?)
(s/def :action/execute fn?)
(s/def :execute/action (s/keys :req-un [:action/type
                                        :action/description
                                        :action/execute]))
(s/def :execute/actions (s/coll-of :execute/action))


(defn- throw-invalid [spec data]
  (when-not (s/valid? spec data)
    (throw (ex-info (str "Invalid Result for spec " spec)
                    (s/explain-data spec data)))))


(defmulti execute (fn [type universe game-state & args] type))


(defn game [game-state possible-actions]
  {:possible-actions possible-actions
   :game-state game-state
   :universe universe})


(defn action
  [type description game-state & args]
  {:type type
   :description description
   :execute
   (fn []
     (let [final-args (into [type game-state] args)
           result (apply execute final-args)
           _ (throw-invalid :execute/result result)
           [new-state & actions] result]
       (throw-invalid :execute/new-state new-state)
       (throw-invalid :execute/actions actions)
       (game
        universe
        (-> game-state
            (merge new-state)
            (assoc :last-action type))
        actions)))})
