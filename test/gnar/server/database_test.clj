(ns gnar.server.database-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [gnar.server.database :refer :all]
            [korma.db :refer :all]))

;; Helper functions for wrapping tests in DB transactions:
(defn clear [test]
  (transaction
   (test)
   (rollback)))

;; Transaction per test, rollback at end of each assertion
(use-fixtures :each clear)

;; (deftest db-spec-test
;;   (testing "when no user and pass set"
;;     (let [spec (db-spec)]
;;       (is (= nil (:user spec)))
;;       (is (= nil (:pass spec)))
;;       (is (= "localhost" (:host spec)))
;;       (is (= "gnar_test" (:host spec))))))

(deftest all-links-test
  (testing "is empty to start"
    (is (= [] (all-links)))))

(deftest create-link-test
  (testing "it can insert record"
    (let [title "title"
          links (create-link {:title title
                              :url "http://example.com"
                              :domain "example.com"})
          link (first links)]
      (is (= title (:title link)))
      (is (not= nil (:id link))))
    (println (clear (create-link {:title "foo"
                                  :url "http://example.com/foo"
                                  :domain "example.com"})))))
