(ns junk-sender.sender
  (:require [clojure.tools.logging :refer :all]
            [postal.core :refer :all]
            [junk-sender.db :as db]
            [comb.template :as template]))


(defn SMPT-server [host port tls user pass]
  {:host host :port port :user user :pass pass :tls tls :starttls.enable true :debug false})

(def SMTP-default (SMPT-server "smtp.gmail.com" 587 true "mladensavic94@gmail.com" "lxuehmvciglikbrf"))

(defn send-email [to subject body]
  (info "send-email("to " " subject")")
  (send-message SMTP-default {:from (:host SMTP-default) :to to :subject subject :body body})
  (db/insert-doc :msgReq {:to to :subject subject :message body})
  (info "~send-email("to " " subject")"))

(defn send-email-async [to subject body]
  (let [id (future (send-message SMTP-default {:from (:host SMTP-default) :to to :subject subject :body body}))]
    (info "mail uguran u future za ml" id)
    {:response "Ode mail"}))

(defn check-params-nil? [temp mappings]
  (let [params (map #(keyword %) (:params temp))]
    (some nil? (map #(% mappings) params))))

(defn create-message [msg-reg-id mappings]
  (info "Evaluate template for " msg-reg-id " with " mappings)
  (let [temp (db/find-one-by-field :template :tempID msg-reg-id)]
    (if (not (check-params-nil? temp mappings))
      (template/eval (:src temp) mappings)
      (throw (Exception. (str "missing template " (:tempID temp)  " mappings, required:" (:params temp)))))))

(defn check-mappings [mappings]
  (if (nil? (:tempID mappings))
    (throw (Exception. (str "missing :tempID keyword")))
    (if (nil? (:subject mappings))
      (throw (Exception. (str "missing :subject keyword")))
      (if (nil? (:to mappings))
        (throw (Exception. (str "missing :to keyword")))
        true))))

(defn create-and-send [mappings]
  (try
    (if (check-mappings mappings)
      (let [tempID (:tempID mappings)
            subj (:subject mappings)
            to (:to mappings)
            msg-body (create-message tempID mappings)]
        (send-email to subj msg-body)))
    (catch Exception e
      (info (.getMessage e))
      (throw e))))