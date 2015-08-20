(set-env!
  :source-paths   #{"src"}
  :resource-paths #{"resources/public"}
  :dependencies '[
    [adzerk/boot-cljs            "1.7.48-1"      :scope "test"]
    [adzerk/boot-cljs-repl       "0.1.10-SNAPSHOT" :scope "test"]
    [adzerk/boot-reload          "0.3.1"           :scope "test"]
    [tailrecursion/boot-hoplon   "0.1.1"           :scope "test"]
    [crisptrutski/boot-cljs-test "0.1.0-SNAPSHOT"  :scope "test"]
    [org.clojure/clojure         "1.7.0"]
    [org.clojure/clojurescript   "1.7.58"]
    [tailrecursion/hoplon "6.0.0-alpha5"]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
  '[adzerk.boot-reload    :refer [reload]]
  '[crisptrutski.boot-cljs-test  :refer [test-cljs]]
  '[tailrecursion.boot-hoplon :refer :all])

(deftask auto-test []
  (set-env! :source-paths #{"src" "test"})
  (comp (watch)
        (speak)
        (test-cljs)))

(deftask client []
  (set-env! :source-paths #{"src"})
  (comp (watch)
        (speak)
        (reload :on-jsload 'gnar.client.core/main)
        (cljs-repl)
        (hoplon :pretty-print true
                :source-map true
                :optimizations :whitespace)))

(deftask build-client-for-production []
  (set-env! :source-paths #{"src/gnar/client"})
  (comp (hoplon :optimizations :advanced)))

(deftask server []
  )

(deftask build-dev-server []
  (set-env! :source-paths #{"src/boot_with_node/"})
  (cljs :optimizations :none
        :compiler-options {:target :nodejs}))


(deftask watch-server []
  (comp (watch)
        (speak)
        (build-dev-server)))
