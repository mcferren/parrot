;; the top section includes all of the libraries
;; injected so that we can use their namespace

(ns recursiftion.model
  (:require [clojure.string]
            [monger.core :as mg]
            [monger.collection :as mc]
            [recursiftion.dao_dictionary :as dao]
            [clojure.data.json :as json]
            [clojure.pprint :refer [pprint]])
  (:import [com.mongodb MongoOptions ServerAddress]
           [org.bson.types.ObjectId])
  )



; (def pounce (recursiftion.dao_dictionary))

;; define a starter map object

(def dictionary {"apple" {
                    "noun" {
                            "defins" ["A crisp fruit"
                                     "Food from a tree"
                                     "Can peel before eating"]
                            "synons" [""]
                            "tally" ["I like apple" "Sometimes I like to eat a green apple"] ;; each element is a get function returning from wordString / defins / index
                            }
                          }
                 "smoke" {
                    "verb" {
                           "defins" [""]
                           "synons" [""]
                           "tally" [""] ;; each element is a get function returning from wordString / defins / index
                           }
                    "noun" {
                           "defins" [""]
                           "synons" [""]
                           "tally" [""] ;; each element is a get function returning from wordString / defins / index
                           }
                         }
                 "pear" {
                    "noun" {
                           "defins" [""]
                           "synons" [""]
                           "tally" [""] ;; each element is a get function returning from wordString / defins / index
                           }
                        }
                })


;; function for gaining access to dictionary map object

(defn all []
  dictionary)



; to test out crud operations
(defn insertdb [word color payload]
    (let [wordinput word
          colorinput color
          payloadinput payload]

    (recursiftion.dao_dictionary/insertword wordinput colorinput payloadinput)     
  )
)

(defn acceptUser [payload]
  (let [payloadinput payload
        returnarray []]

      (do 

          (recursiftion.dao_dictionary/checkOrInsertUser (payloadinput :id))


          (binding [*out* *err*]
              (println           
                (conj
                  returnarray
                  (get-in payloadinput [:likes :data 0 :id])
          )))

          (conj
            returnarray
            (get-in payloadinput [:likes :data 0 :id])
          )
          
          ; returnarray
      )
  )
)

(defn acceptquestionauthored [payload]
  (let [payloadinput payload]

      (recursiftion.dao_dictionary/insertQuestionAuthored payloadinput)
  )
)

(defn acceptdata [payload]
  (let [payloadinput payload]
      ; (do 
      ;   (binding [*out* *err*]
      ;       (println (type payload)))
      ;       ; (with-pprint-dispatch *code-dispatch* (pprint payload)))
      ;     ; payload
      ;     ; payload
      ;   )
    ; payloadinput

    ; (slurp (type (json/read-str payload)))
    ; (json/read-str "{\"a\":1,\"b\":2}")
    (recursiftion.dao_dictionary/insertPayload payloadinput)
  )
)


(defn getrequesting []
    (let [returnmap (recursiftion.dao_dictionary/getall)]
      ; (do (binding [*out* *err*]
      ;       (println "Goodbye, world!"))
      ;     returnmap
      ;   )
          returnmap
        
  ) 
)




;; this function adds a new word map object to the dictionary

(defn pushnewword [word pos defins synons]
  (let [wordinput word
        posinput pos
        definsinput defins
        synonsinput synons]
    (def dictionary
      (conj
        {wordinput  {
             posinput {
                      "defins" [definsinput]
                      "synons" [synonsinput]
                      "tally" [""]
                      }
                    }
        }
        dictionary ))
  )
)



;; this function is for a scenario when only a word string is offered
;; it conditionally either creates a new word object or updates an
;; existing word object

