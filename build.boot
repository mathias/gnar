(set-env! :dependencies '[[adzerk/bootlaces "0.1.11" :scope "test"]
                          [adzerk/boot-cljs "0.0-2814-3" :scope "test"]
                          [boot-deps "0.1.4"]])

(require '[adzerk.bootlaces :refer :all]
         '[adzerk.boot-cljs :refer :all]
         '[boot-deps :refer [ancient]])

(def +version+ "0.0.1")

(bootlaces! +version+)

(task-options!
 pom  {:project 'gnar
       :version     +version+
       :description ""
       :url         "https://github.com/mathias/gnar"
       :scm         {:url "https://github.com/mathias/elderberry"}
       :license     {"name" "Eclipse Public License"
                     "url"  "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask node-compile
  "Settings for node.js compilation"
  []
  (cljs :node-target true
        :optimizations :simple
        :pretty-print true))

(deftask watch-compile
  "Generate JS from cljs"
  []
  (comp (watch) (node-compile)))
