(ns reader.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

;(re-frame/register-sub
; :name
; (fn [db]
;   (reaction (:name @db))))
;
(re-frame/register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))

(re-frame/register-sub
  :items
  (fn [db _]
    (reaction (:items @db))))

(re-frame/register-sub
  :folders
  (fn [db _]
    (reaction (:folders @db))))

(defn id->item [db id]
  (get id (:items db)))

(defn populate [lookup id-list]
  "for every item-id in id-list, replace it with the associated real thing in lookup (or nil)"
  (map #(get lookup %) id-list))

(re-frame.core/register-sub
  :folders-items
  (fn [db _]
    (reaction
      (zipmap (keys (:folders-items @db))
              (map
                (partial populate (:items @db))
                (vals (:folders-items @db)))))))