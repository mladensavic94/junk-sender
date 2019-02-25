(ns junk-sender.sender
  (:require [clojure.tools.logging :refer :all]
            [postal.core :refer :all]
            [junk-sender.db :as db]
            [comb.template :as template]))


(defn SMPT-server [host port tls user pass]
  {:host host :port port :user user :pass pass :tls tls :starttls.enable true :debug true})

(def SMTP-default (SMPT-server "smtp.gmail.com" 587 true "mladensavic94@gmail.com" "lxuehmvciglikbrf"))

(defn send-email [to subject body]
  (info "send-email("to " " subject")")
  (send-message SMTP-default {:from (:host SMTP-default) :to to :subject subject :body body})
  (info "~send-email("to " " subject")"))

(defn send-email-async [to subject body]
  (let [id (future (send-message SMTP-default {:from (:host SMTP-default) :to to :subject subject :body body}))]
    (info "mail uguran u future za ml" id)
    {:response "Ode mail"}))

(defn create-message [msg-reg-id mappings]
  (info "Evaluate template for " msg-reg-id " with " mappings)
  (let [temp (db/find-one-by-field :template :tempID msg-reg-id)]
    (template/eval (:src temp) mappings)))

(defn create-and-send [mappings]
  (let [tempID (:tempID mappings)
        subj (:subject mappings)
        to (:to mappings)
        msg-body (create-message tempID mappings)]
    (send-email to subj msg-body)))