(ns webui.main
  (:require
   [dmohs.react :as r]
   [org.broadinstitute.uicomps.nav :as nav]
   [webui.components :as components]
   ))

(defn- init-nav-paths []
  (nav/clear-paths)
  (nav/defredirect {:regex #"privacy-policy" :make-path (constantly "policy")})
  (components/add-nav-paths))

(r/defc App
  {:component-will-mount
   (fn [{:keys [this]}]
     (init-nav-paths)
     (.addEventListener js/window "hashchange" (r/method this :-handle-hash-change))
     (this :-handle-hash-change))
   :render
   (fn [{:keys [this state]}]
     (let [{:keys [component make-props]} (nav/find-path-handler (:window-hash @state))]
       (if component
         [component (make-props)]
         [:div {}
          [:h1 {} "Page not found!"]])))
   :component-will-unmount
   (fn [{:keys [this]}]
     (.removeEventListener js/window "hashchange" (r/method this :-handle-hash-change)))
   :-handle-hash-change
   (fn [{:keys [state]}]
     (let [window-hash (aget js/window "location" "hash")]
       (when-not (nav/execute-redirects window-hash)
         (swap! state assoc :window-hash window-hash))))})

(defn render-application []
  (r/render
   (r/create-element App)
   (.. js/document (getElementById "app"))))

(render-application)
