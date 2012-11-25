
# jenko

Some functions for talking to Jenkins via Clojure.

## Usage

```clojure
(ns myproject
  (:require [jenkins.core :as j]))

; count the number of failing jobs

(count (j/failing-jobs))

; use specific connection information, and fetch
; full job info for all the failing jobs.

(def cnn {:url "http://localhost"
          :user "myuser"
          :token "sometokenwhichwillbelong"})
          
(j/with-jenkins cnn
  (->> (j/jobs)
       (filter j/is-failing)
       (map j/job-info)))

; the user and token are optional
```
