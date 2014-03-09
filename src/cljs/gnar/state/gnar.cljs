(ns gnar.state.gnar
  (:require-macros
    [tailrecursion.javelin :refer [defc defc= cell=]])
  (:require
    [clojure.set           :as cs]
    [clojure.string        :as s]
    [tailrecursion.javelin :as j :refer [cell]]
    [tailrecursion.castra  :as c :refer [mkremote]]))


(set! cljs.core/*print-fn* #(.log js/console %))

(defc state {})
(defc error nil)
(defc loading [])

(defc= links (:links state))

(def get-state
  (mkremote 'gnar.api.gnar/get-state state error (cell nil)))

(def submit-link!
  (mkremote 'gnar.api.gnar/submit-link state error loading) )

(defn init []
  (get-state)
  ;; Check every 5 seconds for new data
  (js/setInterval get-state 5000))
