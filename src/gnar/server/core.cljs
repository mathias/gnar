(ns gnar.server.core
  (:require [cljs.nodejs :as node]))

(node/enable-util-print!)

;; ENV vars
(def session-secret (.-SESSION_SECRET (.-env js/process)))
(def database-url (.-DATABASE_URL (.-env js/process)))

;; Node dependencies
(def express (node/require "express"))
(def express-session (node/require "express-session"))
(def RedisSessionStore ((node/require "connect-redis") express-session))
(def passport (node/require "passport"))
(def LocalStrategy (.-Strategy (node/require "passport-local")))
(def knex (node/require "knex"))
(def pg (apply knex #js {:client "pg"
                         :connection database-url}))

(def session-config #js {:store (RedisSessionStore.)
                         :secret session-secret})

(defn index [req res]
  (.send res "Hello gnar"))

;; Authentication

(.serializeUser passport
                (fn [user done] (done nil user)))

(.deserializeUser passport
                (fn [user done] (done nil user)))

(.use passport (LocalStrategy. (fn [username password done]
                                 (.nextTick js/process
                                            (fn [] ;; auth check
                                              )))))
(defn login-action [req res]
  (.authenticate passport
                 "local"
                 #js {:successRedirect "/"
                      :failureRedirect "/login"
                      :failureFlash true}))

(defn serve-hoplon [view-file]
  (fn [req res] (.sendfile res view-file)))

(defn -main []
  (let [port 3000
        app (express)]
    (.use app (express-session session-config))
    (.get app "/" index)
    (.get app "/login" (serve-hoplon "target/login.html"))
    (.post app "/login" login-action)
    (.use app (.static express "target"))
    (.use app (.static express "resources/public"))
    (.use app (.initialize passport))
    (.use app (.session passport))
    (.listen app port #(println (str "Server started on http://localhost:" port)))))

(set! *main-cli-fn* -main)
