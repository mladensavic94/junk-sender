(ns junk-sender.db
  (:require [monger.core :as mcore]
            [monger.collection :as mcol]
            [clojure.tools.logging :refer :all]
            [validateur.validation :refer :all])
  (:import (org.bson.types ObjectId)
           (java.security InvalidParameterException)))

(def connection
  (mcore/connect {:host "127.0.0.1" :port 27017}))

(def db
  (mcore/get-db connection "junk-sender"))

(def collections {:user "user" :template "template" :msgReq "messageRequest"})

(def userValidator (validation-set (presence-of :name) (presence-of :lastName) (presence-of :mail)))
(def templateValidator (validation-set (presence-of :tempID) (presence-of :src) (presence-of :params)))
(def msgReqValidator (validation-set (presence-of :to) (presence-of :subject) (presence-of :message)))

(def validators {:user userValidator :template templateValidator :msgReq msgReqValidator})

(defn insert-doc [collection doc]
  (if (valid? (collection validators) doc)
    (do(info "Insert " doc " into " (collection collections))
       (mcol/insert-and-return db (collection collections) doc))
    (throw (InvalidParameterException. (str doc " didnt pass validation")))))

(defn find-all [collection]
  (info "Find all from " (collection collections))
  (mcol/find-maps db (collection collections)))

(defn find-one [collection ^String id]
  (info "Find by id:" id " from " (collection collections))
  (mcol/find-map-by-id db (collection collections) (ObjectId. id)))

(defn find-one-by-field [collection field-name value]
  (info "Find by field:" field-name ": " value" from " (collection collections))
  (mcol/find-one-as-map db (collection collections) (hash-map field-name value)))

(defn update-doc [collection doc]
  (info "Update " doc " from " (collection collections))
  (mcol/update-by-id db collection (ObjectId. ^String (:_id doc)) (dissoc doc :_id))
  (find-one collection (:_id doc)))

(defn delete-one [collection ^String id]
  (info "Delete by id " id " from " (collection collections))
  (mcol/remove-by-id db collection (ObjectId. id)))



