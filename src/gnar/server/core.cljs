(ns gnar.server.core
  (:require [cljs.nodejs :as node]))

(node/enable-util-print!)

(def express (node/require "express"))

(defn index [req res]
  (.send res "hello world"))

(defn -main []
  (let [app (express)]
    (.get app "/" index)
    (.use app (.static express "resources/public"))
    (.listen app 3000 (fn []
                        (println "Server started on port 3000")))))


(set! *main-cli-fn* -main)
