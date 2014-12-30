;; the top section includes all of the libraries
;; injected so that we can use their namespace

(ns recursiftion.dao_dictionary
  (:require [clojure.string]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.operators :refer :all]
            [monger.conversion :refer [from-db-object]]
            [clojure.pprint :refer [pprint]])
  (:import [com.mongodb MongoOptions ServerAddress]
           [org.bson.types.ObjectId])
  )



;; connect using connection URI stored in an env variable, in this case, MONGOHQ_URL
; (let [uri               (System/genenv "MONGOHQ_URL")
(def mongo-uri "mongodb://mcferren:Broadway$19@linus.mongohq.com:10028/app30824309")
; (def mongo-uri "mongodb://mcferren:Broadway$19@linus.mongohq.com:10028/app30824309?maxPoolSize=128&waitQueueMultiple=5;waitQueueTimeoutMS=150;socketTimeoutMS=5500&autoConnectRetry=true;safe=false&w=1;wtimeout=2500;fsync=true")

(let [uri               mongo-uri
      {:keys [conn db]} (mg/connect-via-uri uri)
      coll "skittles"]
;   ; (mc/insert db "skittles" { :_id (ObjectId.) :first_name "John" :last_name "Lennon" })
	  (mc/remove db coll)
      (mc/insert-and-return db coll {:name "blue" :age 30}))



; inserts one word into the database
(defn insertword [word color payload]
  (let [wordinput word
        colorinput color
        payloadinput payload
        uri mongo-uri
      		{:keys [conn db]} (mg/connect-via-uri uri)
        coll "skittles"]

        ; (binding [*out* *err*]
        ;             (println (type {:word wordinput :color colorinput :payload payloadinput})))
    (mc/insert-and-return db coll {:word wordinput :color colorinput :payload payloadinput})  		
  )
)

; (defn insertUser [userid]
;   (let [useridinput userid
;         uri mongo-uri
;           {:keys [conn db]} (mg/connect-via-uri uri)
;         ucoll "users"]
;       ; (mc/insert-and-return db ucoll {:payload userid})
;       (mc/update db ucoll {:userdata useridinput} {:upsert true})
;       ; (mc/update db ucoll {:userdata useridinput})
;       ; (mc/update db ucoll {:player "sam"} {$set {:score 1088}} {:upsert true})
;     ))

(defn checkOrInsertUser [userid]
  ; (let [useridinput (userid  "id")
  (let [useridinput userid
        uri mongo-uri
          {:keys [conn db]} (mg/connect-via-uri uri)
        ucoll "users"]

      (do 
        (if (mc/any? db ucoll {:_id useridinput})
            (mc/find-one-as-map db ucoll {:_id useridinput})
            (mc/insert-and-return db ucoll {:_id useridinput :questionsauthored [] :questionsanswered []}))

        ; (mc/update db ucoll {:_id useridinput} {:upsert true})
        ; (mc/update db ucoll {useridinput "{}"} {$set {:questions-authored "[]" :questions-ansered "[]"}} {:upsert true})
      )
    ))


(defn insertQuestionAuthored [payload]
  (let [payloadinput payload
        uid (payloadinput :author)
        uri mongo-uri
          {:keys [conn db]} (mg/connect-via-uri uri)
        qcoll "questions"
        qobject (mc/insert-and-return db qcoll {:payload payloadinput :timestamp (System/currentTimeMillis)})
        qid (qobject :_id)
        ucoll "users"]

        (do 
            ;; ADD INSERT FOR USER'S "QUESTIONS-AUTHORED" CHILD BLOCK
            (mc/update db ucoll {:_id uid} {$push {:questionsauthored qid}})
            qobject
        )
    ))


(defn insertPayload [payload]
  (let [payloadinput payload
        uri mongo-uri
          {:keys [conn db]} (mg/connect-via-uri uri)
        qcoll "questions"]

          (mc/insert-and-return db qcoll {:payload payloadinput})
    ))

(defn acceptpayload [payload]
  (let [payloadinput payload
        uri mongo-uri
          {:keys [conn db]} (mg/connect-via-uri uri)
        coll "skittles"]
        ; (binding [*out* *err*]
        ;     (println (type payloadinput)))
        ; (binding [*out* *err*]
        ;     (pprint payloadinput))
  ;;;; must be a clojure.lang.PersistentArrayMap in order to insert
    ; (mc/insert-and-return db coll payloadinput)
    (mc/insert-and-return db coll {:payload payloadinput})  
    ))

; gets all from the database
(defn getall []
  (let [uri mongo-uri
      		{:keys [conn db]} (mg/connect-via-uri uri)
        coll "skittles"]

    ; (mc/find db coll {:name "blue"})	
    (from-db-object (mc/find-maps db coll { }) true)	
  )
)
