(ns gnar.server.database
  (:require  [clojure.java.jdbc :as jdbc]
             [korma.core :refer :all]
             [korma.db :refer :all]
             [environ.core :refer [env]]
             [reloaded.repl :refer [system]]
             [clojure.string :as string]))

(println "env db: " (env :database-url))
(println "system: " (:db-spec (:db system)))

(defn db-spec []
  (let [db-str (:db-spec (:db system))
        db-uri (java.net.URI. db-str)
        database (last (string/split db-str #"\/"))
        user-and-pass (or (.getUserInfo db-uri) "")
        [user pass] (string/split user-and-pass #":")
        base {:db database
              :host (.getHost db-uri)
              :port (.getPort db-uri)}]
    (if (and user pass)
      (-> base
          (assoc :user user)
          (assoc :pass pass))
      base)))

;;(defdb db (postgres (db-spec)))
(defdb db (postgres {:host "localhost"
                     :port "5432"
                     :db "gnar_test"}))
;; Korma entities
(defentity users
  (entity-fields :username)
  (prepare (fn [{email :email :as v}]
             (if email
               (assoc v :email (string/lower-case email))
               v))))

(defentity links
  (entity-fields :id :url)
  (belongs-to users))

(defn all-links
  "Get all the links in the db, sorted by most recent first"
  []
  (select links
          (join users (= :users.id :user_id))
          (order :created_at :DESC))
  ;; (jdbc/query db
  ;;             ["SELECT l.title, l.url, l.domain, l.user_id, l.created_at, users.username
  ;;               FROM links as l
  ;;               LEFT JOIN users ON l.user_id = users.id
  ;;               ORDER BY created_at DESC"])
  )

(defn find-link-by-id [id]
  ;; (jdbc/query db
  ;;             ["SELECT l.title, l.url, l.domain, l.user_id, l.created_at, users.username
  ;;               FROM links as l
  ;;               WHERE l.id = ?
  ;;               LEFT JOIN users on l.user_id = users.id
  ;;               LIMIT 1"]
  ;;             [id])
  (select links
          (where {:id id})
          (limit 1)))

(defn create-link [data]
  ;; (jdbc/insert! db :links data)
  (let [link-with-created-at (assoc data :created_at (sqlfn now))]
    (insert links
            (values link-with-created-at))))
