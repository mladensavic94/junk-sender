(defproject junk-sender "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :ring {:handler junk-sender.core/handle-req}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.6.1"]
                 [ring "1.7.1"]
                 [ring/ring-json "0.5.0-beta1"]
                 [ring/ring-defaults "0.3.2"]
                 [com.novemberain/monger "3.5.0"]
                 [cheshire "5.3.1"]]
  :plugins [[lein-ring "0.12.4"]])
