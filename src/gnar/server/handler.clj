(ns gnar.server.handler
  (:require [playnice.core :as pn]
            [liberator.core :refer [resource defresource]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [file-response]]
            [gnar.server.database :as db]
            [ring.logger :as logger])
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
               {:links (map encode-created-at
                            (db/all-links))}))

(defresource api-link
  :available-media-types ["application/json"]
  :handle-ok (fn [ctx] "{\"link\": {}}"))

(def home-page
  (file-response "index.html" {:root "target"
                               :index-files? true}))

(def routes (-> nil
                (pn/dassoc "/"          home-page)
                (pn/dassoc "/api/links" api-links)))

(defn route-handler
  [req]
  (pn/dispatch routes req))

(def app
  (-> route-handler
      (wrap-defaults site-defaults)
      logger/wrap-with-logger))
