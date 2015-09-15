(ns gnar.server.database-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [environ.core :refer [env]]
            [gnar.server.database :refer :all]))

;; Helper functions for wrapping tests in DB transactions:
(defn clear [test]
  (jdbc/with-db-transaction [db db-spec]
    (jdbc/db-set-rollback-only! db)
    (binding [db-spec db]
      (test))))
;; Transaction per test, rollback at end of each assertion

(use-fixtures :each clear)

(deftest db-spec-test
  (testing "is not nil"
    (is (not= nil db-spec))))

(deftest all-links-test
  (testing "is empty to start"
    (is (= [] (all-links)))))
;; (facts "all-links"
;;        (with-state-changes [(around :facts (clear ?form))]
;;          (fact "Is empty to start")))

(deftest database-insertion-test
  (testing "it works?"
    (let [title "title"
          links (create-link {:title title
                              :url "http://example.com"
                              :domain "example.com"})
          link (first links)]
      (is (= title (:title link))))))
