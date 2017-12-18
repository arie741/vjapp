(ns vjapp.routes.inbox
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
            [vjapp.routes.helperfunctions :refer :all]))

(hrel/auto-reload *ns*)
(html/set-ns-parser! hjsoup/parser)

(defsnippet inboxcontent "public/inboxcontent.html"
      [:a.inboxc]
      [uuid title message date owner]
      [:h2.ictitle] (html/content title)
      [:p.iccontent] (html/content message)
      [:a.inboxc] (html/set-attr :href (str "/message/" uuid))
      [:div.icdate] (html/content date)
      [:h2.icsender] (html/content (apply :nama (db/searchu owner)))
      [:div.read-unread] (if (= "read" (apply :isread (db/searchinbox-uuid uuid)))
                        (html/set-attr :class "read-unread read")
                        (html/set-attr :class "read-unread"))
      [:div.mail-details] (if (= "read" (apply :isread (db/searchinbox-uuid uuid)))
                        (html/set-attr :class "mail-details read")
                        (html/set-attr :class "mail-details")))

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

(defsnippet sendlink "public/snippets.html"
  [:input#email-to]
  [])

(defsnippet sendemail "public/send.html"
      [:div#sendemail]
      [sent & eto]
      [:form#sendform] (html/prepend (val-status (session/get :username) (sendlink) ""))
      [:input#email-to] (html/set-attr :value (val-status (session/get :username) (first eto) ""))
      [:form#sendform] (html/append (html/html-snippet (anti-forgery-field)))
      [:div.icname] (html/content (apply :nama (db/searchu (session/get :username))))
      [:div.w-form-done] (if sent
                              (html/set-attr :style "display:block;")
                              (html/set-attr :style "display:none;")))

(defsnippet replaylink "public/snippets.html"
        [:a#reply-btn]
        [eto]
        [:a#reply-btn] (html/set-attr :href (str "/reply/:" eto)))

(defsnippet messagepage-admin "public/message.html"
      [:div#message]
      [title message date sender]
      [:div#replay-loc] (html/substitute (replaylink sender))
      [:h2.ictitle] (html/content title)
      [:p.iccontent] (html/content message)
      [:div.icdate] (html/content date)
      [:h2.icsender] (html/content sender)
      [:div.icname] (html/content (apply :nama (db/searchu (session/get :username)))))

(defsnippet messagepage "public/message.html"
      [:div#message]
      [title message date sender]
      [:h2.ictitle] (html/content title)
      [:p.iccontent] (html/content message)
      [:div.icdate] (html/content date)
      [:h2.icsender] (html/content sender)
      [:div.icname] (html/content (apply :nama (db/searchu (session/get :username)))))