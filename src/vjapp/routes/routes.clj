(ns vjapp.routes.routes
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
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [vjapp.routes.helperfunctions :refer :all]
            [vjapp.routes.inbox :refer :all]
            [vjapp.routes.profile :refer :all]))

(hrel/auto-reload *ns*)
(html/set-ns-parser! hjsoup/parser)

;snippet
(defsnippet homepage "public/homepage.html"
  [:div#homepage]
  [])

(defsnippet inboxlink "public/inboxlink.html"
  [:a#inboxlink]
  [])

(defsnippet header "public/header.html"
  [:div#header]
  []
  [:a#loginlink] (if (session/get :username)
                  (html/set-attr :href "../logout")
                  (html/set-attr :href "../ceritakita"))
  [:a#loginlink] (if (session/get :username)
                  (html/content "Logout")
                  (html/content "Login"))
  [:a#inboxlink] (if (session/get :username)
                      (html/substitute (inboxlink))
                      (html/content "")))

(defsnippet footer "public/footer.html"
  [:div#footer]
  [])

(defsnippet ceritakita "public/ceritakita.html"
	[:div#ceritakita]
	[error]
	[:form#loginform] (html/append (html/html-snippet (anti-forgery-field)))
  [:div.w-form-fail] (if error
                        (html/set-attr :style "display:block;")
                        (html/set-attr :style "display:none;")))

(defsnippet contactus "public/contactus.html"
  [:div#contactus]
  [])

(defsnippet pathway "public/pathway.html"
  [:div#pathway]
  [])

;template
(deftemplate indexpage "public/index.html"
  [snippet & datagrf]
  [:div#header] (html/substitute (header))
  [:div#content] (html/content snippet)
  [:div#footer] (html/content (footer))
  [:html] (if (not (empty? datagrf))
              (html/set-attr :data-wf-page "59dc7a1f92c47000010c7c4e")
              (html/set-attr :data-wf-page ""))
  [:html] (if (not (empty? datagrf))
              (html/set-attr :data-wf-site "59dc43273f7a820001ca73b8")
              (html/set-attr :data-wf-page "")))

;Routes
(defroutes app-routes
  (GET "/" [] 
    (indexpage (homepage)))
  (GET "/ceritakita" []
  	(validate (indexpage (inboxpage (db/searchinbox (session/get :username)))) (indexpage (ceritakita false))))
  (POST "/login-action" {params :params}
  	(login (:email params) (:password params) 
  		(resp/redirect "/profile") 
  		(indexpage (ceritakita true))))
  (GET "/inbox" []
  	(validate (indexpage (inboxpage (db/searchinbox (session/get :username)))) (indexpage (ceritakita false))))
  (GET "/send-email" []
  	(validate 
      (indexpage (sendemail false)) 
      (indexpage (ceritakita false))))
  (GET "/reply/:uname" [uname]
    (val-status (session/get :username)
      (validate (indexpage (sendemail false (apply str (rest uname)))) (indexpage (ceritakita false)))
      (validate (indexpage (sendemail false)) (indexpage (ceritakita false)))))
  (POST "/send-action" {params :params}
  	(do 
      (let [etitle (:etitle params)
            emes (:emessage params)
            efrom (session/get :username)
            eto (val-status (session/get :username) (:to params) "faisal@visijurusan.com")]
          (db/sendemail efrom eto etitle emes))
      (validate (indexpage (sendemail true)) (indexpage (ceritakita false)))))
  (GET "/message/:uuid" [uuid]
  	(let [dat (db/searchm uuid (session/get :username))
  		    emes (apply :message dat)
  		    etitle (apply :title dat)
  		    edate (apply :date dat)
  		    esender (apply :sender dat)
          uuid (apply :uuid dat)]
  		    (do 
            (db/is-read uuid)
            (val-status (session/get :username)
                      (validate (indexpage (messagepage-admin etitle emes edate esender)) (indexpage (ceritakita false)))
                      (validate (indexpage (messagepage etitle emes edate esender)) (indexpage (ceritakita false)))))))
  (GET "/profile" []
    (validate (indexpage (profilepage)) (indexpage (ceritakita false))))
  (GET "/contactus" []
    (indexpage (contactus) true))
  (GET "/pathway" []
    (indexpage (pathway)))
  (GET "/logout" []
  	(do
  		(session/clear!)
  		(resp/redirect "/")))
  (route/not-found "Not Found"))