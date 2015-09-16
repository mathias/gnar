(set-env!
  :source-paths   #{"src"}
  :resource-paths #{"resources/public"}
  :dependencies '[
    ;; boot dependencies
    [adzerk/boot-cljs            "1.7.48-3"        :scope "test"]
    [adzerk/boot-cljs-repl       "0.1.10-SNAPSHOT" :scope "test"]
    [adzerk/boot-test            "1.0.4"           :scope "test"]
    [boot-deps                   "0.1.6"           :scope "test"]
    [crisptrutski/boot-cljs-test "0.1.0-SNAPSHOT"  :scope "test"]
    [boot-environ       "1.0.1"           :scope "test"]
    [tailrecursion/boot-hoplon   "0.1.4"           :scope "test"]
    ;; project dependencies
    [environ "1.0.1"]
    [http-kit "2.1.19"]
    [korma "0.4.2"]
    [liberator "0.13"]
    [org.clojure/clojure         "1.7.0"]
    [org.clojure/clojurescript   "1.7.122"]
    [org.danielsz/system "0.1.9"]
    [org.clojure/java.jdbc "0.3.7"]
    [org.clojure/tools.nrepl "0.2.10"]
    [playnice "1.0.1"]
    [postgresql "9.3-1102.jdbc41"]
    [ring/ring-core "1.4.0"]
    [ring/ring-defaults "0.1.5"]
    [ring/ring-mock "0.3.0"]
    [ring-logger "0.7.1"]
    [tailrecursion/hoplon "6.0.0-alpha7"]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
  '[boot-deps :refer [ancient]]
  '[crisptrutski.boot-cljs-test :refer [test-cljs]]
  '[environ.boot :refer [environ]]
  '[gnar.server.systems :refer :all]
  '[reloaded.repl :as repl :refer [start stop go reset]]
  '[system.boot :refer [system run]]
  '[tailrecursion.boot-hoplon :refer :all]
  '[adzerk.boot-test :refer :all])

(deftask build-dev []
  (set-env! :source-paths #{"src"})
  (comp
   (hoplon :pretty-print true
           :source-map true
           :optimizations :whitespace)
   (cljs :optimizations :none
         :source-map true)))

(deftask dev-server
  "Run a restartable system in the REPL"
  []
  (comp
   (environ :env {:http-port "3000"
                  :database-url "postgres://localhost:5432/gnar_development"})
   (watch :verbose true)
   (system :sys #'dev-system
           :hot-reload true
           :auto-start true
           :files ["handler.clj"])
   (repl :server true)))

(deftask autotest
  "Run server tests"
  []
  (set-env! :source-paths #{"test" "src"})
  (comp
   (watch :verbose true)
   (environ :env {:database-url "postgres://localhost:5432/gnar_test"})
   (system :sys #'test-system
           :hot-reload true
           :auto-start true)
   (test)))
