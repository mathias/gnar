(ns gnar.database
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as sql]
            [honeysql.helpers :refer :all]
            [cemerick.friend.credentials :as creds]))

(def db (or (System/getenv "DATABASE_URL")
            {:subprotocol "postgresql"
             :subname "//127.0.0.1:5432/gnar_development"}))

(defn find-user-by-username [username]
  (first
   (j/query db (-> (select :*)
                   (from :users)
                   (where [:= :username username])
                   (limit 1)
                   (sql/format)))))
(defn find-user-by-email [email]
  (first
   (j/query db (-> (select :*)
                   (from :users)
                   (where [:= :email email])
                   (limit 1)
                   (sql/format)))))

(defn find-user-by-id [user_id]
  (first (j/query db (-> (select :*)
                         (from :users)
                         (where [:= :id user_id])
                         (limit 1)
                         (sql/format)))))

(defn add-link! [link-attrs]
  (j/insert! db :links link-attrs))

(defn now-timestamp []
  (java.sql.Timestamp. (.getTime (java.util.Date.))))

(defn create-user-record [username email password]
  (j/insert! db :users {:username username
                        :email email
                        :encrypted_password (creds/hash-bcrypt password)
                        :referred_by_user_id 0
                        :created_at (now-timestamp)}))

(defn links-newest-first []
  (j/query db
           (-> (select :l.title :l.url :l.domain :l.user_id :l.created_at :users.username)
               (from [:links :l])
               (order-by [:created_at :desc])
               (join :users [:= :l.user_id :users.id])
               (limit 100)
               (sql/format))))
