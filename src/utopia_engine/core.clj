(ns utopia-engine.core
  (:require [clojure.spec.alpha :as s]))

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


;;;; Components

(def lead "Lead")
(def quartz "Quartz")
(def silica "Silica")
(def gum "Gum")
(def wax "Wax")
(def silver "Silver")
(def components #{lead quartz silica gum wax silver})

(defn component? [c] (contains? components c))

(s/def :world/component component?)


;;;; Treasures

(def ice-plate "Ice Plate")
(def bracelet-of-ios "Bracelet of Ios")
(def shimmering-moonlace "Shimmering Moonlace")
(def scale-of-the-infinity-wurm "Scale of the Infinity Wurm")
(def the-molten-shard "The Molten Shard")
(def the-ancient-record "The Ancient Record")
(def treasures #{ice-plate bracelet-of-ios shimmering-moonlace scale-of-the-infinity-wurm
                 the-molten-shard the-ancient-record})

(defn treasure? [t] (contains? treasures t))

(s/def :world/treasure treasure?)


;;;; Constructs

(def crystal-battery "Crystal Battery")
(def golden-chassis "Golden Chassis")
(def scrying-lens "Scrying Lens")
(def hermetic-mirror "Hermetic Mirror")
(def seal-of-balance "Seal of Balance")
(def void-gate "Void Gate")
(def constructs #{crystal-battery golden-chassis scrying-lens hermetic-mirror seal-of-balance void-gate})

(defn construct? [c] (contains? constructs c))

(s/def :engine/construct construct?)


;;;; Monsters

;; TODO

;;;; Regions

(def halebeard-peak "Halebeard Peak")
(def the-great-wilds "The Great Wilds")
(def root-strangled-marshes "Root Strangled Marshes")
(def glassrock-canyon "Glassrock Canyon")
(def ruined-city-of-the-ancients " Ruined City of the Ancients")
(def the-fiery-maw "The Fiery Maw")
(def regions #{halebeard-peak the-great-wilds root-strangled-marshes glassrock-canyon
               ruined-city-of-the-ancients the-fiery-maw })

(defn region? [r] (contains? regions r))

(s/def :world/region region?)


(s/def :wilderness/day #(contains? #{:easy-day :hard-day} %))
(s/def :wilderness/days (s/coll-of :wilderness/day :count 6))
(s/def :wilderness/region (s/keys :req [:world/region
                                        :wilderness/days
                                        :engine/construct
                                        :world/component
                                        :world/treasure]))

(defn- wilderness-days [& args] (map #(if (= -1 %) :hard-day :easy-day) args))


(defn wilderness-region [name construct component treasure days]
  {:wilderness/region name
   :wilderness/days days
   :engine/construct construct
   :world/component component
   :world/treasure treasure})


(def the-wilderness (->> [[halebeard-peak seal-of-balance silver ice-plate
                           (wilderness-days -1 -1 0 -1 0 0)]
                          [the-great-wilds hermetic-mirror quartz bracelet-of-ios
                           (wilderness-days -1 0 0 -1 0 0)]
                          [root-strangled-marshes void-gate gum shimmering-moonlace
                           (wilderness-days -1 0 -1 0 -1 0)]
                          [glassrock-canyon golden-chassis silica scale-of-the-infinity-wurm
                           (wilderness-days -1 0 -1 0 -1 0)]
                          [ruined-city-of-the-ancients scrying-lens wax the-ancient-record
                           (wilderness-days -1 0 0 -1 0 0)]
                          [the-fiery-maw crystal-battery lead the-molten-shard
                           (wilderness-days -1 -1 0 -1 0 0)]]
                         (map #(apply region %))))


;;;; Link

(s/def :link/constructs (s/coll-of :engine/construct :count 2 :distinct true))


(s/def :engine/link (s/keys req [:world/component
                                 :link/constructs]))



(defn link [component construct-a construct-b]
  {:world/component component
   :link/constructs [construct-a construct-b]})


(def links (->> [[silver seal-of-balance scrying-lens]
                 [quartz seal-of-balance golden-chassis]
                 [silica seal-of-balance hermetic-mirror]
                 [lead golden-chassis crystal-battery]
                 [gum golden-chassis void-gate]
                 [wax hermetic-mirror void-gate]]
                (map #(apply link %))))

;;;; waste-basket

(s/def :engine/waste-basket (s/and pos-int?
                                   #(<= % 10)))


(defn waste-basket-full? [waste-basket] (>= 10 waste-basket))
