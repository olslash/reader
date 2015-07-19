(ns reader.views
    (:require [re-frame.core :as re-frame]
              [reagent.core :as r]))

; helpers ----------------------------------------------------------

(defn in-any-folder? [folders-items item-id]
  "is the given item in any folder?"
  (let [folder-content-vecs (vals folders-items)]
    (some (fn [folder]
            (some (fn [item] (= (:id item) item-id)) folder))
          folder-content-vecs)))

(defn handle-input-change [e state key]
  (swap! state assoc key (.. e -target -value)))


; views ------------------------------------------------------------

(defn item []
  "a folder item"

  (fn [item]
    [:li.tracked-item "ITEM"
     [:span.icon.star
      {:class-name (when-not (:starred item) "hidden")} "star"]
     [:span.title (:title item)]
     [:span.url (:url item)]
     [:span.unread-count (:unread-count item)]]))

(defn folder []
  "a container for items"

  (fn [f {:keys [items]}]
    [:li.folder
     [:span.icon.folder-open "FOLDER"]
     [:div.folder-header (:title f)]
     [:ul.folder-contents (if (empty? items)
                            [:li.empty-folder "empty folder"]
                            (for [i items]
                              ^{:key (:url i)} [item i]))]]))

(defn tree-view [header]
  "the tree of folders and their items"

  (let [items         (re-frame/subscribe [:items])
        folders       (re-frame/subscribe [:folders])
        folders-items (re-frame/subscribe [:folders-items])]

    (fn []
      (let [f-i @folders-items]                             ; have to deref here?
        [:div
         [:div header]
         ; items not in a folder are shown in the main list
         (for [i (vals @items) :when (not (in-any-folder? f-i (:id i)))]
           ^{:key (:url i)} [item i])

         (for [f @folders]
           ^{:key (:title f)} [folder f {:items (get f-i (:title f))}])]))))

(defn add-item-button []
  (let [state (r/atom {:active false
                       :url ""})]
    (fn []
      (if (:active @state)
        [:input.add-item
         {:on-change #(handle-input-change %1 state :url)
         :on-key-down #(when (= (.. %1 -keyCode) 13)
                        (do
                          (swap! state assoc :active false)
                          (re-frame/dispatch [:add-item {:url (:url @state)}])
                          (swap! state assoc :url "")))     ; clear input
         :value (:url @state)}]

        [:a.add-item {:on-click  #(swap! state assoc :active true)}
         "Add an item"]))))

(defn sidebar-view []
  [:div
   [tree-view "Tree view"]
   [add-item-button]])





; <div class="folder-list"> ; folder list
;   <div class="folder"> ; folder component
;     <span class="icon folder-open"></span>
;     <div class="folder-header">Programming</div>

;     <ul class="folder-contents"> ; item component
;       <li class="folder-item">
;         <span class="icon star hidden"></span>
;         <span class="title">Hacker News</span>
;         <span class="unread-count">20</span>
;       </li>
;     </ul>

;   </div>
; </div>

;; --------------------
(defmulti panels identity)
(defmethod panels :home-panel [] [sidebar-view])
;(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      (panels @active-panel))))

