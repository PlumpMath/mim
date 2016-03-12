(ns mim.emit.sablono
  (:require [clojure.test :refer :all]
            [cljs.compiler :as cljs]
            [cljs.tagged-literals :as cljs-literals]
            [clojure.walk :refer [prewalk]]
            [sablono.normalize :as normalize]
            [sablono.util :refer :all]
            [sablono.compiler :refer :all]
            [sablono.core :refer [attrs]]
            [sablono.interpreter :as interpreter]
            [mim.core :refer [html]])
  (:import cljs.tagged_literals.JSValue))

(defmacro html-expand
  "Macro expand the Hiccup `content`."
  [form]
  `(macroexpand `(html ~~form)))

(defmethod print-method JSValue
  [^JSValue v, ^java.io.Writer w]
  (.write w "#js ")
  (.write w (pr-str (.val v))))

(deftype JSValueWrapper [val]
  Object
  (equals [this other]
    (and (instance? JSValueWrapper other)
         (= (.val this) (.val other))))
  (hashCode [this]
    (.hashCode (.val this)))
  (toString [this]
    (.toString (.val this))))

(defmethod print-method JSValueWrapper
  [^JSValueWrapper v, ^java.io.Writer w]
  (.write w "#js ")
  (.write w (pr-str (.val v))))

(defn wrap-js-value [forms]
  (prewalk
   (fn [form]
     (if (instance? JSValue form)
       (JSValueWrapper. (wrap-js-value (.val form))) form))
   forms))

(defn replace-gensyms [forms]
  (prewalk
   (fn [form]
     (if (and (symbol? form)
              (re-matches #"attrs\d+" (str form)))
       'attrs form))
   forms))

(defmacro are-html-expanded [& body]
  `(are [form# expected#]
       (is (= (wrap-js-value expected#)
              (wrap-js-value (replace-gensyms (html-expand form#)))))
     ~@body))

(deftest tag-names
  (testing "basic tags"
    (are-html-expanded
     '[:div] '(js/React.createElement "div" nil)
     '["div"] '(js/React.createElement "div" nil)
     '['div] '(js/React.createElement "div" nil)))
  (testing "tag syntax sugar"
    (are-html-expanded
     '[:div#foo] '(js/React.createElement "div" #js {:id "foo"})
     '[:div.foo] '(js/React.createElement "div" #js {:className "foo"})
     '[:div.foo (str "bar" "baz")]
     '(let* [attrs (str "bar" "baz")]
        (clojure.core/apply
         js/React.createElement "div"
         (if (clojure.core/map? attrs)
           (sablono.interpreter/attributes
            (sablono.normalize/merge-with-class {:class ["foo"]} attrs))
           #js {:className "foo"})
         (if (clojure.core/map? attrs)
           nil [(sablono.interpreter/interpret attrs)])))
     '[:div.a.b] '(js/React.createElement "div" #js {:className "a b"})
     '[:div.a.b.c] '(js/React.createElement "div" #js {:className "a b c"})
     '[:div#foo.bar.baz] '(js/React.createElement "div" #js {:id "foo", :className "bar baz"})
     '[:div.jumbotron] '(js/React.createElement "div" #js {:className "jumbotron"}))))
