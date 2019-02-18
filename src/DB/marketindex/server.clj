(ns DB.marketindex.server
  (:require [monger.core :as mg]
            [monger.collection :as mc])

  (:import [com.mongodb MongoOptions ServerAddress])
  (:import org.bson.types.ObjectId))

(let [conn (mg/connect)])

(let [conn (mg/connect {:host "db.megacorp.internal"})])

(let [conn (mg/connect {:host "db.megacorp.internl" :port 7878})])


(defn get-index []  (let [conn (mg/connect)
                          db (mg/get-db conn "nasdaq")
                          oid  (ObjectId.)]
                      (mc/find-maps db "index" {:date "6/8/2018"} )))



