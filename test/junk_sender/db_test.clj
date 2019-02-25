(ns junk-sender.db-test
  (:require [clojure.test :refer :all]
            [junk-sender.db :refer :all]
            [validateur.validation :refer :all]))

(deftest userValidatorWillPass
  (testing "Validator test will pass for user"
    (is (valid? userValidator {:name "test" :lastName "test1" :mail "test2"}))))

(deftest userValidatorWillFail
  (testing "Validator test will fail for user without name"
    (is (not (valid? userValidator {:lastName "test1" :mail "test2"})))))
