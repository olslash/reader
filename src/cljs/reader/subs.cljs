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
