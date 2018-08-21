(ns utopia-engine.universe
  (:require #?(:clj [clojure.spec.alpha :as s]
               :cljs [cljs.spec.alpha :as s])))


;;;; COMPONENTS

(def lead "Lead")
(def quartz "Quartz")
(def silica "Silica")
(def gum "Gum")
(def wax "Wax")
(def silver "Silver")

(def components #{lead quartz silica gum wax silver})

(defn component? [c] (contains? components c))

(s/def :world/component component?)

(s/def :world/components (s/coll-of :world/component :kind set? :count 6))


;;;; TREASURES

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

(s/def :world/treasures (s/coll-of :world/treasure :kind set? :count 6))


;;;; MONSTERS

(def ice-bear "Ice Bear")
(def roving-bandits "Roving Bandits")
(def blood-wolves "Blood Wolves")
(def horse-eater-hawk "Horse Eater Hawk")
(def giant-of-the-peaks "Giant of the Peaks")
(def rogue-thief "Rogue Thief")
(def blanket-of-crows "Blanket of Crows")
(def hornback-bison "Hornback Bison")
(def grassy-back-troll "Grassy Back Troll")
(def thunder-king "Thunder King")
(def feisty-gremlin "Feisty Gremlin")
(def glasswing-drake "Glasswing Drake")
(def reaching-claws "Reaching Claws")
(def terrible-wurm "Terrible Wurm")
(def leviathan-serpent "Leviathan Serpent")
(def gemscale-boa "Gemscale Boa")
(def ancient-alligator "Ancient Alligator")
(def land-shark "Land Shark")
(def abyssal-leech "Abyssal Leech")
(def dweller-in-the-tides "Dweller in the Tides")
(def grave-robbers "Grave Robbers")
(def ghost-lights "Ghost Lights")
(def vengeful-shade "Vengeful Shade")
(def nightmare-crab "Nightmare Crab")
(def the-unnamed "The Unnamed")
(def minor-imp "Minor Imp")
(def renegade-warlock "Renegade Warlock")
(def giant-flame-lizzard "Giant Flame Lizzard")
(def spark-elemental "Spark Elemental")
(def volcano-spirit "Volcano Spirit")

(def monsters #{ice-bear roving-bandits blood-wolves horse-eater-hawk giant-of-the-peaks
                rogue-thief blanket-of-crows hornback-bison grassy-back-troll thunder-king
                feisty-gremlin glasswing-drake reaching-claws terrible-wurm leviathan-serpent
                gemscale-boa ancient-alligator land-shark abyssal-leech dweller-in-the-tides
                grave-robbers ghost-lights vengeful-shade nightmare-crab the-unnamed
                minor-imp renegade-warlock giant-flame-lizzard spark-elemental volcano-spirit})

