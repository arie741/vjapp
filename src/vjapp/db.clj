(ns vjapp.db
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [noir.session :as session]
            [clojure.java.jdbc :as jdbc]
            [clj-postgresql.core :as pg]))

(def dbase (pg/pool :host "localhost:5432"
                  :user "vjapp"
                  :dbname "vjapp"
                  :password "vjapp2000"))

(defn searchu [uname]
	(jdbc/query dbase [(str "select * from users where email = '" uname "'")]))

(defn searchinbox [owner]
	(jdbc/query dbase [(str "select * from inbox where owner = '" owner "'")]))