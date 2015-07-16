(ns reader.views
    (:require [re-frame.core :as re-frame]
              [reagent.core :as r]))

(defn item []
  "a folder item"
  (fn [item]
    [:li.tracked-item "ITEM"
     [:span.icon.star
      {:class-name (when-not (:starred item) "hidden")} "star"]
     [:span.name (:title item)]
     [:span.unread-count (:unread-count item)]]))

(defn folder []
  "a container for items"
  (let []
    (fn [f]
      [:li.folder
       [:span.icon.folder-open]
       [:div.folder-header (str "FOLDER" (:title f))]])))

; tree view should show all the folders, all their contents,
; and any items that aren't in a folder
(defn tree-view []
  (let [items   (re-frame/subscribe [:items])
        folders (re-frame/subscribe [:folders])]

    (fn [header]
      [:div
       [:div header]
       (for [i @items :when (empty? (:in-folders i))]
         ^{:key (:url i)} [item i])                         ; items not in a folder

       (for [f @folders]
         ^{:key (:title f)} [folder f])
       ])))


(defn handle-input-change [e state key]
  (swap! state assoc key (.. e -target -value)))


(defn add-item-button []
  (let [state (r/atom {:active false
                       :url ""})]
    (fn []
      (if (:active @state)
        [:input.add-item
         {:on-change   #(handle-input-change %1 state :url)
          :on-key-down #(when (= (.. %1 -keyCode) 13)
                         (do
                           (swap! state assoc :active false)
                           (re-frame/dispatch [:add-item {:url (:url @state)}])
                           (swap! state assoc :url ""))) ; clear input
          :value       (:url @state)}]

        [:a.add-item {:on-click  #(swap! state assoc :active true)} "Add an item"]))))


(defn sidebar-view []
  [:div
   [tree-view "Tree view"]
   [add-item-button]])










; <TreeView>
;   <Folder name=Bikes>
;     <Item>Red Kite Prayer</Item>
;     <Item>Manual for Speed</Item>
;   </Folder>
;   <Folder name=Programming>
;     <Item favorite=true>Hackernews</Item>
;     <Item unread-count=20>Clojuredocs</Item>
;     <Item unread-count=19>Github Blog</Item>
;   </Folder>
; </TreeView>

; <div class="folder-list"> ; folder list
;   <div class="folder"> ; folder component
;     <span class="icon folder-open"></span>
;     <div class="folder-header">Programming</div>

;     <ul class="folder-contents"> ; item component
;       <li class="folder-item">
;         <span class="icon star hidden"></span>
;         <span class="name">Hacker News</span>
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

