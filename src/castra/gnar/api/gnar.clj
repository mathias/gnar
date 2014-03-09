(ns gnar.api.gnar
  (:refer-clojure :exclude [defn])
  (:use [gnar.http.rules :exclude [assert]])
  (:require [tailrecursion.castra :refer [defn ex error *session*]]))

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

(defn get-state [& [user]]
  (let [db-val @db]
    (if (logged-in?)
      {:current-user user
       :links (rseq (:links db-val))}
      {:links (rseq (:links db-val))})))

(defn new-link [{:keys [title url username]}]
  {:title title
   :url url
   :domain ""
   :user username
   :created-at (java.util.Date.)})

(defn add-link [db-val details]
  (update-in db-val [:links] conj (new-link details)))

(defn submit-link [details]
  (swap! db add-link details)
  (get-state))
