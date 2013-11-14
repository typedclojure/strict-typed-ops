(ns strict-typed-ops.strict
  (:refer-clojure :exclude [vector hash-map set])
  (:require [clojure.core.typed :as t]))

(defmacro vector>
  "Make a vector of element type t. During type checking, arguments must be of type t."
  [t & args]
  `(t/ann-form (clojure.core/vector ~@args) (t/Vec ~t)))

(defmacro hash-map>
  "Make a hash map with keys type ks and vals type vs. During type checking,
  argument keys and vals must be of type ks and vs respectively."
  [ks vs & args]
  `(t/ann-form (clojure.core/hash-map ~@args) (t/Map ~ks ~vs)))

(defmacro hash-set>
  "Make a hash set of element type t. During type checking, arguments must be
  of type t."
  [t & args]
  `(t/ann-form (clojure.core/hash-set ~@args) (t/Set ~t)))

(defmacro sorted-set>
  "Make a sorted set of element type t. During type checking, arguments must be
  of type t."
  [t & args]
  `(t/ann-form (clojure.core/sorted-set ~@args) (t/SortedSet ~t)))

(t/ann ^:no-check conj-vec
     (All [x
           [y :< x]]
          [(t/Vec x) y y * -> (t/Vec x)]))
(defn conj-vec 
  "conj onto a vector. During type checking, the candiates to conj must have the
  same type as the target element type."
  [target arg & args]
  (apply conj target arg args))

(t/ann ^:no-check conj-map
     (All [k v
           [k1 :< k]
           [v1 :< v]]
          [(t/Map k v) (U nil '[k1 v1]) (U nil '[k1 v1]) * -> (t/Map k v)]))
(defn conj-map 
  "conj onto a map. During type checking, the arguments to conj must have the
  same type as the target element type."
  [target arg & args]
  (apply conj target arg args))

(t/ann ^:no-check conj-set
       (All [x
             [y :< x]]
            [(t/Set x) y y * -> (t/Set x)]))
(defn conj-set 
  "conj onto a set During type checking, the arguments to conj must have the
  same type as the target element type."
  [target arg & args]
  (apply conj target arg args))

(t/ann ^:no-check into-map
     (All [k v
           [k1 :< k]
           [v1 :< v]]
          [(t/Map k v) (U nil (t/Seqable '[k1 v1])) -> (t/Map k v)]))
(defn into-map 
  "Add items from a collection into a map. During type checking, the second argument must have the
  same type element type as the target element type."
  [target arg]
  (into target arg))

(t/ann ^:no-check into-set
     (All [x
           [y :< x]]
          [(t/Set x) (U nil (t/Seqable y)) -> (t/Set x)]))
(defn into-set 
  "Add items from a collection into a set. During type checking, the second argument must have the
  same type element type as the target element type."
  [target arg]
  (into target arg))

(t/ann ^:no-check into-sorted-set
     (All [x
           [y :< x]]
          [(t/SortedSet x) (U nil (t/Seqable y)) -> (t/SortedSet x)]))
(defn into-sorted-set 
  "Add items from a collection into a sorted set. During type checking, the second argument must have the
  same type element type as the target element type."
  [target arg]
  (into target arg))


(comment 
  ; adding '[Number Boolean] entries allowed
  (t/cf
    (-> (hash-map> Number Boolean)
        (conj-map [1 true])
        (into-map [[1 true] [2 false]])))

  ; can't add '[Number Number] entry
  (t/cf
    (-> (hash-map> Number Boolean)
        (conj-map [1 2])))

  ; can't add '[Number Number] entry
  (t/cf
    (-> (hash-map> Number Boolean)
        (into-map [[1 2]])))

  ; can add Numbers to a (Set Number)
  (t/cf (into-set (hash-set> Number) [1 2 3 4 5]))

  ; can add Numbers to a (SortedSet Number)
  (t/cf (into-sorted-set (sorted-set> Number) [1 2 3 4 5]))

  ; can't add (U Long Double) to (SortedSet Long)
  (t/cf (into-sorted-set (sorted-set> Long) [1 2 2.2]))
  )
