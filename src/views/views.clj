(ns views.views
  (:require [net.cgrand.enlive-html :as html]))


(defn show-index []
  (html/at (html/html-resource "public/index.html")))
