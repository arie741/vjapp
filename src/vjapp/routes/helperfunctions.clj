(ns vjapp.routes.helperfunctions
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

;helper functions

(defn val-status [uname succ fail]
  (if (= "admin" (apply :service (db/searchu uname)))
    succ
    fail))

(defn login [email pass succ fail]
      (let [uname (db/searchu email)]
    (if (not (empty? uname))
       (if (= pass (apply :password uname))
           (do
            (session/put! :username email)
              succ)
              fail)
    fail)))

(defn validate [succ fail]
      (if (session/get :username)
            succ
            fail))