(ns gnar.server.core
  (:require [cljs.nodejs :as node]))

(node/enable-util-print!)

(def express (node/require "express"))
(def express-session (node/require "express-session"))
(def Redis-Session-Store ((node/require "connect-redis") express-session))

;; ENV vars for session
(def session-secret (.-SESSION_SECRET (.-env js/process)))
(def session-config #js {:store (Redis-Session-Store.)
                         :secret session-secret})

(defn index [req res]
  (.send res "Hello gnar"))

(defn -main []
  (let [port 3000
        app (express)]
    (.get app "/" index)
    (.use app (.static express "resources/public"))
    (.use app (express-session session-config))
    (.listen app port #(println (str "Server started on http://localhost:" port)))))

(set! *main-cli-fn* -main)
