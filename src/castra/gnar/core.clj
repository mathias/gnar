(ns gnar.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [tailrecursion.castra.handler :refer [castra]]))

(def server (atom nil))
(def cookie-secret (or (System/getenv "COOKIE_SECRET")
                       "a 16-bit secret"))

(defn app [port public-path]
  (-> (castra 'gnar.api.gnar)
      (wrap-session {:store (cookie-store {:key cookie-secret})})
      (wrap-file public-path)
      (wrap-file-info)
      (run-jetty {:join? false :port port})))

(defn start-server
  "Start castra server (port 33333)."
  [port public-path]
  (swap! server #(or % (app port public-path))))

(defn run-task [port public-path]
  (.mkdirs (java.io.File. public-path))
  (start-server port public-path)
  (fn [continue]
    (fn [event]
      (continue event))))
