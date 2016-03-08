(def project 'mim)
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.8.0" :scope "provided"]
                            [org.clojure/clojurescript "1.7.228"]
                            [org.clojure/core.match "0.3.0-alpha4"]
                            [hiccup "1.0.5"]
                            [sablono "0.6.2"]
                            [adzerk/boot-test "1.1.1" :scope "test"]])

(task-options!
 pom {:project     project
      :version     version
      :description "FIXME: write description"
      :url         "http://example/FIXME"
      :scm         {:url "https://github.com/yourname/mim"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))
