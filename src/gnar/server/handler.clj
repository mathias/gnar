(ns gnar.server.handler
  (:require [bidi.ring :refer [make-handler redirect files]]
            [liberator.core :refer [resource defresource]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [reloaded.repl :refer [system]]
            [clojure.java.jdbc :as jdbc])
  (:import (java.util Date SimpleTimeZone)
           (java.text SimpleDateFormat)))

(def default-date-format "yyyy-MM-dd'T'HH:mm:ss'Z'")

(defn encode-date
  "Encode a date object to the json generator."
  [^Date d]
  (let [sdf (SimpleDateFormat. default-date-format)]
    (.setTimeZone sdf (SimpleTimeZone. 0 "UTC"))
    (str (.format sdf d))))

(defn encode-created-at
  [row]
  (assoc row :created_at (encode-date (:created_at row))))

(defresource api-links
  :available-media-types ["application/json"]
  :handle-ok (fn [ctx]
               (let [db (:db-spec (:pg system))]
                 {:links
                  (map encode-created-at
                       (jdbc/query db
                                   ["SELECT l.title, l.url, l.domain, l.user_id, l.created_at, users.username
                                    FROM links as l
                                    LEFT JOIN users ON l.user_id = users.id
                                    ORDER BY created_at DESC"]))})))

(defresource api-link
  :available-media-types ["application/json"]
  :handle-ok (fn [ctx] "{\"link\": {}}"))

(def routes
  ["/" {"api/"
        {"links" {"" api-links}
         "links/" {"" (redirect "/api/links")
                   [:id] api-link}}
        "" (files {:dir "target/"})}])

(def app
  (-> (make-handler routes)
      (wrap-defaults site-defaults)))
