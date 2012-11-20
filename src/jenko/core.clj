
(ns jenko.core
  (:import java.io.ByteArrayInputStream)
  (:require [clojure.string :as string]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [clojure.xml :as xml]))

(def env #(System/getenv %))

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
  (= "red" (:color job)))

(defn matches-job
  "Returns true if predicate matches job"
  [predicate job]
  (let [a (string/lower-case predicate)
        b (string/lower-case (:name job))]
  (boolean (= a b))))

;; Public
;; ------

(defn jobs []
  (:jobs (fetch-json "/api/json")))

(defn job 
  "Fetch information about a single job"
  [job-name]
  (->> (jobs)
       (filter (partial matches-job job-name))
       first))

(def job-url (comp :url job))
 
(defn failing-jobs []
  (->> (jobs)
       (filter is-failing)))

(defn job-config [job-name]
  (fetch-xml (format "/job/%s/config.xml" job-name)))

(defn copy-job
  ([from to] (copy-job from to identity))
  ([from to mutator]
	(xml/emit
      (->> (job-config "scotam-webapp-master")
           (mutator)))))
