(ns build
  (:require [clojure.tools.build.api :as b]))

(def build-directory "target")
(def jar-content (str build-directory "/resources"))

(def basis (b/create-basis {:project "deps.edn"}))
(def version "1.0.0")
(def app-name "mail-harvester")
(def uber-file-name (format "%s/%s-%s-standalone.jar" build-directory app-name version))

(defn clean [_]
  (b/delete {:path build-directory})
  (println (format "Build directory \"%s\" removed" build-directory)))

(defn uber [_]
  (clean nil)
  (b/copy-file {:src "README.md"
                :target-dir build-directory})

  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir jar-content})

  (b/uber {:class-dir jar-content
           :uber-file uber-file-name
           :basis basis
           :main 'mail-harvester.core})

  (println (format "Uber file created: \"%s\"" uber-file-name)))
