
(ns jenko.core
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))

(def color-fail "red")

(defn env [var]
  (System/getenv var))

(def JENKINS_URL (env "JENKINS_URL"))
(def JENKINS_USER (env "JENKINS_USER"))
(def JENKINS_TOKEN (env "JENKINS_TOKEN"))

(defn parse-string [str]
  (json/parse-string str true))

(defn fetch [url]
  (let [res (client/get
		      (format "%s%s" JENKINS_URL url)
		    	{:basic-auth [JENKINS_USER JENKINS_TOKEN]})]
    (parse-string (:body res))))

(defn jobs []
  (:jobs (fetch "/api/json")))

(defn is-failing [job]
  (= color-fail (:color job)))

(defn failing-jobs []
  (->> (jobs)
       (filter is-failing)))
