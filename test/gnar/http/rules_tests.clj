(ns gnar.http.rules-tests
  (:refer-clojure :exclude [assert])
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [gnar.http.rules :refer :all]
            [gnar.database :refer [find-user-by-username create-user-record]]
            [cemerick.friend.credentials :as creds]))


(let [username "test-user"
      email "test@example.com"
      password "test-password"]
  (with-state-changes [(before :facts
                               (do
                                 (when-not (find-user-by-username username)
                                   (create-user-record username email password))))]
    (deftest register!-tests
      (fact "Existing user fails to register again"
        (register! username email "other-pass" "other-pass") => (throws #"Username not available."))
      (fact "Passwords don't match"
        (register! username email "other-pass" "another-pass") => (throws #"Passwords don't match."))
      (fact "Password too short"
        (register! username email "short" "short") => (throws #"Password must be at least 8 characters long."))
      (fact "Email address already registered"
        (register! "new-username" email password password) => (throws #"Email address has already been registered.")))

    (deftest login!-tests
      (fact "Incorrect credentials"
        (login! username "wrong-password") => (throws #"Bad username/password."))

      (fact "Correct credentials"
        (login! username password) => (contains {:username username})))))
