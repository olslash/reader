(ns reader.db
  (:require [re-frame.core :as re-frame]
            [reagent.core :as r]))

(defrecord tracked-item [title url starred? unread-count])
(defrecord folder [title open?])

(def uuid-counter (atom 0))

(defn uuid []
  (swap! uuid-counter inc)
  @uuid-counter)


(defn make-tracked-item
  [args]
  (map->tracked-item (merge {:id           (uuid)
                             :unread-count 0}
                            args)))

(defn make-folder
  [args]
  (map->folder (merge {:open? false}
                      args)))



(def hn
  (make-tracked-item {:title        "Hacker News"
                      :url          "http://news.ycombinator.com"
                      :starred?     false}))

(def l-t-a
  (make-tracked-item {:title        "Lambda the Ultimate"
                      :url          "http://lambda-the-ultimate.org/"
                      :starred      true
                      :unread-count 10}))

(def programming (make-folder {:title "Programming"}))
(def something (make-folder {:title "Something"}))

(def default-db {:folders       [programming something]
                 :items         (sorted-map (:id hn) hn
                                            (:id l-t-a) l-t-a)
                 :folders-items {"Programming" [(:id l-t-a)]
                                 "Something"   []}})
