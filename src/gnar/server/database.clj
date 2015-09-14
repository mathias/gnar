(ns gnar.server.database
  (:require [yesql.core :refer [defquery]]))

(defquery db-links "gnar/server/queries/links.sql")
