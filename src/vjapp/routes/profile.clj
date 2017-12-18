(ns vjapp.routes.profile
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [net.cgrand.enlive-html :refer [deftemplate defsnippet] :as html]
            [net.cgrand.reload :as hrel]
            [net.cgrand.jsoup :as hjsoup]
            [noir.session :as session]
            [vjapp.db :as db]
            [noir.response :as resp]
            [hiccup.core :as hc]
            [clj-time.core :as t]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(hrel/auto-reload *ns*)
(html/set-ns-parser! hjsoup/parser)

(defsnippet profilepage "public/profilepage.html"
      [:div#profile]
      []
      )