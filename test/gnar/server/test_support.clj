(ns gnar.server.test-support
  (:require [reloaded.repl :refer [system set-init! init start stop go reset]]
            [gnar.server.systems :refer [dev-system]]
            [environ.core :as environ]
            [clojure.test :refer [use-fixtures]]))

(defn setup []
  (set-init! dev-system)
  (println (str "Current system: " dev-system "\n"))
  (println (str "Autostarting the system: " (go) "\n")))

(defn teardown []
  (println "Stopping the system")
  (stop))

(def test-env
  {:http-port 3000
   :database-url "postgres://localhost:5432/gnar_test"})

(defn system-fixture [tests]
  (with-redefs [environ/env (merge environ/env test-env)]
    (setup)
    (tests)
    (teardown)))
