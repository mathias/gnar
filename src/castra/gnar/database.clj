(ns gnar.database
  (:require [clojure.java.jdbc :as j]
            [honeysql.core :as sql]
            [honeysql.helpers :refer :all]
            [cemerick.friend.credentials :as creds]))

;; (def users {"mathiasx" {:pass "password"}
;;             "devin" {:pass "password"}
;;             "josh" {:pass "password"}})

;; (def links [{:url "http://www.spritzinc.com/"
;;              :title "Spritz (read things fast/comfortably"
;;              :user "josh"
;;              :domain "sprintzinc.com"
;;              :created-at "4 days ago"}
;;             {:url "http://www.youtube.com/watch?feature=player_detailpage&v=2rKEveL55TY"
;;              :title "The Micro-Service Architecture"
;;              :user "devin"
;;              :domain "youtube.com"
;;              :created-at "1 day ago"}
;;             {:url "http://tech.ftbpro.com/post/78195641092/ruby-2-1-our-experience"
;;              :title "Ruby 2.1 - Our Experience (ed note: big improvements, some incompat issues)"
;;              :domain "thbpro.com"
;;              :user "mathiasx"
;;              :created-at "3 hours ago"}])

(def db {:subprotocol "postgresql"
         :subname "//127.0.0.1:5432/gnar_development"})

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

(defn create-user-record [email password]
  (j/insert! db :users {:email email
                        :encrypted_password (creds/hash-bcrypt password)
                        :referred_by_user_id 0
                        :created_at (now-timestamp)}))

(defn links-newest-first []
  (j/query db
           (-> (select :l.title :l.url :l.domain :l.user_id :l.created_at :users.email)
               (from [:links :l])
               (order-by [:created_at :desc])
               (join :users [:= :l.user_id :users.id])
               (limit 100)
               (sql/format))))
