(defproject
 gnar
 "0.1.0-SNAPSHOT"
 :dependencies
 [[org.clojure/clojure "1.6.0"]
  [tailrecursion/boot.core "2.5.0" :exclusions [[org.clojure/clojure]]]
  [tailrecursion/hoplon "5.10.24"]
  [tailrecursion/boot.task "2.2.1"]
  [tailrecursion/boot.notify "2.0.2"]
  [tailrecursion/boot.ring "0.2.1"]
  [honeysql "0.4.3"]
  [org.clojure/java.jdbc "0.3.3"]
  [postgresql/postgresql "8.4-702.jdbc4"]
  [hearst "0.1.1-SNAPSHOT"]
  [com.cemerick/url "0.1.1"]
  [com.cemerick/friend "0.2.0"]
  [environ "1.0.0"]
  [playnice "1.0.1"]]
 :source-paths
 [".boot/tmp/92526/_COLON_tailrecursion.boot.task_SLASH_cljs_stage/dir.tmp"
  "src/castra"
  ".boot/tmp/92526/_COLON_tailrecursion.hoplon.boot_SLASH_hoplon_src/dir.tmp"
  "src/hoplon"
  ".boot/tmp/92526/_COLON_tailrecursion.hoplon.boot_SLASH_cljs_out/dir.tmp"
  "src/cljs"
  ".boot/tmp/92526/_COLON_tailrecursion.hoplon.boot_SLASH_hoplon_src_inc_css/dir.tmp"
  ".boot/tmp/92526/_COLON_tailrecursion.hoplon.boot_SLASH_public_out/dir.tmp"
  "resources"]
 :min-lein-version "2.0.0"
 :uberjar-name
 "gnar-standalone.jar"
 :plugins
 [[lein-environ "1.0.0"]]
 :profiles
 {:production {:env {:production true}}})
