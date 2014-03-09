(ns gnar.api.gnar
  (:refer-clojure :exclude [defn])
  (:use [gnar.http.rules :exclude [assert]])
  (:require [tailrecursion.castra :refer [defn ex error *session*]]
            [hearst.url-cleanup :refer [normalize-url]]))

(def users {"mathiasx" {:pass "password"}
            "devin" {:pass "password"}
            "josh" {:pass "password"}})

(def links [{:url "http://www.spritzinc.com/"
             :title "Spritz (read things fast/comfortably"
             :user "josh"
             :domain "sprintzinc.com"
             :created-at "4 days ago"}
            {:url "http://www.youtube.com/watch?feature=player_detailpage&v=2rKEveL55TY"
             :title "The Micro-Service Architecture"
             :user "devin"
             :domain "youtube.com"
             :created-at "1 day ago"}
            {:url "http://tech.ftbpro.com/post/78195641092/ruby-2-1-our-experience"
             :title "Ruby 2.1 - Our Experience (ed note: big improvements, some incompat issues)"
             :domain "thbpro.com"
             :user "mathiasx"
             :created-at "3 hours ago"}])

(def initial-db-value
  {:users users
   :links links})

(def db (atom initial-db-value))

(defn current-user []
  (when (logged-in?)
    (get @*session* :user)))

(defn new-link [{:keys [title url]}]
  {:title title
   :url (normalize-url url)
   :domain ""
   :user (current-user)
   :created-at (java.util.Date.)})

(defn add-link [db-val details]
  (update-in db-val [:links] conj (new-link details)))

(defn get-state [& [user]]
  (let [db-val @db
        current-user (current-user)]
    (if (logged-in?)
      {:current-user current-user
       :links (rseq (:links db-val))}
      {:links (rseq (:links db-val))})))

(defn register [user password password-confirmation]
  {:rpc/pre [(register! db user password password-confirmation)]}
  (get-state user))

(defn login [user pass]
  {:rpc/pre [(login! db user pass)]}
  (get-state user))

(defn logout []
  {:rpc/pre [(logout!)]}
  (get-state))

(defn submit-link [details]
  {:rpc/pre [(logged-in?)]}
  (swap! db add-link details)
  (get-state))
