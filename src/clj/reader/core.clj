(ns reader.core)


; /users -- get my user account info including folders, tracked sites

; /proxy -- proxy get rss feed
; ?url=http://www.test.atom

; /

(ns ReverseLonger)

(defn reverseLonger
  [a b]
  (let [[shorter longer] (sort-by #(count %) [a b])]
    (str shorter (reverse longer) shorter)))