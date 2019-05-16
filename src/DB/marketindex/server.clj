(ns DB.marketindex.server
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import org.bson.types.ObjectId))

(let [conn (mg/connect)])

(let [conn (mg/connect {:host "db.megacorp.internal"})])

(let [conn (mg/connect {:host "db.megacorp.internl" :port 7878})])


(defn get-index []  (let [conn (mg/connect)
                          db (mg/get-db conn "nasdaq")
                          oid  (ObjectId.)]
                      (mc/find-maps db "index" {:date "10/12/2018"} )))
(def custom-formatter (f/formatter "MM/dd/yyyy"))



(use '(incanter core stats charts))
(def y (filter #(% :date ) ))
(defn data [] (map #(Double/valueOf %) (map :adjclose (let [conn (mg/connect)
                                                           db (mg/get-db conn "SP")
                                                           oid  (ObjectId.)]
                                                       (mc/find-maps db "spider"  ))) ))

(require '[clj-time.format :as f])
(require '[clj-time.coerce :as c])

(f/formatter custom-formatter "MM/dd/yyyy")

(defn dates [] (map #(f/parse custom-formatter %) (map :date (let [conn (mg/connect)
                                                    db (mg/get-db conn "SP")
                                                    oid  (ObjectId.)]
                                                (mc/find-maps db "spider"  ))) ))

(def millis (map c/to-long (dates)))

(view data)
(view (time-series-plot millis (data)))
(f/formatter custom-formatter "MM/dd/yyyy")
(defn parse-int [& s]
  (Integer. (re-find #"[0-9]*" s)))

(Float/parseFloat "13034")

(defn parse-number
  "Reads a number from a string. Returns nil if not a number."
  [& s]
  (if (re-find #"^-?\d+\.?\d*$" s)
    (read-string s)))
(into [] (map :adjclose (let [conn (mg/connect)
                                      db (mg/get-db conn "SP")
                                      oid  (ObjectId.)]
                                  (mc/find-maps db "spider"  ) )))











