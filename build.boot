#!/usr/bin/env boot

#tailrecursion.boot.core/version "2.5.0"

(set-env!
  :project 'gnar
  :version "0.1.0-SNAPSHOT"
  :main-class 'gnar.core
  :dependencies (read-string (slurp "deps.edn"))
  :out-path     "resources/public"
  :src-paths    #{"src/hoplon" "src/castra" "src/cljs"})

;; Static assets
(add-sync! (get-env :out-path) #{"src/static"})

(require
 '[tailrecursion.hoplon.boot :refer :all]
 '[tailrecursion.castra.task :refer [castra-dev-server]])

(deftask heroku
  "Prepare project.clj and Procfile for Heroku deployment."
  [& [main-class]]
  (let [jar-name   (format "%s-standalone.jar" (get-env :project))
        jar-path   (format "target/%s" jar-name)
        main-class (or main-class (get-env :main-class))]
    (set-env!
      :src-paths #{"resources"}
      :lein      {:min-lein-version "2.0.0"
                  :uberjar-name     jar-name
                  :plugins          '[[lein-environ "1.0.0"]]
                  :profiles         {:production {:env {:production true}}}})
    (comp
      (lein-generate)
      (with-pre-wrap
        (-> "project.clj" slurp
          (.replaceAll "(:min-lein-version)\\s+(\"[0-9.]+\")" "$1 $2")
          ((partial spit "project.clj")))
        (-> "web: java $JVM_OPTS -cp %s clojure.main -m %s $PORT"
          (format jar-path main-class)
          ((partial spit "Procfile")))))))

(deftask development
  "Start local dev server."
  []
  (comp
    (castra-dev-server 'gnar.api.gnar)
    (watch)
    (hoplon {:prerender false})))

(deftask production
  "Compile application with Google Closure advanced optimizations."
  []
  (hoplon {:optimizations :whitespace}))
