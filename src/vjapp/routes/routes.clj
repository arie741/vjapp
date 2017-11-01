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
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(hrel/auto-reload *ns*)
(html/set-ns-parser! hjsoup/parser)

;deftemplate
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

(defsnippet inboxcontent "public/inboxcontent.html"
	[:a.inboxc]
	[uuid title message date owner]
	[:h2.ictitle] (html/content title)
	[:p.iccontent] (html/content message)
	[:a.inboxc] (html/set-attr :href (str "/message/" uuid))
	[:div.icdate] (html/content date)
	[:h2.icsender] (html/content (apply :nama (db/searchu owner))))

(defn inboxcs [inboxes]
	(map #(inboxcontent (:uuid %) (:title %) (:message %) (:date %) (:sender %)) inboxes))

(defsnippet inboxpage "public/inbox.html"
	[:div#inbox]
	[& inboxes]
	[:form#loginform] (html/append (html/html-snippet (anti-forgery-field)))
	[:div#inboxcontent] (html/content (apply inboxcs inboxes))
  [:div.icname] (if (session/get :username)
                  (html/content (apply :nama (db/searchu (session/get :username))))
                  (html/content "")))

(defsnippet sendemail "public/send.html"
	[:div#sendemail]
	[sent]
	[:form#sendform] (html/append (html/html-snippet (anti-forgery-field)))
  [:div.icname] (html/content (apply :nama (db/searchu (session/get :username))))
  [:div.w-form-done] (if sent
                        (html/set-attr :style "display:block;")
                        (html/set-attr :style "display:none;")))

(defsnippet messagepage "public/message.html"
	[:div#message]
	[title message date sender]
	[:h2.ictitle] (html/content title)
	[:p.iccontent] (html/content message)
	[:div.icdate] (html/content date)
	[:h2.icsender] (html/content sender)
  [:div.icname] (html/content (apply :nama (db/searchu (session/get :username)))))

(defsnippet contactus "public/contactus.html"
  [:div#contactus]
  [])

;template
(deftemplate indexpage "public/index.html"
  [snippet]
  [:div#header] (html/substitute (header))
  [:div#content] (html/content snippet)
  [:div#footer] (html/content (footer)))

;Routes
(defroutes app-routes
  (GET "/" [] 
    (indexpage (homepage)))
  (GET "/ceritakita" []
  	(validate (indexpage (inboxpage (db/searchinbox (session/get :username)))) (indexpage (ceritakita false))))
  (POST "/login-action" {params :params}
  	(login (:email params) (:password params) 
  		(resp/redirect "/inbox") 
  		(indexpage (ceritakita true))))
  (GET "/inbox" []
  	(validate (indexpage (inboxpage (db/searchinbox (session/get :username)))) (indexpage (ceritakita false))))
  (GET "/send-email" []
  	(validate (indexpage (sendemail false)) (indexpage (ceritakita false))))
  (POST "/send-action" {params :params}
  	(do (let [etitle (:etitle params)
  	  		  emes (:emessage params)
  	  		  efrom (session/get :username)
  	  		  eto "faisal@visijurusan.com"]
  	  		(db/sendemail efrom eto etitle emes))
  		(validate (indexpage (sendemail true)) (indexpage (ceritakita false)))))
  (GET "/message/:uuid" [uuid]
  	(let [dat (db/searchm uuid (session/get :username))
  		  emes (apply :message dat)
  		  etitle (apply :title dat)
  		  edate (apply :date dat)
  		  esender (apply :sender dat)]
  		  (validate (indexpage (messagepage etitle emes edate esender)) (indexpage (ceritakita false)))))
  (GET "/contactus" []
    (indexpage (contactus)))
  (GET "/q" []
    (db/searchu "faisal@visijurusan.com"))
  (GET "/logout" []
  	(do
  		(session/clear!)
  		(resp/redirect "/")))
  (route/not-found "Not Found"))