(ns gnar.server.database
  (:require  [clojure.java.jdbc :as jdbc]
             [reloaded.repl :refer [system]]))

(defmacro with-db
  "Get a fresh db reference in each query"
  [db-var & body]
  `(let [~db-var (:db-spec (:pg system))]
     ~@body))

(defn all-links
  "Get all the links in the db, sorted by most recent first"
  []
  (with-db db
    (jdbc/query db
                ["SELECT l.title, l.url, l.domain, l.user_id, l.created_at, users.username
                FROM links as l
                LEFT JOIN users ON l.user_id = users.id
                ORDER BY created_at DESC"])))
