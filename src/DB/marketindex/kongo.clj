(ns DB.marketindex.kongo
  (:require [somnium.congomongo :as m]))

(def conn
  (m/make-connection "nasdaqindex"
                     :instances [{:host "127.0.0.1" :port 27017}]))


(defn getindex [] (prinln (m/fetch-one :open :where (:date {"2018-01-02"}))) )

(getindex)

(def getindex (atom
                (into {} (let [map (m/fetch)]))))