(def monster-types #{:normal :spirit})


(defn monster? [m] (contains? monsters m))


(defn attack-range
  ([x] (attack-range x x))
  ([min-hit max-hit] (set (range min-hit (inc max-hit)))))


(s/def :world/monster monster?)
(s/def :world/monsters (s/coll-of :world/monster :kind set? :count 30))
(s/def :monster/type #(contains? monster-types %))
(s/def :monster/attack-range (s/coll-of pos-int? :distinct true))
(s/def :monster/hit-range (s/coll-of pos-int? :distinct true))
(s/def :monster/level (s/and pos-int?
                             #(<= % 5)))


;;;; CONSTRUCTS

(def crystal-battery "Crystal Battery")
(def golden-chassis "Golden Chassis")
(def scrying-lens "Scrying Lens")
(def hermetic-mirror "Hermetic Mirror")
(def seal-of-balance "Seal of Balance")
(def void-gate "Void Gate")

(def constructs #{crystal-battery golden-chassis scrying-lens hermetic-mirror seal-of-balance void-gate})

(defn construct? [c] (contains? constructs c))

(s/def :engine/construct construct?)
(s/def :engine/constructs (s/coll-of :engine/construct :kind set? :count 6))


;;;; WILDERNESS

(def halebeard-peak "Halebeard Peak")
(def the-great-wilds "The Great Wilds")
(def root-strangled-marshes "Root Strangled Marshes")
(def glassrock-canyon "Glassrock Canyon")
(def ruined-city-of-the-ancients "Ruined City of the Ancients")
(def the-fiery-maw "The Fiery Maw")
(def regions #{halebeard-peak the-great-wilds root-strangled-marshes glassrock-canyon
               ruined-city-of-the-ancients the-fiery-maw })

(defn region? [r] (contains? regions r))

(defn- encounter-for-every-level? [coll]
  (->> coll
       (map :monster/level)
       set
       (= #{1 2 3 4 5})))

(s/def :world/region region?)
(s/def :world/regions (s/coll-of :world/region :kind set? :count 6))
(s/def :wilderness/day #(contains? #{:easy-day :hard-day} %))
(s/def :wilderness/days (s/coll-of :wilderness/day :count 6))
(s/def :wilderness/encounter (s/keys :req [:world/monster
                                           :monster/level
                                           :monster/type
                                           :monster/attack-range
                                           :monster/hit-range]))
(s/def :winderness/encounters (s/and (s/coll-of :wilderness/encounter :count 5 :distinct true)
                                     encounter-for-every-level?))
(s/def :wilderness/region (s/keys :req [:world/region
                                        :wilderness/days
                                        :engine/construct
                                        :world/component
                                        :world/treasure
                                        :wilderness/encounters]))


(defn- wilderness-days [& args] (map #(if (= -1 %) :hard-day :easy-day) args))


(defn- wilderness-encounter
  [monster level monster-type
   attack-range-min attack-range-max hit-range-min hit-range-max]
  {:world/monster monster
   :monster/level level
   :monster/type monster-type
   :monster/attack-range (attack-range attack-range-min attack-range-max)
   :monster/hit-range (attack-range hit-range-min hit-range-max)})


(defn- wilderness-encounters [& args] (map #(apply wilderness-encounter %) args))


(defn- wilderness-region
  [name construct component treasure days encounters]
  {:world/region name
   :wilderness/days days
   :engine/construct construct
   :world/component component
   :world/treasure treasure
   :wilderness/encounters encounters})

(s/def :world/wilderness (s/map-of string? :wilderness/region :count 6))


(def the-wilderness (->> [[halebeard-peak seal-of-balance silver ice-plate
                           (wilderness-days -1 -1 0 -1 0 0)
                           (wilderness-encounters [ice-bear 1 :normal 1 1 5 6]
                                                  [roving-bandits 2 :normal 1 1 6 6]
                                                  [blood-wolves 3 :normal 1 2 6 6]
                                                  [horse-eater-hawk 4 :normal 1 3 6 6]
                                                  [giant-of-the-peaks 5 :normal 1 4 6 6])]
                          [the-great-wilds hermetic-mirror quartz bracelet-of-ios
                           (wilderness-days -1 0 0 -1 0 0)
                           (wilderness-encounters [rogue-thief 1 :normal 1 2 5 6]
                                                  [blanket-of-crows 2 :normal 1 1 6 6]
                                                  [hornback-bison 3 :normal 1 1 6 6]
                                                  [grassy-back-troll 4 :normal 1 3 5 6]
                                                  [thunder-king 5 :spirit 1 4 6 6])]
                          [root-strangled-marshes void-gate gum shimmering-moonlace
                           (wilderness-days -1 0 -1 0 -1 0)
                           (wilderness-encounters [feisty-gremlin 1 :normal 1 1 5 6]
                                                  [glasswing-drake 2 :normal 1 1 6 6]
                                                  [reaching-claws 3 :spirit 1 2 6 6]
                                                  [terrible-wurm 4 :normal 1 3 6 6]
                                                  [leviathan-serpent 5 :normal 1 4 6 6])]
                          [glassrock-canyon golden-chassis silica scale-of-the-infinity-wurm
                           (wilderness-days -1 0 -1 0 -1 0)
                           (wilderness-encounters [gemscale-boa 1 :normal 1 1 5 6]
                                                  [ancient-alligator 2 :normal 1 2 6 6]
                                                  [land-shark 3 :normal 1 2 6 6]
                                                  [abyssal-leech 4 :normal 1 3 6 6]
                                                  [dweller-in-the-tides 5 :normal 1 4 6 6])]
                          [ruined-city-of-the-ancients scrying-lens wax the-ancient-record
                           (wilderness-days -1 0 0 -1 0 0)
                           (wilderness-encounters [grave-robbers 1 :normal 1 1 5 6]
                                                  [ghost-lights 2 :spirit 1 1 6 6]
                                                  [vengeful-shade 3 :spirit 1 2 6 6]
                                                  [nightmare-crab 4 :normal 1 3 6 6]
                                                  [the-unnamed 5 :spirit 1 4 6 6])]
                          [the-fiery-maw crystal-battery lead the-molten-shard
                           (wilderness-days -1 -1 0 -1 0 0)
                           (wilderness-encounters [minor-imp 1 :normal 1 1 5 6]
                                                  [renegade-warlock 2 :normal 1 2 5 6]
                                                  [giant-flame-lizzard 3 :normal 1 3 5 6]
                                                  [spark-elemental 4 :spirit 1 3 6 6]
                                                  [volcano-spirit 5 :spirit 1 4 6 6])]]
                         (map #(apply wilderness-region %))))


;;;; LINKS

(s/def :link/constructs (s/coll-of :engine/construct :count 2 :distinct true))


(s/def :engine/link (s/keys req [:world/component
                                 :link/constructs]))
(s/def :engine/links (s/coll-of :engine/link :count 6))


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


;;;; TOOLS

(def dowsing-rod "Dowsing Rod")
(def paralisys-wand "Paralisys Wand")
(def focus-charm "Focus Charm")

(def tools #{dowsing-rod paralisys-wand focus-charm})

(defn tool? [t] (contains? tools t))

(s/def :world/tool tool?)
(s/def :world/tools (s/coll-of :world/tool :kind set? :count 3))


;;;; UNIVERSE

(s/def ::universe (s/keys :req-un [:world/wilderness
                                   :world/regions
                                   :world/treasures
                                   :world/monsters
                                   :world/components
                                   :engine/constructs
                                   :engine/links
                                   :world/tools]))

(def universe
  (let [universe-data
        {:wilderness (->> the-wilderness
                          (map (juxt :world/region identity))
                          (into {}))
         :regions regions
         :treasures treasures
         :monsters monsters
         :components components
         :constructs constructs
         :tools tools
         :links links}
        valid? (s/valid? ::universe universe-data)]
    (if valid?
      universe-data
      (throw (ex-info "Universe Failed Spec" (s/explain-data ::universe universe-data))))))
