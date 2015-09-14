(set-env!
  :source-paths   #{"src"}
  :resource-paths #{"resources/public"}
  :dependencies '[
    [adzerk/boot-cljs            "1.7.48-3"        :scope "test"]
    [adzerk/boot-cljs-repl       "0.1.10-SNAPSHOT" :scope "test"]
    [boot-deps                   "0.1.6"           :scope "test"]
    [crisptrutski/boot-cljs-test "0.1.0-SNAPSHOT"  :scope "test"]
    [danielsz/boot-environ       "0.0.5"           :scope "test"]
    [tailrecursion/boot-hoplon   "0.1.4"           :scope "test"]
    ;; project dependencies
    [bidi "1.21.0"]
    [environ "1.0.1"]
    [http-kit "2.1.19"]
    [liberator "0.13"]
    [org.clojure/clojure         "1.7.0"]
    [org.clojure/clojurescript   "1.7.122"]
    [org.danielsz/system "0.1.9"]
    [org.clojure/java.jdbc "0.4.1"]
    [org.clojure/tools.nrepl "0.2.10"]
    [postgresql "9.3-1102.jdbc41"]
    [ring/ring-core "1.4.0"]
    [ring/ring-defaults "0.1.5"]
    [tailrecursion/hoplon "6.0.0-alpha7"]
    [yesql "0.4.2"]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
  '[boot-deps :refer [ancient]]
  '[crisptrutski.boot-cljs-test :refer [test-cljs]]
  '[danielsz.boot-environ :refer [environ]]
  '[gnar.server.systems :refer [dev-system]]
  '[reloaded.repl :as repl :refer [start stop go reset]]
  '[system.boot :refer [system run]]
  '[tailrecursion.boot-hoplon :refer :all])

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
   (environ :env {:http-port 3000
                  :database-url "postgres://localhost:5432/gnar_development"})
   (watch :verbose true)
   (system :sys #'dev-system
           :hot-reload true
           :auto-start true
           :files ["handler.clj"])
   (repl :server true)))
