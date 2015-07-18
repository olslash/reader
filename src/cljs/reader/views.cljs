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
     [:span.url (:url item)]
     [:span.unread-count (:unread-count item)]]))

(defn folder []
  "a container for items"
  (let [this (r/current-component)]
    (fn [f]
      [:li.folder
       [:span.icon.folder-open]
       [:div.folder-header (str "FOLDER" (:title f))]
       (into [:ul.folder-contents] (r/children this))])))

(defn in-any-folder? [folders-items item-id]
  "is the given item in any folder?"
  #_(print (:id (first (first (vals folders-items)))))
  (let [folder-content-vecs (vals folders-items)]
    (some (fn [folder]
            (some (fn [item] (= (:id item) item-id)) folder))
          folder-content-vecs)))


; tree view should show all the folders, all their contents,
; and any items that aren't in a folder
(defn tree-view [header]
  (let [items         (re-frame/subscribe [:items])
        folders       (re-frame/subscribe [:folders])
        folders-items (re-frame/subscribe [:folders-items])]

    (fn []
      (let [f-i @folders-items]                             ; have to deref here?
        (print @folders)
        [:div
         [:div header]
         ; items not in a folder are shown in the main list
         (for [i (vals @items) :when (not (in-any-folder? f-i (:id i)))]
           ^{:key (:url i)} [item i])

         (for [f @folders]
           ^{:key (:title f)} [folder f
                               (let [current-folder (get f-i (:title f))]
                                 (if (empty? current-folder)
                                   [:li.empty-folder "empty folder"]

                                   (for [i current-folder]
                                     ^{:key (:url i)} [item i])))])]))))


(defn handle-input-change [e state key]
  (swap! state assoc key (.. e -target -value)))


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

