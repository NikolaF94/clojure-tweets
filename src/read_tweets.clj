
(ns read-tweets
  (:import (me.jhenrique.main Main))
  (require [damionjunk.nlp.stanford :refer :all]))
(defn gettweets [] (into [] (for [x (Main/main "realdonaldtrump" "China" "2015-12-20" "2019-01-01" 5)]
                              (into {} (for [y (bean x)]
                                         y)) ) ))




(defn oceni [] (println (sentiment-maps (str (let [v (gettweets)]
                                               (vec (map #(% :text) v)))) ) ) )


(println (for [x (let [v (gettweets)]
                   (vec (map #(% :text) v)))] x) )

(str (let [v (gettweets)]
       (vec (map #(% :text) v))))






