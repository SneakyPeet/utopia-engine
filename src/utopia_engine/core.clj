(ns utopia-engine.core
  (:require [clojure.spec.alpha :as s]
            [utopia-engine.universe :as universe]))


(defn start []
  {:universe (universe/universe)})



;;;; IDEAS

(def *action-types (atom (make-hierarchy)))


(defn- derive-action-type [parent action-type]
  {:pre [(keyword parent) (keyword action-type)]}
  (swap! *action-types derive action-type parent))


(defn choice-action [action-type] (derive-action-type ::choice action-type))


(choice-action :travel-to-the-wilderness)


;;;; TODO IDEAS FOR ACTIONS

{:type :foo
 :description "Search The Wilderness"
 :args [:player/die-roll :player/die-roll]
 :execute #(prn "some f")
 }


;;;; User

(s/def :player/die-roll (s/and pos-int?
                               #(<= % 6)))


;;;; waste-basket

(s/def :engine/waste-basket (s/and pos-int?
                                   #(<= % 10)))


(defn waste-basket-full? [waste-basket] (>= 10 waste-basket))
