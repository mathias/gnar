(ns gnar.server.systems
  (:require [system.core :refer [defsystem]]
            (system.components [http-kit :refer [new-web-server]]
                               [repl-server :refer [new-repl-server]]
                               [postgres :refer [new-postgres-database]])
            [environ.core :refer [env]]
            [gnar.server.handler :refer [app]]))

(defsystem dev-system
  [:web (new-web-server (or (Integer. (env :http-port))
                            3000) app)
   :db (new-postgres-database (env :database-url))])

(defsystem prod-system
  [:web (new-web-server (Integer. (env :http-port)) app)
   :repl-server (new-repl-server (Integer. (env :repl-port)))
   :db (new-postgres-database (env :database-url))])

