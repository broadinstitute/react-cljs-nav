(ns webui.common
  (:require
   [dmohs.react :as r]
   [org.broadinstitute.uicomps.nav :as nav]
   ))

(r/defc PageHeader
  {:render
   (fn []
     [:div {}
      [(if (nav/is-current-path? :home) :span :a) {:href (nav/get-link :home)} "Home"]
      " | "
      [(if (nav/is-current-path? :policy) :span :a) {:href (nav/get-link :policy)} "Policy"]
      " | "
      [:a {:href "#bad-link"} "Bad Link"]
      " | "
      [:a {:href "#privacy-policy"} "Old Link (redirected)"]
      [:hr]])})
