(ns junk-sender.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :refer [response]]
            [compojure.handler :as handler]
            [junk-sender.db :refer :all]
            [monger.json :refer :all]))



(defn- wrap-response [body]
  (try {:headers {"Content-Type" "application/json; charset=utf-8"}
        :body    (cheshire.core/generate-string body)
        :status  200}
       (catch Exception e {:headers {"Content-Type" "application/json; charset=utf-8"}
                           :body    (str e)
                           :status  500})))
(defn- wrap-error [func & args]
  (try
    (wrap-response (apply func args))
    (catch Exception e  {:status 400 :body (str "e u kurac " e)})))

(defroutes router
           (GET "/api/v1/user/:id" [id] (wrap-error find-one :user id))
           (GET "/api/v1/user" [] (wrap-error (find-all :user)))
           (POST "/api/v1/user" {body :body} (wrap-error insert-doc :user body))
           (GET "/api/v1/template/:id" [id] (wrap-error (find-one :template id)))
           (GET "/api/v1/template" [] (wrap-error (find-all :template)))
           (POST "/api/v1/template" {body :body} (wrap-error insert-doc :template body))
           (route/not-found "Not Found"))

(def handle-req
  (-> (handler/api router)
      (middleware/wrap-json-body {:keywords? true :bigdecimals? true})
      (middleware/wrap-json-response)))


(defn -main [& args]
  (jetty/run-jetty handle-req args))