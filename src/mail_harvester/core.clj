(ns mail-harvester.core
  (:gen-class)
  (:require [cljfx.api :as fx]
            [cljfx.css :as css]
            [mail-harvester.scraper :as scraper]
            [clojure.core.async :refer [thread]])
  (:import [javafx.application Platform]))

;; The main, mutable state for the app
(def *state
  (atom {:status "Not in use"
         :url ""
         :browser "Chrome"}))

(def style
  (css/register ::style
                (let [background-color "#333"
                      foreground-color "#ececec"
                      elements-color "#444"
                      elements-hover-color "#555"]
                  {".root" {:-fx-background-color background-color
                            :-fx-accent elements-hover-color
                            :-fx-fill foreground-color
                            :-fx-control-inner-background "derive(-fx-base, 35%)"
                            :-fx-control-inner-background-alt "-fx-control-inner-background"
                            :-fx-font-family "Arial"
                            :-fx-font-weight "300"}
                   ".root .text" {:-fx-fill foreground-color}
                   ".text-field" {:-fx-background-color elements-color
                                  :-fx-text-fill foreground-color}
                   ".button" {:-fx-background-color elements-color}
                   ".button:hover" {:-fx-background-color elements-hover-color}
                   ".choice-box" {:-fx-background-color elements-color
                                  :-fx-mark-color foreground-color}
                   ".choice-box .context-menu" {:-fx-background-color elements-color}})))

;; Separate widgets so that they are easier to read.

(defn url-field
  "The URL field for the app"
  [{}]
  {:fx/type :h-box
   :spacing 5
   :alignment :center
   :children [{:fx/type :label
               :text "URL: "}
              {:fx/type :text-field
               :on-text-changed #(swap! *state assoc :url %)}]})

(defn browser-picker
  "The browser picker menu for the app"
  [{}]
  {:fx/type :h-box
   :spacing 5
   :alignment :center
   :children [{:fx/type :label
               :text "Browser: "}
              {:fx/type :choice-box
               :items ["Chrome" "Firefox" "Safari"]
               :value "Chrome"
               :on-value-changed (fn [value]
                                   (swap! *state assoc :browser value))}]})

(defn scrape-emails-button
  "The 'Scrape URL for emails' button for the app"
  [{}]
  {:fx/type :button
   :on-action (fn [_]
                ;; Let the user know that scraping is going on
                (swap! *state assoc :status "Scraping")
                ;; Run the scraper function in another thread to prevent locking the UI
                (thread (try
                          (let [res (scraper/scrape-url (-> @*state :url)
                                                        (-> @*state :browser)
                                                        "emails")]
                            ;; Export it to a CSV
                            (scraper/write-to-exports res "emails")
                            ;; ...and let the user know that we have exported it
                            (swap! *state assoc :status "Scraping Done!"))
                          (catch Exception e
                            ;; Write an error log
                            (spit "error.txt" e)
                            ;; And tell the user an error occured
                            (swap! *state assoc :status "Error! Please file an issue on GitHub")))))
   :text "Scrape URL for emails"})

(defn scrape-links-button
  "The 'Scrape URL for links' button for the app"
  [{}]
  {:fx/type :button
   :on-action (fn [_]
                ;; Let the user know that scraping is going on
                (swap! *state assoc :status "Scraping")
                ;; Run the scraper function in another thread to prevent locking the UI
                (thread (try
                          (let [res (scraper/scrape-url (-> @*state :url)
                                                        (-> @*state :browser)
                                                        "links")]
                            ;; Export it to a CSV
                            (scraper/write-to-exports res "links")
                            ;; ..and let the user know that we have exported it
                            (swap! *state assoc :status "Scraping Done!"))
                          (catch Exception e
                            ;; Write an error log
                            (println e)
                            ;; And tell the user an error occured
                            (swap! *state assoc :status "Error! Please file an issue on GitHub")))))
   :text "Scrape URL for links"})

(defn root
  "The root app that glues all the components together"
  [{:keys [status]}]
  {:fx/type :stage
   :showing true
   :title "Mail Harvester"
   :scene {:fx/type :scene
           :stylesheets [(::css/url style)]
           :root {:fx/type :v-box
                  :padding 15
                  :alignment :center
                  :spacing 5
                  :children [{:fx/type url-field}
                             {:fx/type browser-picker}
                             {:fx/type :label
                              :text "Choose the action you would like to perform"}
                             {:fx/type :v-box
                              :alignment :center
                              :spacing 5
                              :children [{:fx/type scrape-links-button}
                                         {:fx/type scrape-emails-button}]}
                             {:fx/type :label
                              :text (str "Status: " status)}
                             {:fx/type :label
                              :text "Visit the documentation for details on usage"}]}}})

;; A renderer that constantly checks the state and reloads if anything changes in the state
(def renderer (fx/create-renderer
               :middleware (fx/wrap-map-desc assoc :fx/type root)))

(defn -main
  "The entry point for the app"
  [& args]
  (Platform/setImplicitExit true) ;; Exits when the main window closes
  (fx/mount-renderer *state renderer))
