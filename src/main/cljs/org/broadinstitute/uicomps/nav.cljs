(ns org.broadinstitute.uicomps.nav
  (:require
   [clojure.string :as str]
   ))

(defonce all-path-handlers (atom {}))

(defn defpath [k handler]
  (assert (contains? handler :regex))
  (assert (contains? handler :component))
  (assert (contains? handler :make-props))
  (assert (contains? handler :make-path))
  (assert (not (contains? @all-path-handlers k)) (str "Key " k " already defined"))
  (swap! all-path-handlers assoc k handler))

(defonce all-redirects (atom []))

(defn defredirect [handler]
  (assert (contains? handler :regex))
  (assert (contains? handler :make-path))
  (swap! all-redirects conj handler))

(defn clear-paths []
  (reset! all-path-handlers {})
  (reset! all-redirects []))

(defn- find-matches [window-hash redirects?]
  (let [window-hash (or window-hash "")
        cleaned (js/decodeURI (subs window-hash 1))]
    (filter
     some?
     (map
      (fn [x]
        (let [[k handler] (if redirects? [nil x] x)]
          (when-let [matches (re-matches (:regex handler) cleaned)]
            (let [make-props (:make-props handler)]
              (assoc handler
                     :key k
                     ;; First match is the entire string, so toss that one.
                     :make-props #(apply make-props (rest matches)))))))
      (if redirects? @all-redirects @all-path-handlers)))))

(defn find-path-handler [window-hash]
  (let [matching-handlers (find-matches window-hash false)]
    (assert (not (> (count matching-handlers) 1))
            (str "Multiple keys matched path: " (map :key matching-handlers)))
    (first (not-empty matching-handlers))))

(defn get-path [k & args]
  (let [handler (get @all-path-handlers k)
        {:keys [make-path]} handler]
    (assert handler
            (str "No handler found for key " k ". Valid path keys are: " (keys @all-path-handlers)))
    (js/encodeURI (apply make-path args))))

(defn get-link [k & args]
  (str "#" (apply get-path k args)))

(defn go-to-path [k & args]
  (aset js/window "location" "hash" (apply get-path k args)))

(defn is-current-path? [k & args]
  (= (apply get-path k args) (subs (aget js/window "location" "hash") 1)))

(defn execute-redirects [window-hash]
  (let [matching-handlers (find-matches window-hash true)]
    (assert (not (> (count matching-handlers) 1))
            (str "Multiple redirects matched path: " (map :regex matching-handlers)))
    (when-let [handler (first (not-empty matching-handlers))]
      (let [{:keys [make-path]} handler]
        (js-invoke (aget js/window "location") "replace" (str "#" (make-path)))
        true))))

(defn parse-query-string [qs]
  (if-not (clojure.string/blank?  qs)
    (reduce (fn [r kv]
              (let [[k v] (clojure.string/split kv #"[=]")
                    k (when k (js/decodeURIComponent k))
                    v (when v (js/decodeURIComponent v))]
                (assoc r (keyword k) v)))
            {}
            (clojure.string/split qs #"[&]"))
    {}))

(defn ->query-string [m]
  (if (empty? m)
    ""
    (str
     "?"
     (clojure.string/join
      "&"
      (map (fn [[k v]] (str (name k) "=" v)) m)))))
