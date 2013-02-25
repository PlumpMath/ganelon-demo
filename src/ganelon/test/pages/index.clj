;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns ganelon.test.pages.index
  (:require
    [ganelon.web.dyna-routes :as dyna-routes]
    [ganelon.web.widgets :as widgets]
    [ganelon.web.ui-operations :as ui]
    [ganelon.web.actions :as actions]
    [ganelon.test.pages.common :as common]
    [hiccup.page :as hiccup]
    [hiccup.core :as h]
    [hiccup.util]
    [ganelon.util :as util]
    [ganelon.util.datetime :as dt]
    [noir.response :as resp]
    [noir.session :as sess]
    [compojure.core :as compojure]))

(defn mkmsg [msg]
  {:msg (util/substring msg 0 20)
   :time (dt/format-time (java.util.Date.) "UTC")})

(defonce COUNTER (atom 0))
(defonce ENTRIES (atom []))

(defn box-widget []
  (widgets/with-div
    [:p "Call count (since restart): " [:b @COUNTER] ". Last 4 entries:"]
    (for [entry @ENTRIES]
      [:div.hibox [:b (:time entry)] ":&nbsp;"
       (hiccup.util/escape-html (:msg entry))])
    (widgets/action-form "say-hi" {} {:class "form-inline"}
      [:input {:name "msg" :placeholder "Say hi!" :type "text"
               :maxlength "20" :size "20"}] "&nbsp;"
      [:button {:class "btn btn-primary"} "Send!"])))

