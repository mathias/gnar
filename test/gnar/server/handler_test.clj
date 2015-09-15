(ns gnar.server.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer (request) :rename {request mock-request}]
            [gnar.server.handler :refer [app]]))

(deftest app-test
  (testing "links endpoint"
    (let [response (app (mock-request :get "/api/links"))]
      (is (= 200 (:status response)))
      (is (= "application/json;charset=UTF-8" (get-in response [:headers "Content-Type"]))))))
  (testing "not-found route"
    (let [response (app (mock-request :get "/bogus"))]
      (is (= (:status response) 404))))
