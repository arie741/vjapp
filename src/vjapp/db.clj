(ns vjapp.db
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [noir.session :as session]
            [clojure.java.jdbc :as jdbc]
            [clj-postgresql.core :as pg]
            [clj-time.local :as tl]))

(def dbase (pg/pool :host "localhost:5432"
                  :user "vjapp"
                  :dbname "vjapp"
                  :password "vjapp2000"))

(defn squuid []
  (let [uuid (java.util.UUID/randomUUID)
        time (System/currentTimeMillis)
        secs (quot time 1000)
        lsb (.getLeastSignificantBits uuid)
        msb (.getMostSignificantBits uuid)
        timed-msb (bit-or (bit-shift-left secs 32)
                          (bit-and 0x00000000ffffffff msb))]
    (java.util.UUID. timed-msb lsb)))

(defn searchu [uname]
	(jdbc/query dbase [(str "select * from users where email = '" uname "'")]))

(defn searchinbox [owner]
	(jdbc/query dbase [(str "select * from inbox where owner = '" owner "'")]))

(defn sendemail [efrom eto etitle emes]
  (jdbc/insert! dbase :inbox 
    {:owner eto :sender efrom :title etitle :message emes :uuid (str (squuid)) :date (str (tl/local-now))}))

(defn searchm [uuid]
  (jdbc/query dbase [(str "select * from inbox where uuid = '" uuid "'")]))