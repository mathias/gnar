(ns gnar.server.database-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [gnar.server.database :refer :all]
            [gnar.server.test-support :as test-support]
            [korma.db :refer :all]))

;; Helper functions for wrapping tests in DB transactions:
(defn clear [test]
  (transaction
   (test)
   (rollback)))

;; Transaction per test, rollback at end of each assertion
(use-fixtures :each clear)
(use-fixtures :once test-support/system-fixture)

(deftest db-spec-test
  (testing "when no user and pass set"
    (let [spec (db-spec)]
      (is (= false (contains? spec :user)))
      (is (= false (contains? spec :password)))
      (is (= "localhost" (:host spec)))
      (is (= "gnar_test" (:db spec)))))
  (testing "when user and pass set"
    (let [spec (db-spec "postgres://foo:bar@localhost/gnar_test")]
      (is (= "foo" (:user spec)))
      (is (= "bar" (:password spec))))))

(deftest all-links-test
  (testing "is empty to start"
    (is (= [] (all-links)))))

(deftest create-link-test
  (testing "it can insert record"
    (let [title "title"
          link (create-link {:title title
                             :url "http://example.com"
                             :domain "example.com"})]
      (is (= title (:title link)))
      (is (not= nil (:id link))))))
