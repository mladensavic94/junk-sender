(ns junk-sender.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :refer [response]]
            [compojure.handler :as handler]
            [junk-sender.db :refer :all]
            [clojure.tools.logging :refer :all]
            [junk-sender.sender :refer :all]
            monger.json))

(defn- wrap-response [body]
  (try {:headers {"Content-Type" "application/json; charset=utf-8"}
        :body    (cheshire.core/generate-string body)
        :status  200}
       (catch Exception e {:headers {"Content-Type" "application/json; charset=utf-8"}
                           :body    (str e)
                           :status  500})))

(defn error-response [msg]
  {:status 500 :body (str msg)})

(defroutes router
           (GET "/api/v1/template/:id" [id] (try (wrap-response (find-one :template id))
                                                 (catch Exception e (warn "Error on find template with id: " id " " (.getMessage e))
                                                                    (error-response e))))
           (GET "/api/v1/template" [] (try (wrap-response (find-all :template))
                                           (catch Exception e (warn "Error on find all templates" (.getMessage e))
                                                              (error-response e))))
           (POST "/api/v1/template" {body :body} (try (wrap-response (insert-doc :template body))
                                                      (catch Exception e (warn "Error on insert template" (.getMessage e))
                                                                         (error-response e))))
           (PUT "/api/v1/template" {body :body} (try (wrap-response (update-doc :template body))
                                                     (catch Exception e (warn "Error on update template" (.getMessage e))
                                                                        (error-response e))))
           (DELETE "/api/v1/template/:id" [id] (try (delete-one :template id) {:status 202}
                                                    (catch Exception e (warn "Error on delete template with id: " id " " (.getMessage e))
                                                      (error-response e))))
           (GET "/api/v1/message/:id" [id] (try (wrap-response (find-one :msgReq id))
                                                (catch Exception e (warn "Error on find message request with id:" id " " (.getMessage e))
                                                                   (error-response e))))
           (GET "/api/v1/message" [] (try (wrap-response (find-all :msgReq))
                                          (catch Exception e (warn "Error on find all message requests" (.getMessage e))
                                                             (error-response e))))
           (POST "/api/v1/message" {body :body} (try (wrap-response (create-and-send body))
                                                     (catch Exception e (warn "Error creating and sending message" (.getMessage e))
                                                                        (error-response e))))
           (POST "/api/v1/messageAsync" {body :body} (try (wrap-response (create-and-send-async body))
                                                     (catch Exception e (warn "Error creating and sending message" (.getMessage e))
                                                                        (error-response e))))
           (route/not-found "Not Found"))

(def handle-req
  (-> (handler/api router)
      (middleware/wrap-json-body {:keywords? true :bigdecimals? true})
      (middleware/wrap-json-response)))


(defn -main [& args]
  (jetty/run-jetty handle-req args))