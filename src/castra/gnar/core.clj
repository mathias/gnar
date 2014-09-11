(ns gnar.core
  (:gen-class)
  (:require [environ.core :refer [env]]
            [playnice.core :refer [dassoc dispatch]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [tailrecursion.castra.handler :refer [castra]]))

(def cookie-secret (or (env :cookie_secret)
                       "a 16-bit secret"))

(defn serve-index [req]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (FileInputStream. "resources/public/index.html")})

(def routes (-> nil
                (dassoc "/" serve-index)))

(defn app-handler [request]
  (dispatch routes request))

(def app
  (-> app-handler
      (castra 'gnar.api.gnar)
      (wrap-session {:store (cookie-store {:key cookie-secret})})
      (wrap-resource "public")
      (wrap-file-info)))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 8000))]
    (run-jetty #'app {:port port :join? false})))
