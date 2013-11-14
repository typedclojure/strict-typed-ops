(ns strict-typed-ops.strict
  (:refer-clojure :exclude [vector hash-map set])
  (:require [clojure.core.typed :as t]))

(defmacro vector>
  [t & args]
  `(t/ann-form (clojure.core/vector ~@args) (t/Vec ~t)))

(defmacro hash-map>
  [ks vs & args]
  `(t/ann-form (clojure.core/hash-map ~@args) (t/Map ~ks ~vs)))

(defmacro hash-set>
  [t & args]
  `(t/ann-form (clojure.core/hash-set ~@args) (t/Set ~t)))

(defmacro sorted-set>
  [t & args]
  `(t/ann-form (clojure.core/sorted-set ~@args) (t/SortedSet ~t)))

(t/ann ^:no-check conj-vec
     (All [x
           [y :< x]]
          [(t/Vec x) y y * -> (t/Vec x)]))
(defn conj-vec [target arg & args]
  (apply conj target arg args))

(t/ann ^:no-check conj-map
     (All [k v
           [k1 :< k]
           [v1 :< v]]
          [(t/Map k v) (U nil '[k1 v1]) (U nil '[k1 v1]) * -> (t/Map k v)]))
(defn conj-map [target arg & args]
  (apply conj target arg args))

(t/ann ^:no-check conj-set
       (All [x
             [y :< x]]
            [(t/Set x) y y * -> (t/Set x)]))
(defn conj-set [target arg & args]
  (apply conj target arg args))

(t/ann ^:no-check into-map
     (All [k v
           [k1 :< k]
           [v1 :< v]]
          [(t/Map k v) (U nil (t/Seqable '[k1 v1])) -> (t/Map k v)]))
(defn into-map [target arg]
  (into target arg))

(t/ann ^:no-check into-set
     (All [x
           [y :< x]]
          [(t/Set x) (U nil (t/Seqable y)) -> (t/Set x)]))
(defn into-set [target arg]
  (into target arg))

(t/ann ^:no-check into-sorted-set
     (All [x
           [y :< x]]
          [(t/SortedSet x) (U nil (t/Seqable y)) -> (t/SortedSet x)]))
(defn into-sorted-set [target arg]
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

