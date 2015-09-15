(ns gnar.server.handler
  (:require [bidi.ring :refer [make-handler redirect files]]
            [liberator.core :refer [resource defresource]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [gnar.server.database :as db])
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
               {:links (map encode-created-at (db/all-links))}))

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
