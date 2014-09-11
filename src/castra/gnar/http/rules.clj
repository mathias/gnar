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
  (assert (empty? (find-user-by-username username)) "Username not available.")
  (assert (empty? (find-user-by-email email)) "Email address already has been registered. Did you forget your password?")
  (let [user (create-user-record username email password)]
    (do-login! (:id user))))

(defn login! [username password]
  (let [user (find-user-by-username username)]
    (assert (= (creds/bcrypt-verify password (:encrypted_password user))) "Bad username/password.")
    (do-login! (:id user))))
