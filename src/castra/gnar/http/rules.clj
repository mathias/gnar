(ns gnar.http.rules
  (:refer-clojure :exclude [assert])
  (:require [tailrecursion.castra :refer [ex auth *request* *session*]]
            [gnar.database :refer [find-user-by-username find-user-by-email create-user-record]]
            [cemerick.friend.credentials :as creds]))

;;; utility ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmacro assert [expr & [msg]]
  `(when-not ~expr (throw (ex auth (or ~msg "Server error.")))))

;;; internal ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn do-login! [user_id]
  (swap! *session* assoc :user_id user_id))

;;; public ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn logout! []
  (swap! *session* assoc :user_id nil))

(defn logged-in? []
  (and (contains? @*session* :user_id)
       (not (nil? (get @*session* :user_id)))))

(defn register! [username email password password-confirmation]
  (assert (= password password-confirmation) "Passwords don't match.")
  (assert (> (count password) 8) "Password must be at least 8 characters long.")
  (assert (empty? (find-user-by-username username)) "Username not available.")
  (assert (empty? (find-user-by-email email)) "Email address has already been registered.")
  (let [user (create-user-record username email password)]
    (do-login! (:id user))))

(def creds-checker (partial creds/bcrypt-credential-fn find-user-by-username))

(defn login! [username password]
  (if-let [user (creds-checker {:username username :password password})]
    user
    (assert false "Bad username/password.")))
