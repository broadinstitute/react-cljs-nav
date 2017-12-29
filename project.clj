(def source-paths ["src/main/cljs"])
(def version (get (System/getenv) "REACT_CLJS_NAV_VERSION" "master-SNAPSHOT"))

(defproject org.broadinstitute/react-cljs-nav version
  :url "https://github.com/broadinstitute/react-cljs-nav"
  :description "URL navigation and routing for React CLJS single-page applications."
  :license "BSD 3-Clause License"
  :dependencies [[dmohs/react "1.3.0"]]
  :plugins [[lein-cljsbuild "1.1.7"] [lein-figwheel "0.5.14"]]
  :profiles {:ui
             {:dependencies [[binaryage/devtools "0.9.8"]
                             [org.clojure/clojure "1.8.0"]
                             [org.clojure/clojurescript "1.9.946"]]
              :target-path "resources/public/target"
              :clean-targets ^{:protect false} ["resources/public/target"]
              :cljsbuild
              {:builds
               {:client
                {:source-paths ~(concat source-paths ["src/test/cljs"])
                 :compiler
                 {:main "webui.main"
                  :optimizations :none
                  :source-map true
                  :source-map-timestamp true
                  :output-dir "resources/public/target/build"
                  :output-to "resources/public/target/compiled.js"
                  :asset-path "target/build"
                  :preloads [devtools.preload]
                  :external-config {:devtools/config {:features-to-install [:formatters :hints]}}}
                 :figwheel true}}}}}
  :source-paths ~source-paths
  :cljsbuild {:builds {:client {:source-paths ~source-paths}}}
  :deploy-repositories [["clojars" {:sign-releases false}]])
