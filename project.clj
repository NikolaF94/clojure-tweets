(defproject clojure-tweets "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"], [stylefy "1.12.0"], [metasoarous/oz "1.6.0-alpha2"], [clj-http "3.9.1"], [enlive "1.1.6"], [markdown-to-hiccup "0.6.2"], [me.jhenrique/getoldtweets "1.1.0"], [incanter "1.9.3"], [damionjunk/nlp "0.3.0"], [com.novemberain/monger "3.1.0"], [clj-time "0.15.0"], [cheshire "5.8.1"], [ring "1.7.1"], [compojure "1.6.1"], [hiccup "2.0.0-alpha2"] [selmer "1.12.6"]]
  :repositories {"local" "file:C:\\Users\\Nikola\\.m2\\repository"}

   :main clojure-tweets.core

      :profiles
  {:dev

     {:main clojure-tweets.core/-dev-main}}


  )
