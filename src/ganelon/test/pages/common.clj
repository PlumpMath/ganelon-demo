(ns ganelon.test.pages.common
  (:require
    [ganelon.web.dyna-routes :as dyna-routes]
    [ganelon.web.widgets :as widgets]
    [ganelon.web.actions :as actions]
    [ganelon.web.ui-operations :as ui]
    [hiccup.page :as hiccup]
    [hiccup.core :as h]
    [hiccup.util]
    [noir.response :as resp]
    [noir.request :as req]
    [noir.session :as sess]
    [compojure.core :as compojure]
    ))

(defn navbar-link [path & descr]
  (h/html
    [:li {:class (if (= (:uri req/*request*) path) "active" "")}
     [:a {:href path} descr]]))

(defn navbar []
  (h/html
    [:div [:div {:class "navbar navbar-inverse navbar-fixed-top" :style "opacity: 0.9;"}
           [:div {:class "navbar-inner"}
            [:div {:class "container"}
             [:a {:class "brand" :href "/"} "Ganelon"]
             [:div {:class "nav-collapse collapse"}
              [:ul {:class "nav"}
               (navbar-link "/basics" [:i.icon-lightbulb] "&nbsp;Basic concepts")
;               (navbar-link "/tutorial" [:i.icon-book] "&nbsp;Tutorial")
               (navbar-link "/ajax" [:i.icon-spinner] "&nbsp;AJAX")
               (navbar-link "/routing" [:i.icon-road] "&nbsp;Routing")
;               (navbar-link "/extras" [:i.icon-star] "Extras")
               (navbar-link "/doc/index.html" [:i.icon-book] "API docs")
               [:li [:a {:href "https://github.com/tlipski/ganelon" :target "_blank"} [:i.icon-github] "&nbsp;Source codes"]]
               [:li [:a {:href "https://github.com/tlipski/ganelon/issues" :target "_blank"} [:i.icon-tasks] "&nbsp;Issue tracker"]]
               ]]]]]]))

(defn layout [& content]
  (hiccup/html5
    [:head
     [:title "Ganelon"]
     (hiccup/include-css "/ganelon/css/bootstrap.css")
     (hiccup/include-css "/ganelon/css/jquery.gritter.css")
     (hiccup/include-css "/ganelon-demo/css/ganelon-demo.css")
     (hiccup/include-css "/ganelon-demo/font-awesome/css/font-awesome.css")]
    [:body {:class "default-body" :data-spy "scroll" :data-target "#sidenav"}
     (navbar)
     content
     [:footer {:style "text-align: center; padding: 30px 0; margin-top: 70px; border-top: 1px solid #E5E5E5; color: #f6f6f6; background: url(/ganelon-demo/img/low_contrast_linen.png);"}
      [:div.container
        [:p "The Ganelon framework has been designed, created and is maintained by " [:a {:href "http://twitter.com/tomeklipski"} "@tomeklipski"] "."]
        [:p "The code is available under " [:a {:href "http://opensource.org/licenses/eclipse-1.0.php"} "Eclipse Public License 1.0"] "."]
        [:p "This demo site runs on " [:a {:href "http://heroku.com"} "heroku"] ". Source code highlightning powered by "
         [:a {:href "http://hilite.me/"} "hilite.me"] "."]
        ]]
(hiccup/include-js "/ganelon/js/jquery-1.8.1.min.js")
(hiccup/include-js "/ganelon/js/bootstrap.js")
(hiccup/include-js "/ganelon/js/ganelon.js")
(hiccup/include-js "/ganelon/js/ext/ganelon.ops.bootstrap.js")
(hiccup/include-js "/ganelon/js/ext/ganelon.ops.gritter.js")
(hiccup/include-js "/ganelon/actions.js")
]))

(defn banner [title & details]
  (h/html
  [:div {:style "z-index: 100; color: #e6e6e6; background: url(/ganelon-demo/img/low_contrast_linen.png);;padding: 70px 0 40px; text-align: center; text-shadow: 0 1px 3px rgba(0, 0, 0, .4), 0 0 30px rgba(0, 0, 0, .075);"}
   [:div {:class "container" :style ""}
    [:h1 {:style "font-size: 120px; line-height: 1;letter-spacing: -2px;"} title]
    (when (not-empty details)
      [:p {:style "font-size: 40px; font-weight: 200; line-height: 1.25;"}
        details])]]))

(defn demo1-widget [msg]
  (widgets/with-div
    [:p "The message is: " [:b (hiccup.util/escape-html msg)]]
    [:ul [:li (widgets/action-link "demo-action"
      {:msg "test1"} {} "Set message to <b>test1</b>.")]
     [:li (widgets/action-link "demo-action"
       {:msg "test2"} {} "Set message to <b>test2</b>.")]]))

(actions/defjsonaction "demo-action" [msg widget-id]
  (actions/put-operation! (ui/notification "Success"
      (h/html "Message set to: " [:b (hiccup.util/escape-html msg)])))
  (actions/put-operation! (ui/notification "Another message"
      (h/html "Widget-id is: " [:b (hiccup.util/escape-html widget-id)])))
  (ui/fade widget-id (demo1-widget msg)))


(dyna-routes/setroute!
  :adminize (compojure/GET "/setadmin" []
              (sess/put! :admin true)
              (h/html
                [:p "User admin priviledge set, DO NOT enable this in production!"
                 [:a {:href (or (get (:headers noir.request/*request*) "referer") "/")} "return"]])))

(dyna-routes/setroute!
  :unadminize (compojure/GET "/unsetadmin" []
                (sess/put! :admin false)
                (h/html
                  [:p "User admin priviledge unset, DO NOT enable this in production!"
                   [:a {:href (or (get (:headers noir.request/*request*) "referer") "/")} "return"]])))