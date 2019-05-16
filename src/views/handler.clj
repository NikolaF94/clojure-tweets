(ns views.handler
  (:require [views.views :as views]                         ; add this require
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [net.cgrand.enlive-html :as html]
            [ring.adapter.jetty :as jetty]))


(def app
  (wrap-defaults app-routes site-defaults))


;;(GET "/add-location"
;                []
;             (views/add-location-page))
;           (POST "/add-location"
;                 {params :params}
;             (views/add-location-results-page params))
;           (GET "/location/:loc-id"
;                [loc-id]
;             (views/location-page loc-id))
;           (GET "/all-locations"
;                []
;             (views/all-locations-page));;