(actions/defwidgetaction "say-hi" [msg]
  (swap! COUNTER inc)
  (swap! ENTRIES #(util/smart-subvec (flatten [(mkmsg msg) %]) 0 4))
  (box-widget))

(actions/defjsonaction "demo-action-source" []
  (ui/modal "demo-action-source-modal"
     (h/html
       [:div {:class "modal-header"}
        [:a {:class "close" :data-dismiss "modal"} "×"]
        [:h3 "Example action and widget"]])
     :style "width: 640px;"))

(dyna-routes/defpage "/" []
  (common/layout
    (common/banner "Ganelon"
      "Microframework bringing instant development of AJAX-enabled web applications to Clojure/Ring."
      [:div {:style "text-align: center"}
        [:div
         [:a.btn.btn-large.btn-primary {:href "https://github.com/tlipski/ganelon" :style "margin-right: 10px"} [:i.icon-github] "&nbsp;Browse the source codes"]
         [:a.btn.btn-large.btn-primary {:href "https://github.com/tlipski/ganelon/star":style "margin-left: 10px"} [:i.icon-star] "&nbsp;Star on GitHub"]
         ]]
      )
    [:div {:class "container" :style "padding-top: 30px;"}
     [:div.row
      [:div.span8
       [:h2 [:i.icon-spinner]"&nbsp;AJAX: as simple as possible"]
       [:p "Ganelon is about just one thing - building real-world,
       AJAX-enabled web applications with Clojure/Ring as easily and quickly as possible."]
       [:p "Just define your views (Widgets) and UI logic (Actions) in Clojure server-side, and Ganelon framework will take care of technical details."]
       [:p "The in-memory shoutbox below (try it!):"]
       [:div.box-widget (box-widget)]
       [:p "Is built with the following code:"]
       [:p "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"keyword\">defn </span><span class=\"symbol\">box-widget</span> []
  (<span class=\"function\">widgets/with-div</span>
    [<span class=\"symbol\">:p</span> <span class=\"literal\">&quot;Call count (since restart): &quot;</span> [<span class=\"symbol\">:b</span> <span style=\"\">@</span><span class=\"symbol\">COUNTER</span>] <span class=\"literal\">&quot;. Last 4 entries:&quot;</span>]
    (<span class=\"statement\">for </span>[<span class=\"symbol\">entry</span> <span style=\"\">@</span><span class=\"symbol\">ENTRIES</span>]
      [<span class=\"symbol\">:div.hibox</span> [<span class=\"symbol\">:b</span> (<span class=\"symbol\">:time</span> <span class=\"symbol\">entry</span>)] <span class=\"literal\">&quot;:&amp;nbsp;&quot;</span>
       (<span class=\"function\">hiccup.util/escape-html</span> (<span class=\"symbol\">:msg</span> <span class=\"symbol\">entry</span>))])
    (<span class=\"function\">widgets/action-form</span> <span class=\"literal\">&quot;say-hi&quot;</span> {} {<span class=\"symbol\">:class</span> <span class=\"literal\">&quot;form-inline&quot;</span>}
      [<span class=\"symbol\">:input</span> {<span class=\"symbol\">:name</span> <span class=\"literal\">&quot;msg&quot;</span> <span class=\"symbol\">:placeholder</span> <span class=\"literal\">&quot;Say hi!&quot;</span> <span class=\"symbol\">:type</span> <span class=\"literal\">&quot;text&quot;</span>
               <span class=\"symbol\">:maxlength</span> <span class=\"literal\">&quot;20&quot;</span> <span class=\"symbol\">:size</span> <span class=\"literal\">&quot;20&quot;</span>}] <span class=\"literal\">&quot;&amp;nbsp;&quot;</span>
      [<span class=\"symbol\">:button</span> {<span class=\"symbol\">:class</span> <span class=\"literal\">&quot;btn btn-primary&quot;</span>} <span class=\"literal\">&quot;Send!&quot;</span>])))

(<span class=\"function\">actions/defwidgetaction</span> <span class=\"literal\">&quot;say-hi&quot;</span> [<span class=\"symbol\">msg</span>]
  (<span class=\"function\">swap!</span> <span class=\"symbol\">COUNTER</span> <span class=\"symbol\">inc</span>)
  (<span class=\"function\">swap!</span> <span class=\"symbol\">ENTRIES</span> <span class=\"numeric\">#</span>(<span class=\"function\">util/smart-subvec</span> (<span class=\"function\">flatten</span> [(<span class=\"function\">mkmsg</span> <span class=\"symbol\">msg</span>) <span class=\"symbol\">%</span>]) <span class=\"numeric\">0</span> <span class=\"numeric\">4</span>))
  (<span class=\"function\">box-widget</span>))
</pre></div>
"]
      [:p [:a {:href "/basics"} "Learn more"] ]
      ]

      [:div.span4
       [:h2 [:i.icon-star] "&nbsp;Easy to use"]
       [:p "If you are using <code>leiningen</code>, just add following dependency to your <code>project.clj</code> file:"
       [:code
        "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">[<span class=\"symbol\">ganelon</span> <span class=\"literal\">&quot;0.7-SNAPSHOT&quot;</span>]</pre></div>"]]
       [:p "And you are good to go!" [:br ] "All required libraries will be loaded, "
        "CSS and JS files will be available as well."
        ]
       [:h2 [:i.icon-ok]"&nbsp;Open Source"]
       [:p "Ganelon is completely free for either commercial, personal or educational use." ]
       [:p "The whole source code is available under "
        [:a {:href "http://opensource.org/licenses/eclipse-1.0.php"} "Eclipse Public License 1.0"]
        " and available on "
        [:a {:href "http://github.com/tlipski/ganelon"} "GitHub"]]
       [:h2 [:i.icon-dashboard]"&nbsp;Production tested"]
       [:p "Ganelon is used to power user experience for the news aggregator app: "
        [:a {:href "https://mydailysocial.info" :target "_blank"} "Daily Social"] "."]
       [:p "If you are using Ganelon, " "<a href=\"https://twitter.com/intent/tweet?screen_name=tomeklipski&text=http%3A%2F%2F...%20is%20powered%20by%20%23ganelon%20and%20%23clojure%20as%20well!\">tell me about it!</a>"]
       [:p "This demo site is of course powered by Ganelon as well. " [:br]
        [:a {:href "http://github.com/tlipski/ganelon-demo" :target "_blank"} "View source"]]
       ]]
     [:div {:class "row"}
      [:div {:class "span6"}
       [:h2 [:i.icon-cogs] "&nbsp;Solid foundation"]
       [:p "Ganelon combines great Open Source technologies and frameworks, including:"]
       [:ul [:li [:a {:href "http://www.clojure.org/" :target "_blank"} [:strong "Clojure "]] " - the most powerful programming language for the Java Virtual Machine. "]
        [:li [:a {:href "http://www.jquery.org/" :target "_blank"} [:strong "jQuery"]] " - All-round, extensive JavaScript library. "]
        [:li [:a {:href "https://github.com/weavejester/hiccup" :target "_blank"} [:strong "Hiccup"]] " - consise and elastic library for representing HTML in Clojure. "]
        [:li [:a {:href "http://twitter.github.com/bootstrap/" :target "_blank"} [:strong "Twitter Bootstrap 2"]] " - Sleek, intuitive, and powerful front-end framework for faster
              and easier web development. "]
        [:li [:a {:href "https://github.com/weavejester/compojure" :target "_blank"} [:strong "Compojure"]] "/" [:a {:href "https://github.com/ring-clojure/ring" :target "_blank"} [:strong "Ring"]]
         " - abstraction and routing of HTTP(S) requests for Clojure."]
        ]
       [:p "You can also use Ganelon with your own favorite JavaScript framework, even totally replacing Bootstrap and jQuery,
            by providing a thin JavaScript adapter layer!"]
       ]
      [:div {:class "span6"}
       [:h2 [:i.icon-bolt] "&nbsp;Rapid development"]
       [:p "With Clojure and little or even no custom JavaScript, you can develop dynamic web apps in minutes."
        [:br ]
        "No more redeployments, time consuming project builds and web server restarts:"]
       [:ol [:li "Add " [:code "ring.middleware.reload/wrap-reload"] " middleware to your routes."]
        [:li "Load your project in Clojure REPL from your favorite IDE, such as Emacs, Eclipse or Intellij IDEA."]
        [:li "Modify a Clojure file."]]
       [:p "Your Compojure/Ring routes and Ring middleware will be updated on the fly, without the servlet
      container/Clojure process restarts or redeploys."]
       ]]
     [:div {:class "row"}
;      [:div {:class "span6"}
;       [:h1 "And even more..."]
;       [:p "Additional features are provided with <a href='https://github.com/tlipski/ganelon-extras'>ganelon-extras</a> and
;      <a href='https://github.com/tlipski/ganelon-mongo>ganelon-mongo</a> packages:"]
;       [:ul [:li "Pluggable persistence mechanisms with scaffolding support. "
;             [:a {:href "http://www.mongodb.org/" :target "_blank"} [:strong "MongoDB "]] " support is provided by
;         <a href='https://github.com/tlipski/ganelon-mongo'>ganelon-mongo</a>"]
;        [:li "Out-of-the-box configuration support."]
;        [:li "RabbitMQ message queuing."]
;        [:li "I18N widget, allowing site administrator to edit page content on-the-fly."]
;        [:li "Cryptographic, email and http utilities."]
;        ]]
      [:div {:class "span4"}]]
     ]))
