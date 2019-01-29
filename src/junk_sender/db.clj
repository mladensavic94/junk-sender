(ns junk-sender.db
  (:require [monger.core :as mcore]
            [monger.collection :as mcol])
  (:import (org.bson.types ObjectId)))

(def connection
   (mcore/connect {:host "127.0.0.1" :port 27017}))

(def db
  (mcore/get-db connection "junk-sender"))

(def collections {:user "user" :template "template"})

(defn insert-doc [collection doc]
  (mcol/insert-and-return db (collection collections)  doc))

(defn find-all [collection]
  (mcol/find-maps db (collection collections)))

(defn find-one [collection id]
  (mcol/find-map-by-id db (collection collections) (ObjectId. id)))


