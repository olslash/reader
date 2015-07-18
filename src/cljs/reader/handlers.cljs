(ns reader.handlers
    (:require [re-frame.core :as r]
              [reader.db :as db]))

(r/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(r/register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(r/register-handler
  :add-item
  (fn [db [_ item]]
    (update db :items conj (db/make-tracked-item item))))
