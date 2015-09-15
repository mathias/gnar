(ns gnar.server.database
  (:require  [clojure.java.jdbc :as jdbc]
             [environ.core :refer [env]]
             [reloaded.repl :refer [system]]))

(def db-spec (:connection (:pg system)))

(defn all-links
  "Get all the links in the db, sorted by most recent first"
  []
  (jdbc/query db-spec
              ["SELECT l.title, l.url, l.domain, l.user_id, l.created_at, users.username
                FROM links as l
                LEFT JOIN users ON l.user_id = users.id
                ORDER BY created_at DESC"]))

(defn find-link-by-id [])

(defn create-link [data]
  (jdbc/insert! db-spec :links data))
