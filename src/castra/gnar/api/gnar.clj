(ns gnar.api.gnar
  (:refer-clojure :exclude [defn])
  (:use [gnar.http.rules :exclude [assert]])
  (:require [tailrecursion.castra :refer [defn ex error *session*]]
            [hearst.url-cleanup :refer [normalize-url]]
            [gnar.database :refer :all]))

(defn current-user-id []
  (when (logged-in?)
    (get @*session* :user_id)))

(defn assume-http-if-not [url]
  (if-not (re-find #"^(\w+://){1}" url) ;; no protocol found
    (str "http://" url)
    url))

(defn new-link [{:keys [title url]}]
  (let [parsed-url (normalize-url (assume-http-if-not url))
        domain (:host (cemerick.url/url parsed-url))]
    {:title title
     :url parsed-url
     :domain domain
     :user_id (current-user-id)
     :created_at (now-timestamp)}))

(defn get-state []
  (let [current-user-id (current-user-id)
        links {:links (links-newest-first)}]
    (if (logged-in?)
      (merge links {:current-user-id current-user-id})
      links)))

(defn register [email password password-confirmation]
  {:rpc/pre [(register! email password password-confirmation)]}
  (get-state))

(defn login [email password]
  {:rpc/pre [(login! email password)]}
  (get-state))

(defn logout []
  {:rpc/pre [(logout!)]}
  (get-state))

(defn submit-link [details]
  {:rpc/pre [(logged-in?)]}
  (add-link! (new-link details))
  (get-state))
