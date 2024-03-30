(ns mail-harvester.scraper
  (:require [etaoin.api :as e]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [clojure.string :as string])
  (:import [java.util UUID]))

(defn fetch-emails
  "Fetches the mailto: links in a website, removes the mailto: prefix and returns a list of emails"
  [driver filters]
  (for [rawemail (e/query-all driver {:css "a[href^=\"mailto:\"]"})]
    (let [email (string/replace (e/get-element-attr-el driver rawemail :href)
                                #"mailto:"
                                "")]
      (if (empty? filters)
        email
        (if (= (.contains filters email) false)
          email)))))

(defn fetch-links
  "Fetches any links in a website and returns a list of links"
  [driver filters]
  (for [rawlink (e/query-all driver {:css "a"})]
    ;; Return the raw link from the href attribute
    (let [link (e/get-element-attr-el driver rawlink :href)]
      (if (empty? filters)
        link
        (if (= (.contains filters link) false)
          link)))))

(defn scrape-url
  "Scrapes from the URL, with information on which browser to use and what action to perform"
  [url browser filters action]
  (cond
    (= browser "Firefox") (let [driver (e/firefox-headless {:path-driver "./drivers/geckodriver"})]
                            (e/go driver url)
                            (cond (= action "emails") (fetch-emails driver filters)
                                  (= action "links") (fetch-links driver filters)
                                  :else (println (format "Unknown action \"%s\" ignored" action))))
    (= browser "Chrome") (let [driver (e/chrome-headless {:path-driver "./drivers/chromedriver"})]
                           (e/go driver url)
                           (cond (= action "emails") (fetch-emails driver filters)
                                 (= action "links") (fetch-links driver filters)
                                 :else (println (format "Unknown action \"%s\" ignored" action))))
    (= browser "Safari") (let [driver (e/safari)]
                           (e/go driver url)
                           (cond (= action "emails") (fetch-emails driver filters)
                                 (= action "links") (fetch-links driver filters)
                                 :else (println (format "Unknown action \"%s\" ignored" action))))
    :else (throw (Exception. (str "Browser is not valid: " browser)))))

(defn write-to-exports
  "Exports a list of emails or links to a CSV file with a random UUID and a prefix"
  [links type]
  ;; Open a writer
  (with-open [writer (io/writer (str type "-" (.toString (UUID/randomUUID)) ".csv"))]
    ;; Write the data to it. It will handle closing it after it's not in use
    (csv/write-csv writer
                   (map (fn [link]
                          (vector link)) links))))
