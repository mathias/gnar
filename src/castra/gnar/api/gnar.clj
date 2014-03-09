(ns gnar.api.gnar
  (:refer-clojure :exclude [defn])
  (:require [tailrecursion.castra :refer    [defn ex error *session*]]))


(def users {"mathiasx" {:pass "password"}
            "devin" {:pass "password"}
            "josh" {:pass "password"}})

(def links [{:href "#"
              :title "Ruby 2.1 - Our Experience (ed note: big improvements, some incompat issues)"
              :domain "thbpro.com"
              :user "mathiasx"
              :created-at "3 hours ago"}
             {:href "#"
              :title "The Micro-Service Architecture"
              :user "devin"
              :domain "youtube.com"
              :created-at "1 day ago"}
             {:href "#"
              :title "Spritz (read things fast/comfortably"
              :user "josh"
              :domain "sprintzinc.com"
              :created-at "4 days ago"}])

(def initial-db-value
  {:users users
   :links links})

(def db (atom initial-db-value))

(defn get-state []
  (let [db-val @db]
    {:users (:users db-val)
     :links (rseq (:links db-val))}))

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
