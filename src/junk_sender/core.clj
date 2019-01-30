(ns junk-sender.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :refer [response]]
            [compojure.handler :as handler]
            [junk-sender.db :refer :all]
            monger.json))

(defn- wrap-response [body]
  (try {:headers {"Content-Type" "application/json; charset=utf-8"}
        :body    (cheshire.core/generate-string body)
        :status  200}
       (catch Exception e {:headers {"Content-Type" "application/json; charset=utf-8"}
                           :body    (str e)
                           :status  500})))

(defn- wrap-error [func & args]
  (try
    (prn args)
    (wrap-response (apply func args))
    (catch Exception e {:status 400 :body (str "Error occurred: " e)})))

(defroutes router
           (GET "/api/v1/user/:id" [id] (wrap-error find-one :user id))
           (GET "/api/v1/user" [] (wrap-response (find-all :user)))
           (POST "/api/v1/user" {body :body} (wrap-error insert-doc :user body))
           (PUT "/api/v1/user" {body :body} (wrap-error update-doc :user body))
           (DELETE "/api/v1/user/:id" [id] (try (delete-one :user id) {:status 202}
                                                (catch Exception e {:status 500 :body (str e)})))
           (GET "/api/v1/template/:id" [id] (wrap-error (find-one :template id)))
           (GET "/api/v1/template" [] (wrap-response (find-all :template)))
           (POST "/api/v1/template" {body :body} (wrap-error insert-doc :template body))
           (PUT "/api/v1/template" {body :body} (wrap-error update-doc :template body))
           (DELETE "/api/v1/template/:id" [id](try (delete-one :template id) {:status 202}
                                                   (catch Exception e {:status 500 :body (str e)})))
           (GET "/api/v1/message/:id" [id] (wrap-error (find-one :msgReq id)))
           (GET "/api/v1/message" [] (wrap-response (find-all :msgReq)))
           (POST "/api/v1/message" {body :body} (wrap-error insert-doc :msgReq body))
           (PUT "/api/v1/message" {body :body} (wrap-error update-doc :msgReq body))
           (DELETE "/api/v1/message/:id" [id] (try (delete-one :msgReq id) {:status 202}
                                                   (catch Exception e {:status 500 :body (str e)})))
           (route/not-found "Not Found"))

(def handle-req
  (-> (handler/api router)
      (middleware/wrap-json-body {:keywords? true :bigdecimals? true})
      (middleware/wrap-json-response)))


(defn -main [& args]
  (jetty/run-jetty handle-req args))