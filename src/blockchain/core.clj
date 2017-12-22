(ns blockchain.core
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]))

(s/def :bc/sha (s/and string?
                      #(re-matches #"[A-Fa-f0-9]+" %)))

(s/def :bc/sender :bc/sha)
(s/def :bc/recipient :bc/sha)
(s/def :bc/amount number?)
(s/def :bc/proof pos-int?)
(s/def :bc/prev-hash :bc/sha)

(s/def :bc/transaction (s/keys :req
                               [:bc/sender
                                :bc/recipient
                                :bc/amount]))
(s/def :bc/transactions (s/coll-of :bc/transaction
                                   :kind vector?))

(s/def :bc/index nat-int?)
(s/def :bc/timestamp inst?)
(s/def :bc/proof pos-int?)
(s/def :bc/prev-hash :bc/sha)

(s/def :bc/block (s/keys :req
                         [:bc/index
                          :bc/timestamp
                          :bc/transactions
                          :bc/proof
                          :bc/prev-hash]))

(s/def :bc/chain (s/coll-of :bc/block :kind vector?))

(s/def :bc/bc (s/keys :req [:bc/transactions
                            :bc/chain]))

(def genesis {:bc/prev-hash "1"
              :bc/proof 100})

(defn now [] (new java.util.Date))

(defn sha-hash [x]
  x)

(s/fdef add-block
        :args (s/cat :bc :bc/bc
                     :proof :bc/proof
                     :prev-hash :bc/prev-hash)
        :ret :bc/bc)
(defn add-block [bc proof prev-hash]
  (update bc :bc/chain conj
          {:bc/index (+ (count (:bc/chain bc)) 1)
           :bc/timestamp (now)
           :bc/transactions (:bc/transactions bc)
           :bc/proof proof
           :bc/prev-hash (or prev-hash (sha-hash (last (:bc/chain bc))))
           })
  )

(s/fdef blockchain
        :ret :bc/bc)
(defn blockchain []
  (add-block
   {:bc/transactions []
    :bc/chain []}
   (:bc/proof genesis)
   (:bc/prev-hash genesis)
   )
  )

(comment
  (blockchain)
  )

(s/fdef add-tx
        :args (s/cat :bc :bc/bc
                     :sender :bc/sender
                     :recipient :bc/recipient
                     :amount :bc/amount)
        :ret :bc/bc)
(defn add-tx [bc sender recipient amount]
  (update bc
          :bc/transactions
          conj
          {:bc/sender sender
           :bc/recipient recipient
           :bc/amount amount}))

(s/fdef next-idx
        :args (s/cat :bc :bc/bc)
        :ret :bc/index)
(defn next-idx [bc]
  (-> bc
      :bc/chain
      last
      :bc/indix
      (or 0)))

(comment
  (add-tx {:bc/chain []
           :bc/transactions []} "abc" "def" 5))

(comment (require '[orchestra.spec.test :as st])
         (set! s/*explain-out* expound/printer)
         (st/instrument))





