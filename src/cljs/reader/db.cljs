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
  (map->tracked-item (merge args
                            {:id (uuid)})))

(defn make-folder
  [args]
  (map->folder (merge args
                      {:open? false})))



(def hn (make-tracked-item {:title        "Hacker News"
                            :url          "http://news.ycombinator.com"
                            :starred?      false
                            :unread-count 0}))

(def l-t-a
  (make-tracked-item {:title        "Lambda the Ultimate"
                      :url          "http://lambda-the-ultimate.org/"
                      :starred      true
                      :unread-count 10}))

(def programming (make-folder {:title "Programming"}))

(def default-db {:folders       [programming]
                 :items         {(:id hn)    hn
                                 (:id l-t-a) l-t-a}
                 :folders-items {"Programming" [(:id l-t-a)]}})
