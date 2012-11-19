
# jenko

Some functions for talking to Jenkins via Clojure.

## Usage

```
(ns myproject
  (:require [jenkins.core :as j]))

; count the number of failing jobs
(count (j/failing-jobs))
```
