(ns vjapp.routes.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [net.cgrand.enlive-html :refer [deftemplate defsnippet] :as html]
            [noir.session :as session]
            [vjapp.db :as db]
            [noir.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

;deftemplate
(defn login [email pass succ fail]
	(let [uname (db/searchu email)
		  upass (apply :password uname)
		  uuid  (apply :uuid uname)]
		(if (= pass upass)
			(do
				(session/put! :username email)
				succ)
			fail)))

(defn validate [succ fail]
	(if (session/get :username)
		succ
		fail))

;template
(deftemplate indexpage "public/index.html"
	[snippet]
	[:div#content] (html/content snippet))

;snippet
(defsnippet ceritakita "public/ceritakita.html"
	[:div#ceritakita]
	[]
	[:form#loginform] (html/append (html/html-snippet (anti-forgery-field))))

(defsnippet inboxcontent "public/inboxcontent.html"
	[:div.inboxc]
	[uuid title message date owner]
	[:div.ictitle] (html/content title)
	[:div.iccontent] (html/content message)
	[:a.icuuid] (html/set-attr :href (str "/message/" uuid))
	[:div.icdate] (html/content date)
	[:div.icsender] (html/content owner))

(defn inboxcs [inboxes]
	(map #(inboxcontent (:uuid %) (:title %) (:message %) (:date %) (:sender %)) inboxes))

(defsnippet inboxpage "public/inbox.html"
	[:div#inbox]
	[& inboxes]
	[:form#loginform] (html/append (html/html-snippet (anti-forgery-field)))
	[:div#inboxcontent] (html/content (apply inboxcs inboxes)))

;Routes
(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/ceritakita" []
  	(indexpage (ceritakita)))
  (POST "/login-action" {params :params}
  	(login (:email params) (:password params) 
  		(resp/redirect "/inbox") 
  		(indexpage (ceritakita))))
  (GET "/inbox" []
  	(validate (indexpage (inboxpage (db/searchinbox (session/get :username)))) (indexpage (ceritakita))))
  (GET "/q" [] 
  	(apply str (db/searchinbox (session/get :username))))
  (GET "logout" []
  	(do
  		(session/clear!)
  		(resp/redirect "/")))
  (route/not-found "Not Found"))