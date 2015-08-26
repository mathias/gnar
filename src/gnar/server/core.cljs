(ns gnar.server.core
  (:require [cljs.nodejs :as node]))

(node/enable-util-print!)

;; ENV vars
(def session-secret (.-SESSION_SECRET (.-env js/process)))
(def database-url (or (.-DATABASE_URL (.-env js/process))
                      "postgresql://127.0.0.1:5432/gnar_development"))
(def redis-url (.-REDIS_URL (.-env js/process)))

;; Node dependencies
(def express (node/require "express"))
(def Redis (node/require "ioredis"))
(def redis-client (Redis. redis-url))

(def express-session (node/require "express-session"))
(def RedisSessionStore ((node/require "connect-redis") express-session))
(def session-store (RedisSessionStore. #js {:client redis-client}))
(def session-config #js {:store session-store
                         :secret session-secret})
(def passport (node/require "passport"))
(def Strategy (.-Strategy (node/require "passport-local")))
(def Promise (node/require "bluebird"))
(def bcrypt (.promisifyAll Promise (node/require "bcrypt")))
(def Sequelize (node/require "sequelize"))
(def db (Sequelize. database-url))

;; Models

(def User (.define db
                   "user"
                   #js {:id {:type (.UUID Sequelize)
                             :defaultValue (.UUIDV4 Sequelize)
                             :primaryKey true}
                        :username {:type (.STRING Sequelize)}
                        :email {:type (.STRING Sequelize)}
                        :password {:type (.STRING Sequelize)}}))

(.sync User #js {:force true})

;; Actions

(defn index [req res]
  (.send res "Hello gnar"))

(defn serve-hoplon [view-file]
  (fn [req res] (.sendfile res view-file)))

;; Authentication
(.use passport "local"
      (Strategy. (fn [username password callback]
                   (let [user {}]
                     (callback nil user)))))


(.serializeUser passport
                (fn [user callback]
                  (callback nil (get user :id))))

(.deserializeUser passport
                  (fn [id callback]
                    (callback {:id id})))

(defn -main []
  (let [port 3000
        app (express)]
    (.use app (express-session session-config))
    (.get app "/" index)
    (.get app "/login" (serve-hoplon "target/login.html"))
    (.get app "/users" (fn [req res] (.findAll User)))
    (.post app "/login" (.authenticate passport
                                       "local"
                                       #js {:successRedirect "/"
                                            :failureRedirect "/login"}))
    (.get app "/logout" (fn [req res]
                          (.logout req)
                          (.redirect res "/")))
    (.use app (.static express "target"))
    (.use app (.static express "resources/public"))

    (.use app (.initialize passport))
    (.use app (.session passport))

    (.listen app port #(println (str "Server started on http://localhost:" port)))))

(set! *main-cli-fn* -main)
