
(ns jenko.core
  (:import java.io.ByteArrayInputStream)
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [clojure.xml :as xml]))

(def color-fail "red")

(defn env [var]
  (System/getenv var))

(def JENKINS_URL (env "JENKINS_URL"))
(def JENKINS_USER (env "JENKINS_USER"))
(def JENKINS_TOKEN (env "JENKINS_TOKEN"))

(defn parse-string [str]
  (json/parse-string str true))

(defn fetch [url]
  (client/get
	(format "%s%s" JENKINS_URL url)
	{:basic-auth [JENKINS_USER JENKINS_TOKEN]}))

(def fetch-body
  (comp :body fetch))

(def fetch-json
  (comp parse-string fetch-body))

(defn string2stream [string]
  (ByteArrayInputStream.
    (.getBytes (.trim string))))

(def fetch-xml
  (comp xml/parse
        string2stream
        fetch-body))

(defn is-failing [job]
  (= color-fail (:color job)))

;; Public
;; ------

(defn jobs []
  (:jobs (fetch-json "/api/json")))

(defn failing-jobs []
  (->> (jobs)
       (filter is-failing)))

(defn job-config [job-name]
  (fetch-xml (format "/job/%s/config.xml" job-name)))
