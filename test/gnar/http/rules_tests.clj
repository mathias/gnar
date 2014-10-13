(ns gnar.http.rules-tests
  (:refer-clojure :exclude [assert])
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [gnar.http.rules :refer :all]
            [gnar.database :refer [find-user-by-username create-user-record delete-user]]
            [cemerick.friend.credentials :as creds]))


(facts "existing user"
  (let [username "test-user"
        email "test@example.com"
        password "test-password"]
    (with-state-changes [(before :facts (when-not (find-user-by-username username)
                                          (create-user-record username email password)))]
      (deftest register!-tests-for-existing-user
        (fact "Existing user fails to register again"
          (register! username email "other-pass" "other-pass") => (throws #"Username not available."))
        (fact "Email address already registered"
          (register! "new-username" email password password) => (throws #"Email address has already been registered.")))

      (deftest login!-tests
        (fact "Incorrect credentials"
          (login! username "wrong-password") => (throws #"Bad username/password."))

        (fact "Correct credentials"
          (login! username password) => (contains {:username username}))))))

(facts "no existing user"
  (let [new-username "test2"
        new-email "test2@example.com"
        new-password "new-password"]
    (with-state-changes [(before :facts (when (find-user-by-username new-username)
                                          (delete-user new-username)))]
    (deftest register!-tests
      (fact "Passwords don't match"
        (register! new-username new-email "other-pass" "another-pass") => (throws #"Passwords don't match."))
      (fact "Password too short"
        (register! new-username new-email "short" "short") => (throws #"Password must be at least 8 characters long."))

      (fact "Registers succesfully"
        (register! new-username new-email new-password new-password)
        =>
        (contains {:username new-username
                   :email new-email}))))))


