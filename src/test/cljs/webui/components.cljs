(ns webui.components
  (:require
   [dmohs.react :as r]
   [org.broadinstitute.uicomps.nav :as nav]
   [webui.common :as common]
   ))

(r/defc- Home
  {:render
   (fn []
     [:div {}
      [common/PageHeader]
      [:h1 {} "Welcome Home!"]])})

(r/defc- Policy
  {:render
   (fn []
     [:div {}
      [common/PageHeader]
      "This is a page describing this site's privacy policy, which is non-existant."])})

(defn add-nav-paths []
  (nav/defpath
    :home
    {:component Home
     :regex #""
     :make-props (constantly {})
     :make-path (constantly "")})
  (nav/defpath
    :policy
    {:component Policy
     :regex #"policy"
     :make-props (constantly {})
     :make-path (constantly "policy")}))
