(def project 'mim)
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"resources"}
          :source-paths   #{"test" "src"}
          :dependencies
          '[[org.clojure/clojure "1.8.0" :scope "provided"]
            [org.clojure/clojurescript "1.7.228" :scope "provided"]
            [org.clojure/core.match "0.3.0-alpha4"]
            [hiccup "1.0.5"]
            [sablono "0.6.2"]
            ;; Dev
            [adzerk/boot-test "1.1.1" :scope "test"]
            ;; Example Frontend
            [adzerk/boot-cljs          "1.7.170-3"  :scope "test"]
            [adzerk/boot-cljs-repl     "0.2.0"      :scope "test"]
            [adzerk/boot-reload        "0.4.1"      :scope "test"]
            [org.omcljs/om "1.0.0-alpha30"]
            ;; Example Backend
            [reloaded.repl "0.2.0"]
            [com.stuartsierra/component "0.2.3"]
            [ring "1.3.2"]
            [compojure "1.4.0"]])

(task-options!
 pom {:project     project
      :version     version
      :description "FIXME: write description"
      :url         "https://github.com/bensu/mim"
      :scm         {:url "https://github.com/bensu/mim"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[reloaded.repl         :refer [go reset start stop system]]
 '[mim.boot         :refer [start-app]])

(load-data-readers!)

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(deftask run []
  (comp (watch)
        (cljs-repl)
        (reload)
        (speak)
        (cljs)
        (start-app :port 3000)))