(defn updatenakedinput [word pos defins synons]

  (let [wordinput word
        posinput pos
        definsinput defins
        synonsinput synons
        wordobject (dictionary word)
        posobject ((dictionary word) pos)
        existingpos (first (keys (dictionary wordinput)))
        ]
    (def dictionary ;; BAD BAD BAD BAD BAD - should be maintaining immutability everywhere
      (conj  ;; if word exists and already has the same part of speech
        {
          wordinput  {;; if word exists but has no pos, then copy any existing definitions, synonems, and tallies into new word object that will replace the existing (naked) one

                        existingpos {
                          "defins" (if (= (first (((dictionary wordinput) existingpos) "defins")) "")
                                      [definsinput]
                                      (if (= definsinput "")
                                          (((dictionary wordinput) existingpos) "defins")
                                          (conj
                                            (((dictionary wordinput) existingpos) "defins")
                                            definsinput
                                          )
                                      )
                                    )
                          "synons" (if (= (first (((dictionary wordinput) existingpos) "synons")) "")
                                     [synonsinput]
                                     (if (= synonsinput "")
                                         (((dictionary wordinput) existingpos) "synons")
                                         (conj
                                            (((dictionary wordinput) existingpos) "synons")
                                            synonsinput
                                         )
                                     )
                                   )
                          "tally" (((dictionary wordinput) existingpos) "tally")
                        }
          }
        }
        (dissoc dictionary wordinput)
      )
    )
  )
)


;; this function is for a scenario when only a word string
;; and possibly other imputs are offered. It focuses on
;; updating an existing word object (specifically one that
;; was originall created without all properties populated)

(defn updatenakedword [word pos defins synons]

  (let [wordinput word
        posinput pos
        definsinput defins
        synonsinput synons
        wordobject (dictionary word)
        posobject ((dictionary word) pos)
        ]
    (def dictionary ;; BAD BAD BAD BAD BAD - should be maintaining immutability everywhere
      (conj  ;; if word exists and already has the same part of speech
        {
          wordinput  {;; if word exists but has no pos, then copy any existing definitions, synonems, and tallies into new word object that will replace the existing (naked) one

                        posinput {
                                  "defins" (if (= (first (((dictionary wordinput) "") "defins")) "")
                                              [definsinput]
                                              (if (= definsinput "")
                                                  (((dictionary wordinput) "") "defins")
                                                  (conj
                                                    (((dictionary wordinput) "") "defins")
                                                    definsinput
                                                  )
                                              )
                                            )
                                  "synons" (if (= (first (((dictionary wordinput) "") "synons")) "")
                                             [synonsinput]
                                             (if (= synonsinput "")
                                                 (((dictionary wordinput) "") "synons")
                                                 (conj
                                                    (((dictionary wordinput) "") "synons")
                                                    synonsinput
                                                 )
                                             )
                                           )
                                  "tally" (((dictionary wordinput) "") "tally")
                                }
          }
        }
        (dissoc dictionary wordinput)
      )
    )
  )
)



;; this function is for a scenario when only a pos string
;; and possibly other inputs are offered. It focuses on
;; updating an existing word object (specifically one that
;; does not currently have a part of speech (nested) object
;; associated with the one given as an argument)

(defn updatewordwithnewpos [word pos defins synons]

  (let [wordinput word
        posinput pos
        definsinput defins
        synonsinput synons
        wordobject (dictionary word)
        posobject ((dictionary word) pos)
        ]

    (def dictionary ;; BAD BAD BAD BAD BAD - should be maintaining immutability everywhere
      (conj  ;; if word exists and already has the same part of speech
        {wordinput
              (conj
                  { posinput {
                          "defins" [definsinput]
                          "synons" [synonsinput]
                          "tally" [""]
                          }
                  }
                  (dissoc wordobject posinput)
                )
        }
        (dissoc dictionary wordinput)
      )
    )
  )
)



