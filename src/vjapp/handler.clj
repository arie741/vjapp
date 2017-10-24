(ns vjapp.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [vjapp.routes.routes :refer :all]
            [noir.util.middleware :as middleware]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]))

(def app (middleware/app-handler
       [app-routes]
       :middleware [wrap-params wrap-multipart-params]))
