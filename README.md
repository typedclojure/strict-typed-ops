# strict-typed-ops

<a href='http://www.typedclojure.org'><img href='images/part-of-typed-clojure-project.png'></a>

Stricter type operations for Typed Clojure. Inspired by Scala's collections.

## Dependency

Available on [Clojars](https://clojars.org/org.typedclojure/strict-typed-ops).

Leiningen:

`[strict-typed-ops "0.1.0"]`

## API

[API](http://typedclojure.github.io/strict-typed-ops/)

## Usage

`strict-typed-ops.strict` contains a suite of typed wrappers for common collection operations.

```clojure
(require '[strict-typed-ops.strict :refer :all]
         '[clojure.core.typed :as t]
         '[clojure.core :as core])
```

The twist: we use core.typed to make them statically stricter.

For example, you can only add `Number`s to a `(Set Number)`. 

As `(hash-set)` is a
`(Set Nothing)`, (entirely useless to add element to: only `Nothing` can be added!)
we also define convenience wrappers for constructors.

```clojure
(t/cf (hash-set> Number))
;=> (t/Set Number)
```

We can then `conj` in a more restricted way:

```clojure
(-> (hash-set> Number)
    (conj-set 1 2 3 4 5))
;=> (Set Number)
```

Using `clojure.core/conj` is perfectly valid, but less restrictive.

```clojure
(t/cf (-> (hash-set> Number)
          (core/conj false 1)))
;=> (Set (U Boolean Number))

```

`conj-set` does not allow this:

```clojure
(t/cf (-> (hash-set> Number)
          (conj-set false 1)))
;=> 
Type Error (strict-typed-ops.strict:2:11) Polymorphic function strict-typed-ops.strict/conj-set could not be applied to arguments:
Polymorphic Variables:
        x16677210
        y16687211 :< x1667

Domains:
        (t/Set x1667) y1668 y1668 *

Arguments:
        (t/Set java.lang.Number) false (Value 1)

Ranges:
        (t/Set x1667)

in: (strict-typed-ops.strict/conj-set (clojure.core.typed/ann-form* (clojure.core/hash-set) (quote (clojure.core.typed/Set Number))) false 1)
ExceptionInfo Type Checker: Found 1 error  clojure.core/ex-info (core.clj:4327)
```

Type errors will be pretty harsh: it's very difficult to give useful information about
polymorphic type errors. Luckily, thanks to local type inference that Typed Clojure
employs, the errors will be localised, often to a specific function call.

## Contributing

Requires a [Clojure CA](http://clojure.org/contributing). Pull requests for substantial
changes welcome.

Please open an issue for small fixes.

## License

Copyright © 2013 Ambrose Bonnaire-Sergeant

Distributed under the Eclipse Public License, the same as Clojure.
