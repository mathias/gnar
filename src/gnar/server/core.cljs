(ns gnar.server.core
  (:require [cljs.nodejs :as node]))

(node/enable-util-print!)

(def express (node/require "express"))

(defn say-hello! [req res]
  (.send res "Hello world!"))

(defn -main []
  (let [app (express)]
    (.get app "/" say-hello!)
    (.listen app 3000 (fn []
                        (println "Server started on port 3000")))))


(set! *main-cli-fn* -main)
