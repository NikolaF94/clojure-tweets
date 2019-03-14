(ns read-tweets
  (:import (me.jhenrique.main Main))
  (:import org.bson.types.ObjectId)
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (require [damionjunk.nlp.stanford :refer :all])
  (require [clj-time.core :as t])
  (require [clj-time.format :as f])
  (:require [cheshire.core :refer :all])
  (require [clj-time.coerce :as c])
  (require [markdown-to-hiccup.core])


  (:import [java.util Calendar Date GregorianCalendar TimeZone]
           [java.sql Timestamp]
           (java.time.format DateTimeFormatter)
           (java.time ZonedDateTime)))

;Loads and returns tweets. Parameters are Twitter Account, Keyword and Start and End Dates, Max number of Tweets
(defn gettweets [username, keyword, startdate, enddate, maxtweets] (into [] (for [x (Main/main username keyword startdate enddate maxtweets )]
                              (into {} (for [y (bean x)]
                                         y)))))
"realdonaldtrump" "China" "2015-12-20" "2019-01-01" 5



(defn return-sentiment [] (into [] (sentiment-maps (str (let [v (gettweets)]
                                                          (vec (map #(% :text) v)))))))
(def sentiments (return-sentiment))


;(f/show-formatters)
;(def built-in-formatter (f/formatters :date))
;(f/parse custom-formatter "06/08/2018")
;(def custom-formatter (f/formatter "dd/MM/yyyy"))
;(use '(incanter core stats chart dataset))
;(def data)                                                         ;;

(def tvitovi (gettweets))


(def negative-sentiment (filter #(< (:sentiment %) 2) sentiments))

(def positive-sentiment (filter #(> (:sentiment %) 1) sentiments))

(defn get-index []  (let [conn (mg/connect)
                          db (mg/get-db conn "nasdaq")
                          oid  (ObjectId.)]
                      (mc/find-maps db "index" {:date "12/14/2018"} )))
(def indexes (get-index))
;(def index-date-complex (apply f/parse custom-formatter (map #(get % :date) indeksi) ) )

(def tweet-date-complex (map #(get % :date) tvitovi))
;;(def joda-complex (map #(c/from-date %) tweet-date-complex))

(def custom-formatter (f/formatter "MM/dd/yyyy"))

(def my-formatter (f/formatter "yyyy-MM-dd "))

(def tweet-date (map #(str %) joda))

(map #(f/parse custom-formatter % ) tweet-date)

(def nudge (= tweet-date index-date))

(map #(f/parse my-formatter % ) tweet-date)

(f/parse custom-formatter "12/14/2018")
(def formatter (f/formatters :date))
(f/parse formatter index-date)

(def tweet-joda-simple (map #(.format(java.text.SimpleDateFormat. "MM/dd/yyyy") %) tweet-date-complex))

;; METRIKE:

(def index-joda-simple (map #(get % :date) indexes))

(def return-open-seq (map #(get % :open) indexes))

(def return-close-seq (map #(get % :adjclose) indexes))

(def return-close (first (map #(read-string %)return-close-seq) ))

(def return-open (first (map #(read-string %)return-open-seq) ))

(def index-change-daily-number  (- return-close return-open))

(def index-change-daily (true? (neg? index-change-daily-number)) )