;; this function is for a scenario when a pos string is not
;; offered. It focuses on updating an existing word object
;; (specifically one that currently has multiple parts of
;; speech objects

(defn guessposandupdate [word pos defins synons]

  (let [wordinput word
        posinput pos
        randompos (nth (keys (dictionary word))
                       (rand-int (count (dictionary word)))
                  )
        definsinput defins
        synonsinput synons
        wordobject (dictionary word)
        posobject ((dictionary word) randompos)
        ]

    (def dictionary ;; BAD BAD BAD BAD BAD - should be maintaining immutability everywhere
      (conj  ;; if word exists and already has the same part of speech
        {wordinput
              (conj
                  { randompos {
                              "defins" (if (= (first (posobject "defins")) "")
                                         [definsinput]
                                         (if (= definsinput "")
                                             (posobject "defins")
                                             (conj
                                                (posobject "defins")
                                                definsinput
                                             )
                                         )
                                       )
                              "synons" (if (= (first (posobject "synons")) "")
                                         [synonsinput]
                                         (if (= synonsinput "")
                                             (posobject "synons")
                                             (conj
                                               (posobject "synons")
                                               synonsinput
                                             )
                                         )
                                       )
                              "tally" (posobject "tally")
                              }
                  }
                  (dissoc wordobject randompos)
                )
        }
        (dissoc dictionary wordinput)
      )
    )
  )
)





;; this function is for a scenario when a word object
;; matches the word string and pos string input. It
;; appends the input sentences and synonems to the
;; correct place in that word object

(defn updatepopulatedword [word pos defins synons]

  (let [wordinput word
        posinput pos
        definsinput defins
        synonsinput synons
        wordobject (dictionary word)
        posobject ((dictionary word) pos)
        ]

    (def dictionary ;; BAD BAD BAD BAD BAD - should be maintaining immutability everywhere
      (conj  ;; if word exists and already has the same part of speech
        {wordinput
              (conj
                  { posinput {
                              "defins" (if (= (first (posobject "defins")) "")
                                         [definsinput]
                                         (if (= definsinput "")
                                             (posobject "defins")
                                             (conj
                                                (posobject "defins")
                                                definsinput
                                             )
                                         )
                                       )
                              "synons" (if (= (first (posobject "synons")) "")
                                         [synonsinput]
                                         (if (= synonsinput "")
                                             (posobject "synons")
                                             (conj
                                               (posobject "synons")
                                               synonsinput
                                             )
                                         )
                                       )
                              "tally" (posobject "tally")
                                      ; (if (= (first (posobject "tally")) "")
                                      ;    [definsinput]
                                      ;    (conj
                                      ;      (posobject "tally")
                                      ;      definsinput
                                      ;    )
                                      ;  )
                              }
                  }
                  (dissoc wordobject posinput)
                )
        }
        (dissoc dictionary wordinput)
      )
    )
  )
)



;; this function takes a word string as an argument
;; if the word exists in the dictionary then it returns
;; a word object; else it returns nil

(defn retrieveword [id]
  (let [wordstring id]
    (if (nil? (dictionary wordstring))
      nil
      (dictionary wordstring))
  )
)



;; this function receives arguments from the controller and routes
;; to the correct functions mentioned above in order to create or
;; update existing word objects accordingly

(defn acceptword [word pos defins synons]
  (let [wordinput word
        wordobject (retrieveword word)
        posinput pos
        definsinput (clojure.string/trimr
                       (apply str
                              (map #(str % " ")
                                   (re-seq #"[a-zA-Z]+" defins))))
        synonsinput synons]

    (if (nil? wordobject)
      (pushnewword wordinput posinput definsinput synonsinput) ;; means its a new word. just push it even if there are blank fields

      (cond ;; means the word  already exists. pick which scenario

          (and (= "" posinput) ;; means we are traversing sentence and don't know part of speach of the word we intend to inspect
               (> (count wordobject) 1)) ;; means the word has multiple parts of speech
            (guessposandupdate wordinput posinput definsinput synonsinput)


          (and (= "" posinput) ;; means we are traversing sentence and don't know part of speach of the word we intend to inspect
               (= (count wordobject) 1) ;; means the word has only one part of speech
               (nil? (wordobject ""))) ;; means that the word's only part of speech is not an empty string
            (updatenakedinput wordinput posinput definsinput synonsinput)


          (and (= "" posinput) ;; means we are traversing sentence and don't know part of speach of the word we intend to inspect
               (= (count wordobject) 1) ;; means the word has only one part of speech
               (not (nil? (wordobject "")))) ;; means that the word's only part of speech is an empty string
            (updatepopulatedword wordinput posinput definsinput synonsinput)


          (and (not= "" posinput) ;; means that the input includes a value for part of speech
               (= (count wordobject) 1) ;; means the word has only one part of speech
               (not (nil? (wordobject ""))));; means that the word's only part of speech is an empty string
            (updatenakedword wordinput posinput definsinput synonsinput)


          (and (not= "" posinput) ;; means that the input includes a value for part of speech
               (not (nil? (wordobject posinput))));; means that the pos already exists within the word object
            (updatepopulatedword wordinput posinput definsinput synonsinput)


          (and (not= "" posinput) ;; means that the input includes a value for part of speech
               (nil? (wordobject posinput)));; means that the pos does not exists within the word object
            (updatewordwithnewpos wordinput posinput definsinput synonsinput)

          :else (updatepopulatedword wordinput posinput definsinput synonsinput))
    )
  )
)




;; this function updates the tally of a word object
;; that may already has all of its properties populated
;; but it has multiple nested part of speech objects

(defn guessword-incrementtally [word sentence]

  (let [wordinput word
        sentenceinput sentence
        targetword (retrieveword word)
        randompos (nth (keys (dictionary word))
                       (rand-int (count (dictionary word)))
                  )
        posobject (targetword randompos)]

        (def dictionary ;; BAD BAD BAD BAD BAD - should be maintaining immutability everywhere
          (conj  ;; if word exists and already has the same part of speech
            {wordinput
                  (conj
                      { randompos {
                                  "defins" (posobject "defins")
                                  "synons" (posobject "synons")
                                  "tally" (if (= (first (posobject "tally")) "")
                                             [sentenceinput]
                                             (conj
                                                (posobject "tally")
                                                sentenceinput
                                             )
                                          )
                                  }
                      }
                      (dissoc targetword randompos)
                    )
            }
            (dissoc dictionary wordinput)
          )
        )
  )
)




;; this function updates the tally of a word object
;; that already has none of its properties populated

(defn nakedword-incrementtally [word sentence]

  (let [wordinput word
        sentenceinput sentence
        targetword (retrieveword word)
        existingpos (first (keys (dictionary word)))
        posobject (targetword existingpos)]

        (def dictionary ;; BAD BAD BAD BAD BAD - should be maintaining immutability everywhere
          (conj  ;; if word exists and already has the same part of speech
            {wordinput
                { existingpos {
                    "defins" (posobject "defins")
                    "synons" (posobject "synons")
                    "tally" (if (= (first (posobject "tally")) "")
                               [sentenceinput]
                               (conj
                                  (posobject "tally")
                                  sentenceinput
                               )
                            )
                    }
                }
            }
            (dissoc dictionary wordinput)
          )
        )
  )
)


;; this function updates the tally of a word object
;; that already has all of its properties populated

(defn populatedword-incrementtally [word sentence]

  (let [wordinput word
        sentenceinput sentence
        targetword (retrieveword word)
        existingpos (first (keys (dictionary word)))
        posobject (targetword existingpos)]

        (def dictionary ;; BAD BAD BAD BAD BAD - should be maintaining immutability everywhere
          (conj  ;; if word exists and already has the same part of speech
            {wordinput
                { existingpos {
                    "defins" (posobject "defins")
                    "synons" (posobject "synons")
                    "tally" (if (= (first (posobject "tally")) "")
                               [sentenceinput]
                               (conj
                                  (posobject "tally")
                                  sentenceinput
                               )
                            )
                    }
                }
            }
            (dissoc dictionary wordinput)
          )
        )
  )
)


;; After a definition sentence traversal has engaged, this function
;; analyzes what catefory a particular word falls into. it then routes
;; that word to the approproate function to update its tally counter

(defn analyzetargetword [word sentence]

  (let [wordinput word
        sentenceinput sentence
        targetword (retrieveword word)]

    (cond

        (> (count targetword) 1) ;; means the word has multiple parts of speech
          (guessword-incrementtally wordinput sentenceinput)


        (and (= (count targetword) 1) ;; means the word has only one part of speech
             (nil? (targetword ""))) ;; means that the word's only part of speech is not an empty string
          (nakedword-incrementtally wordinput sentenceinput)


        (and (= (count targetword) 1) ;; means the word has only one part of speech
             (not (nil? (targetword "")))) ;; means that the word's only part of speech is an empty string
          (populatedword-incrementtally wordinput sentenceinput)
    )

  )
)


;; this function establishes whether the wordobject already exists
;; when a definition sentence is being traversed. if not, it creates
;; the word without complementary properties. if so, then it further
;; triggers the mechanism/function to increase the word's tally property

(defn routetotallyword [word sentence]

  (let [wordinput word
        sentenceinput sentence
        targetword (dictionary word)]

    (if (nil? (retrieveword wordinput))
        (do (acceptword wordinput "" "" "")
            (analyzetargetword wordinput sentenceinput)
        )
        (analyzetargetword wordinput sentenceinput)
    )
  )
)



;; this function is triggered after a word is added to the dictionary
;; it traverses each word found in the submitted definition sentence
;; and it increases each word object's tally property

(defn traversesentence [defins] ;; argument is a string

  (let [trimmedsentence (clojure.string/trimr
                          (apply str
                             (map #(str % " ")
                                (re-seq #"[a-zA-Z]+" defins))))
        sentencearray (clojure.string/split trimmedsentence #"\s")
        justforkicks defins]

    (dotimes [n (count sentencearray)]
        (routetotallyword (sentencearray n) trimmedsentence))
  )
)



;; this function maps over each sentence in a word object's tally
;; and establishes a collection of counts. It then reduces in order to
;; provide a tally of how many times each sentence size/quantity occurs

(defn getsentencereport [word pos]
  (let [wordstring word
        posstring pos
        wordobject (dictionary word)
        posobject ((dictionary word) pos)
        tallyarray (posobject "tally")
        a (map
           #(count (clojure.string/split % #"\s"))
           tallyarray
        )
        b (map
             #(count
                   (filter (fn [x] (= x %))
                    a
                 )
              )
           (set a))
        ]

    {
     "tallies" (zipmap (set a) b)
     "min" (apply min a)
     "max" (apply max a)
     "mean" (int (/ (apply + a) (count a)))
     }
  )
)



;; this is a helper fucntion for the getwordpositionreport function
;; below. It checks how many times a word is found in a sentence
;; and it returns an array of the indices where it is found

(defn sentenceoccurances [n word]

  (def sentence n)
  (def tally [])

  (while (not= (.indexOf sentence word) -1)

    (def tally (conj tally
                     (.indexOf sentence word)))

    (def sentence (subvec sentence (inc (.indexOf sentence word))))
    )
  tally
)



;; this function maps over each sentence in a word object's tally
;; and establishes a collection of index positions where the word 1
;; string argument is found. It then flattens and reduces in order to
;; provide a tally of occurances at each index positions

(defn getwordpositionreport [word pos]
  (let [wordstring word
        posstring pos
        wordobject (dictionary word)
        posobject ((dictionary word) pos)
        tallyarray (posobject "tally")
        a (flatten
             (map #(sentenceoccurances (clojure.string/split % #"\s") wordstring)
                  tallyarray
             )
          )
        b (map
             #(count
                   (filter (fn [x] (= x %))
                    a
                 )
              )
           (set a))
        ]

    (println a)
    (println b)
    (println (zipmap (set a) b))

    {
     "tallies" (zipmap (set a) b)
     "min" (apply min a)
     "max" (apply max a)
     "mean" (int (/ (apply + a) (count a)))
     }
  )
)



;; this future function will traverse the word object's synonem
;; array and provide tallies to map reduce upon (just like we
;; do with definition sentences above)

; (defn traversesynonems [defins] ;; argument is a string

;   (let [synonem (clojure.string/split defins #"\s")]

;     (dotimes [n (count sentencearray)]
;         (routetotallyword (synonem n) defins))
;   )
; )


