(ns junk-sender.db
  (:require [monger.core :as mcore]
            [monger.collection :as mcol]
            [clojure.tools.logging :refer :all])
  (:import (org.bson.types ObjectId)))

(def connection
  (mcore/connect {:host "127.0.0.1" :port 27017}))

(def db
  (mcore/get-db connection "junk-sender"))

(def collections {:user "user" :template "template" :msgReq "messageRequest"})

(defn insert-doc [collection doc]
  (info  "Insert " doc " into " (collection collections))
  (mcol/insert-and-return db (collection collections) doc))

(defn find-all [collection]
  (info   "Find all from " (collection collections))
  (mcol/find-maps db (collection collections)))

(defn find-one [collection id]
  (info  "Find by id:" id " from " (collection collections))
  (mcol/find-map-by-id db (collection collections) (ObjectId. id)))

(defn update-doc [collection doc]
  (info  "Update " doc " from " (collection collections))
  (mcol/update-by-id db collection (ObjectId. (:_id doc)) (dissoc doc :_id))
  (find-one collection (:_id doc)))

(defn delete-one [collection id]
  (info   "Delete by id " id " from " (collection collections))
  (mcol/remove-by-id db collection (ObjectId. id)))



