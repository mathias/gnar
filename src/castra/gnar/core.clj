(ns gnar.core
  (:gen-class)
  (:require [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [tailrecursion.castra.handler :refer [castra]]))

(def cookie-secret (or (env :cookie-secret)
                       "a 16-bit secret"))

(defn wrap-dir-index [handler]
  (fn [req]
    (handler
     (update-in req [:uri]
                #(if (= "/" %) "/index.html" %)))))

(def app
  (-> (castra 'gnar.api.gnar)
      (wrap-session {:store (cookie-store {:key cookie-secret})})
      (wrap-resource "public")
      (wrap-file-info)
      (wrap-dir-index)))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 8000))]
    (run-jetty #'app {:port port :join? false})))
