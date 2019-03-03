(ns junk-sender.db-test
  (:require [clojure.test :refer :all]
            [junk-sender.db :refer :all]
            [validateur.validation :refer :all]))

(deftest userValidatorWillPass
  (testing "Validator test will pass for user"
    (is (valid? templateValidator {:tempID "test" :src "test1" :params ["test2"]}))))

(deftest userValidatorWillFail
  (testing "Validator test will fail for user without name"
    (is (not (valid? templateValidator {:src "test1" :tempID "test2"})))))
