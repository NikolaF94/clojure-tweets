(ns clojure-tweets.core
  (:import (me.jhenrique.main Main)
           (org.bson.types ObjectId))
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer :all]
            [ring.middleware.reload :refer [wrap-reload]]
            [net.cgrand.enlive-html :as html]
            [compojure.route :as route]
            [compojure.core :refer [defroutes GET]]
            [hiccup.core :refer :all]
            [net.cgrand.enlive-html :as html]
            [ring.util.request :as request]
            [ring.util.response :as resp]
            [ring.middleware.session :as session]
            [ring.middleware.params :as params]
            [ring.middleware.keyword-params :as keyword-params]
            [selmer.parser :refer [render-file]]
            [monger.collection :as mc]
            [monger.core :as mg]
            [clojure.edn :as edn]
            [damionjunk.nlp.stanford :refer :all]
            [oz.core :as oz]))

(defn get-tweets [username, keyword, startdate, enddate, maxtweets]
  (into [] (for [x (Main/main username keyword startdate enddate maxtweets)]
             (into {} (for [y (bean x)]
                        y)))))

(def tvitovi (get-tweets "realdonaldtrump" "economy" "2015-12-20" "2019-04-30" 50))
(defn formater [y] (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") y))
(defn get-index [date] (let [conn (mg/connect)
                             db (mg/get-db conn "SP")
                             oid (ObjectId.)]
                         (mc/find-maps db "spider" {:date (get date :date)})))
;(let [indexes (get-index date) ]
; (true? (neg? (- (first (map #(read-string %)(map #(get % :adjclose) indexes)) ) (first (map #(read-string %)(map #(get % :open) indexes)) )))) )

(defn get-value [date] (str (let [indexes (get-index date)]
                              (- (first (map #(read-string %) (map #(get % :adjclose) indexes))) (first (map #(read-string %) (map #(get % :open) indexes)))))))


(defn final-index [tweets] (map #(get-value %) (map #(get % :date))))

(defn indexes [tweets] (for [x (map #(select-keys % [:date]) tweets)] (into {} (get-index x))))
(defn indexed [tweets-with-index] (for [x (map #(select-keys % [:date]) tweets-with-index)] (into {} (get-index x))))






(defn data-vega-plot [tweets-with-index] (map #(select-keys % [:sentiments :index :date] ) tweets-with-index))

;plot vega:
;(oz/start-plot-server!)


(defn histogram [tweets-with-index]
  {:data {:values (data-vega-plot tweets-with-index)}
   :width 800,
   :height 400
   :mark "bar"
   :encoding {:x {
                  :field "sentiments"
                  :type "ordinal"}
              :y {
                  :field "index"
                  :type "quantitative"
                  }
              :color {
                      :field "sentiments"
                      :type "nominal"}}})
;(oz/view! histogram)
(defn colored-scatterplot [tweets-with-index]
  {:data {:values (data-vega-plot tweets-with-index)}
   :width 800,
   :height 400
   :mark "point"
   :encoding {:x {
                  :field "index"
                  :type "quantitative"}
              :y {
                  :field "sentiments"
                  :type "quantitative"
                  }
              :color {
                      :field "sentiments"
                      :type "nominal"}
              :shape {
                      :field "sentiments"
                      :type "nominal"
                      }}})
;(oz/view! colored-scatterplot)





;get all dates:
(def data-dates-adjclose (map #(select-keys % [:date :adjclose]) (let [conn (mg/connect)
                                                                       db (mg/get-db conn "SP")
                                                                       oid  (ObjectId.)]
                                                                   (mc/find-maps db "spider"  )) ))
(defn data-vega-plot1 [tweets-with-index] (map #(assoc %1 :adjclose %2) (map #(select-keys % [:sentiments :index :date] ) tweets-with-index) (map #(get % :adjclose) (map #(select-keys % [:adjclose]) (indexed tweets-with-index)))))



(defn line-plot [tweets-with-index]
  {:data {:values data-dates-adjclose  :start "2018" :format {:parse {:start "date:'%Y'"}}  }
   :width 2040,
   :height 400,
   :layer [ {:encoding {:x {:field "date" :type "temporal"}
                        :y {:field "adjclose" :type "quantitative"}
                        :color {:field "item" :type "nominal"}}
             :mark "line"}
           {
            :mark "point"
            :data {:values (data-vega-plot1 tweets-with-index)}
            :encoding {:x {:field "date" :type "temporal"}
                       :y {:field "adjclose" :type "quantitative"}
                       :shape {:field "sentiments" :type "nominal"}
                       :color {:field "sentiments" :type "nominal"}}
            }]

   }
  )

(defn viz [tweets-with-index]
  [:div
   [:h1 "Tweets sentiment analysis"]
   [:p "Sentiments and S&P500 daily change"]
   [:div {:style {:display "flex" :flex-direction "row"}}
    [:vega-lite (colored-scatterplot tweets-with-index) ]
    [:vega-lite (histogram tweets-with-index) ]]
   [:p "Line plot"]
   [:vega (line-plot tweets-with-index)]

   [:p "Sentiments: 1 - negative 2 - neutral 3 - positive"]])


;(def odabrani (map #(select-keys % [:adjclose]) indeksi))
;(def odabrani1 (map #(get % :adjclose) (map #(select-keys % [:adjclose]) indeksi)))
(defn data-vega-plot1 [tweets-with-index] (map #(assoc %1 :adjclose %2) (map #(select-keys % [:sentiments :index :date] ) tweets-with-index) (map #(get % :adjclose) (map #(select-keys % [:adjclose]) (indexed tweets-with-index)))))

(defn indexed [tweets-with-index] (for [x (map #(select-keys % [:date]) tweets-with-index)] (into {} (get-index x))))











(defn return-sentiment [tweets-with-index] (into [] (sentiment-maps (str (let [v tweets-with-index]
                                                                           (vec (map #(% :text) v)))))))

(defn show-index [tweets-with-sentiment]

  ;(render-file "public/tweets-analysis.html" {:items tweets-with-index})
(do  (oz/view! (viz tweets-with-sentiment) ) (render-file "public/tweets-analysis.html" {:items tweets-with-sentiment}))





  ;(render-file "public/tweets-analysis.html" {:items (final-index tweets) })



  ; (let [indexes (get-value (first (map #(.format (java.text.SimpleDateFormat. "MM/dd/yyyy") %) (map #(get % :date) tweets))))]
  ;    (render-file "public/tweets-analysis.html" {:items1 indexes}))

  )

;(render-file "public/tweets-analysis.html" {:items tweets})

;(defn show-tweets [tweets]
;  (html/at (html/html-resource "public/tweets-analysis.html")
;           [:table :tbody :tr]
;           (html/clone-for [tweet tweets]
;                           [:td#un] (html/content (map #(get % :id) tweets))
;                           [:td#tx] (html/content (map #(get % :text) tweets))
;                           [:td#dt] (html/content (map #(.format (java.text.SimpleDateFormat. "MM/dd/yyyy") %) (map #(get % :date) tweets))))))


(defroutes app-routes
           (GET "/" []
             (resp/content-type (resp/resource-response "index.html" {:root "public"}) "text/html"))
           (GET "/index" []
             (resp/content-type (resp/resource-response "index.html" {:root "public"}) "text/html"))

           (params/wrap-params (keyword-params/wrap-keyword-params
                                 (GET "/tweets-analysis" request (let [encoded-request (:params request)]
                                                                   (do (let [tweet (get-tweets
                                                                                     (:username encoded-request)
                                                                                     (:keyword encoded-request)
                                                                                     (:startdate encoded-request)
                                                                                     (:enddate encoded-request)
                                                                                     (read-string (:maxtweets encoded-request)))]
                                                                         (let [tweets (map #(update-in % [:date] formater) tweet)]
                                                                              (let [tweets-with-index (map #(assoc %1 :index %2 ) tweets
                                                                                                           (into [] (doall (map #(- %1 %2)
                                                                                                                (into [] (map #(if (nil? %) 0 %) (map #(clojure.edn/read-string %)
                                                                                                                                (map #(str %) (map #(get % :adjclose) (indexes tweets) )))))
                                                                                                                (into [] (map #(if (nil? %) 0 %) (map #(clojure.edn/read-string %)
                                                                                                                                (map #(str %) (map #(get % :open) (indexes tweets)))) ))  ))))]
                                                                                (let [tweets-with-sentiments (map #(assoc %1 :sentiments %2) tweets-with-index (map #( get % :sentiment ) (return-sentiment tweets-with-index)) ) ]
                                                                                  (show-index tweets-with-sentiments)))  )))))

                                 ))

           (route/resources "/"))



(defn -main
  "A very simple web server using Ring & Jetty"
  [port-number]
  (jetty/run-jetty app-routes
                   {:port (Integer. port-number)}))
(defn -dev-main
  "A very simple web server using Ring & Jetty that reloads code changes via the development profile of Leiningen"
  [port-number]
  (jetty/run-jetty (wrap-reload #'app-routes)
                   {:port (Integer. port-number)}))

;(GET "/" request
;             (html/emit* (show-index)))





(def datumi (map #(select-keys % [:date]) tvitovi))
; #inst date u simple date:
(defn formater [y] (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") y))
;promena datuma tvitova:
(map #(update-in % [:date] formater) tvitovi)

(def tvitovii (map #(update-in % [:date] formater) tvitovi))

(map #(get-index %) (map #(select-keys % [:date]) tvitovii))

(map #(get-index %) (map #(select-keys % [:date]) tvitovii))

(def indeksi (for [x (map #(select-keys % [:date]) tvitovii)] (into {} (get-index x))))

;vrati indekse brojcane vrednosti bez nill-a:
(into [] (map #(if (nil? %) 0 %) (map #(clojure.edn/read-string %) (map #(str %) (map #(get % :adjclose) indeksi)))))
(def indeksiBO (into [] (map #(if (nil? %) 0 %) (map #(clojure.edn/read-string %) (map #(str %) (map #(get % :adjclose) indeksi))))))
;izracunaj razliku indeksa za te tvitove
(doall (map #(- %1 %2) (into [] (map #(if (nil? %) 0 %) (map #(clojure.edn/read-string %) (map #(str %) (map #(get % :adjclose) indeksi))))) (into [] (map #(if (nil? %) 0 %) (map #(clojure.edn/read-string %) (map #(str %) (map #(get % :open) indeksi)))))))

;dodaj vrednosti indeksa na taj dan svakom tvitu:
(def indeks-tvitovi (map #(assoc %1 :index %2) tvitovii (into [] (doall (map #(- %1 %2) (into [] (map #(if (nil? %) 0 %) (map #(clojure.edn/read-string %) (map #(str %) (map #(get % :adjclose) indeksi))))) (into [] (map #(if (nil? %) 0 %) (map #(clojure.edn/read-string %) (map #(str %) (map #(get % :open) indeksi))))))))))

(defn return-sentiment [tweets-with-index] (into [] (sentiment-maps (str (let [v tweets-with-index]
                                                          (vec (map #(% :text) v)))))))
;(def sentimenti (return-sentiment tweets-with-index))


(defn indexed [tweets-with-index] (for [x (map #(select-keys % [:date]) tweets-with-index)] (into {} (get-index x))))


;dodaj sentimente svakom tvitu:
 (defn sentiments-added [tweets-with-index] (map #(assoc %1 :sentiments %2) tweets-with-index (map #( get % :sentiment ) (return-sentiment tweets-with-index)) ))



;podaci za incanter plot:
;(def data-sentiments (map #(get % :sentiments) konacni))

;(def data-indexes (map #(get % :index) konacni))

;incanter plot index-sentiment:
;(use '(incanter core stats charts datasets))
;(def mvn-plot (scatter-plot data-sentiments data-indexes))
;(view mvn-plot)

;podaci za Vega ploter:
(defn data-vega-plot [tweets-with-index] (map #(select-keys % [:sentiments :index :date] ) tweets-with-index))

;plot vega:
;(oz/start-plot-server!)


(defn histogram [tweets-with-index]
  {:data {:values (data-vega-plot tweets-with-index)}
   :width 800,
   :height 400
   :mark "bar"
   :encoding {:x {
                  :field "sentiments"
                  :type "ordinal"}
              :y {
                  :field "index"
                  :type "quantitative"
                  }
              :color {
                      :field "sentiments"
                      :type "nominal"}}})
;(oz/view! histogram)
(defn colored-scatterplot [tweets-with-index]
  {:data {:values (data-vega-plot tweets-with-index)}
   :width 800,
   :height 400
   :mark "point"
   :encoding {:x {
                  :field "index"
                  :type "quantitative"}
              :y {
                  :field "sentiments"
                  :type "quantitative"
                  }
              :color {
                      :field "sentiments"
                      :type "nominal"}
              :shape {
                      :field "sentiments"
                      :type "nominal"
                      }}})
;(oz/view! colored-scatterplot)





;get all dates:
(def data-dates-adjclose (map #(select-keys % [:date :adjclose]) (let [conn (mg/connect)
                                                                       db (mg/get-db conn "SP")
                                                                       oid  (ObjectId.)]
                                                                   (mc/find-maps db "spider"  )) ))
(defn data-vega-plot1 [tweets-with-index] (map #(assoc %1 :adjclose %2)  (map #(select-keys % [:sentiments :index :date] ) tweets-with-index) (map #(get % :adjclose) (map #(select-keys % [:adjclose]) (indexed tweets-with-index))) ))



(defn line-plot [tweets-with-index]
  {:data {:values data-dates-adjclose  :start "2018" :format {:parse {:start "date:'%Y'"}}  }
   :width 2040,
   :height 400,
   :layer [ {:encoding {:x {:field "date" :type "temporal"}
                        :y {:field "adjclose" :type "quantitative"}
                        :color {:field "item" :type "nominal"}}
             :mark "line"}
           {
            :mark "point"
            :data {:values (data-vega-plot1 tweets-with-index)}
            :encoding {:x {:field "date" :type "temporal"}
                       :y {:field "adjclose" :type "quantitative"}
                       :shape {:field "sentiments" :type "nominal"}
                       :color {:field "sentiments" :type "nominal"}}
            }]

   }
  )

(defn viz [tweets-with-index]
  [:div
   [:h1 "Tweets sentiment analysis"]
   [:p "Sentiments and S&P500 daily change"]
   [:div {:style {:display "flex" :flex-direction "row"}}
    [:vega-lite (colored-scatterplot tweets-with-index) ]
    [:vega-lite (histogram tweets-with-index) ]]
   [:p "Line plot"]
   [:vega (line-plot tweets-with-index)]

   [:p "Sentiments: 1 - negative 2 - neutral 3 - positive"]])


(def odabrani (map #(select-keys % [:adjclose]) indeksi))
(def odabrani1 (map #(get % :adjclose) (map #(select-keys % [:adjclose]) indeksi)))
(defn data-vega-plot1 [tweets-with-index] (map #(assoc %1 :adjclose %2)  (map #(select-keys % [:sentiments :index :date] ) tweets-with-index) (map #(get % :adjclose) (map #(select-keys % [:adjclose]) (indexed tweets-with-index))) ))

(defn indexed [tweets-with-index] (for [x (map #(select-keys % [:date]) tweets-with-index)] (into {} (get-index x))))