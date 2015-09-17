(ns gnar.server.database
  (:require  [clojure.java.jdbc :as jdbc]
             [korma.core :refer :all]
             [korma.db :refer :all]
             [environ.core :refer [env]]
             [reloaded.repl :refer [system]]
             [clojure.string :as string]))

(defn db-spec
  ([]
   (db-spec (:db-spec (:db system))))
  ([db-str]
   (let [db-uri (java.net.URI. db-str)
         database (last (string/split db-str #"\/"))
         user-and-pass (or (.getUserInfo db-uri) "")
         [user pass] (string/split user-and-pass #":")
         base {:db database
               :host (.getHost db-uri)
               :port (.getPort db-uri)}]
     (if (and user pass)
       (-> base
           (assoc :user user)
           (assoc :password pass))
       base))))

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
          (order :created_at :DESC)))

(defn find-link-by-id [id]
  (select links
          (where {:id id})
          (limit 1)))

(defn create-link [data]
  (let [link-with-created-at (assoc data :created_at (sqlfn now))]
    (insert links
            (values link-with-created-at))))
