(ns gnar.server.systems
  (:require [system.core :refer [defsystem]]
            (system.components [http-kit :refer [new-web-server]]
                               [repl-server :refer [new-repl-server]]
                               [postgres :refer [new-postgres-database]])
            [environ.core :refer [env]]
            [gnar.server.handler :refer [app]]))

(defsystem dev-system
  [:web (new-web-server (Integer. (env :http-port)) app)
   :pg (new-postgres-database (env :database-url))])

(defsystem test-system
  [:pg (new-postgres-database (env :database-url))])

(defsystem prod-system
  [:web (new-web-server (Integer. (env :http-port)) app)
   :repl-server (new-repl-server (Integer. (env :repl-port)))
   :pg (new-postgres-database (env :database-url))])
