(ns junk-sender.sender
  (:require [clojure.tools.logging :refer :all]
            [postal.core :refer :all]))


(defn SMPT-server [host port tls user pass]
  {:host host :port port :user user :pass pass :tls tls :starttls.enable true :debug true})

(def SMTP-default (SMPT-server "smtp.gmail.com" 587 true "mladensavic94@gmail.com" "lxuehmvciglikbrf"))

(defn send-email [to subject body]
  (send-message SMTP-default {:from (:host SMTP-default) :to to :subject subject :body body})
  (info "poslat mail"))

(defn send-email-async [to subject body]
  (let [id (future (send-message SMTP-default {:from (:host SMTP-default) :to to :subject subject :body body}))]
    (info "mail uguran u future za ml" id)
    {:response "Ode mail"}))
