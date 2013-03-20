;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns ganelon.test.pages.basics
  (:require [ganelon.web.dyna-routes :as dyna-routes]
            [ganelon.web.widgets :as widgets]
            [ganelon.web.actions :as actions]
            [hiccup.page :as hiccup]
            [hiccup.core :as h]
            [hiccup.util]
            [noir.response :as resp]
            [noir.session :as sess]
            [compojure.core :as compojure]
            [ganelon.test.pages.common :as common]))


(dyna-routes/defpage "/basics" []
  (common/layout
    (common/banner "Basic concepts")
    [:div.container {:style "padding-top: 30px;"}
     [:div.row
      [:div#sidenav.span3 [:ul.nav.nav-list.affix-top.nav-pills.nav-stacked {:style "margin-top: 0px; top: 50px;" :data-spy "affix" :data-offset-top "200"}
                           [:li.active [:a {:href "#concepts"} "Basic concepts"]]
                           [:li [:a {:href "#using"} "Using Ganelon"]]
                           [:li [:a {:href "#ajax"} "AJAX support"]]
                           [:li [:a {:href "#pages"} "Defining pages"]]
;                           [:li [:a {:href "#i18n"} "Internationalization (i18n)"]]
;                           [:li [:a {:href "#scaffolding"} "Scaffolding"]]
                           ]]
      [:div.span9 {}
       [:section#concepts ;{:style "padding-top: 40px;"}
        [:p "Ganelon framework provides additional features on top of Ring/Compojure and can be used just as any other Ring library.
            What makes it different, are the provided JavaScript libraries, allowing server-side to fully control browser behaviour."]
        [:h2 "Basic concepts"]
        [:p "The framework introduces three basic constructs, which allow the programmer to built dynamic web apps effectively, while still
        retaining the benefits of generating HTML server-side:"]
        [:div {:style "text-align: center"} [:img {:src "/ganelon-demo/img/aow.png"}]]
        [:ul
          [:li [:strong "Widget"] " is a function, returning part of the DOM tree, referenceable by an id attribute. Whole page can be built from standard content (e.g. menubar) and
          widgets, which hold the presentation logic."]
          [:li [:strong "Action"] " is a standard Ring handler, which returns JSON operations. The action is a place to communicate with business services or persistence layers and can be be referenced from Widgets or even  JavaScript code."]
          [:li [:strong "Operation"] " is a JSON object, which is interpreted by browser-side JavaScript plugins. "
                                     "For example, an operation can modify widget's contents or display a modal window. "
                                     "The operation defines generic (or tailored to the particular case) communication protocol between Action and Widget.
                                     <br/>Most common operations, such as DOM manipulation, notifications, modals, etc. is already bundled with Ganelon." ]]
        [:p "An application built with these constructs is easy to understand and maintain due to a standarized approach,
        and contains as little boilerplate code as possible."]
        [:p "In addition to that, Ganelon provides:"]
        [:ul
          [:li [:code "ganelon.web.dyna-routes"] " - helper utilities to manage page routes dynamically."]
          [:li [:code "ganelon.web.middleware"] " - helper middleware, such as CDN-ification of urls or access to remote IP when running behind a reverse proxy (e.g. lighttpd or nginx)."]
          [:li [:code "ganelon.util"] " - general purpose utility helper functions."]
          [:li [:code "ganelon.util.logging"] " - general purpose logging functions on top of " [:code "clojure.tools.logging"]]
         ]
       ]
       [:section#using {:style "padding-top: 40px;"}
        [:h2 "Using Ganelon"]
        [:p
          "To apply Ganelon in your web app project, simply add the following leiningen dependency:"]
        [:p "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">[<span class=\"symbol\">ganelon</span> <span class=\"literal\">&quot;0.9-SNAPSHOT&quot;</span>]</pre></div>"]

        [:p "Client side JavaScript is provided through the following files (assuming that /public resources are published as /):"]
[:p
 [:div.code
  [:pre.code
"(<span class=\"function\">hiccup/include-js</span> <span class=\"literal\">\"/ganelon/js/jquery-1.8.1.min.js\"</span>) <span class=\"comment\">;jQuery - required</span>
(<span class=\"function\">hiccup/include-js</span> <span class=\"literal\">\"/ganelon/js/bootstrap.js\"</span>) <span class=\"comment\">;Bootstrap - optional</span>
(<span class=\"function\">hiccup/include-js</span> <span class=\"literal\">\"/ganelon/js/ganelon.js\"</span>) <span class=\"comment\">;basic actions support</span>
(<span class=\"function\">hiccup/include-js</span> <span class=\"literal\">\"/ganelon/js/ext/ganelon.ops.bootstrap.js\"</span>) <span class=\"comment\">;additional Bootstrap related actions</span>
(<span class=\"function\">hiccup/include-js</span> <span class=\"literal\">\"/ganelon/js/ext/ganelon.ops.gritter.js\"</span>) <span class=\"comment\">;growl-style notifications through gritter.js</span>
(<span class=\"function\">hiccup/include-js</span> <span class=\"literal\">\"/ganelon/actions.js\"</span>) <span class=\"comment\">;dynamic actions interface</span>"
]]
 ]
        [:p "The Ganelon JavaScript files are available in " [:a {:href "https://github.com/tlipski/ganelon/tree/master/resources/public/ganelon/js"} "public.ganelon.js package"
                                                              ] "- in case you need to merge them with your other client-side modules."]
        [:p "The Bootstrap and gritter support is optional - you can easily overwrite them with your own plugins."]
        [:p "You might also want to include CSS file for Growl-style notifications or Bootstrap (2.3.0) - or merge them with your CSS files:"]
        [:p
         [:div.code
          [:pre.code
           "(<span class=\"function\">hiccup/include-css</span> <span class=\"literal\">\"/ganelon/css/bootstrap.css\"</span>) <span class=\"comment\">;Bootstrap - optional</span>
(<span class=\"function\">hiccup/include-css</span> <span class=\"literal\">\"/ganelon/css/jquery.gritter.css\"</span>) <span class=\"comment\">;growl-style notifications - optional</span>"
           ]]
         ]
        [:p "The Ganelon CSS files are available in " [:a {:href "https://github.com/tlipski/ganelon/tree/master/resources/public/ganelon/css"} "public.ganelon.css package"
                                                              ] "- in case you need to merge them with your other client-side stylesheets."]


        [:p "To run Ganelon-powered application just use standard Ring mechanisms. For example, code used to run this entire site
        with " [:code "ring.adapter.jetty"] " is listed below:"]
        [:p
"<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"keyword\">ns </span><span class=\"symbol\">ganelon.test.demo</span>
  (<span class=\"symbol\">:require</span> [<span class=\"symbol\">ganelon.test.demo-pages</span>]
            [<span class=\"symbol\">ring.middleware.stacktrace</span>]
            [<span class=\"symbol\">ring.middleware.reload</span>]
            [<span class=\"symbol\">ring.adapter.jetty</span> <span class=\"symbol\">:as</span> <span class=\"symbol\">jetty</span>]
            [<span class=\"symbol\">ganelon.web.middleware</span> <span class=\"symbol\">:as</span> <span class=\"symbol\">middleware</span>]
            [<span class=\"symbol\">ganelon.web.app</span> <span class=\"symbol\">:as</span> <span class=\"symbol\">webapp</span>]
            [<span class=\"symbol\">noir.session</span> <span class=\"symbol\">:as</span> <span class=\"symbol\">sess</span>]))

(<span class=\"keyword\">defonce </span><span class=\"symbol\">SERVER</span> (<span class=\"function\">atom</span> <span class=\"symbol\">nil</span>))

(<span class=\"keyword\">defn </span><span class=\"symbol\">start-demo</span> [<span class=\"symbol\">port</span>]
  (<span class=\"function\">jetty/run-jetty</span>
    (<span class=\"function\">-&gt;</span>
      (<span class=\"function\">ganelon.web.app/app-handler</span>
        (<span class=\"function\">ganelon.web.app/javascript-actions-route</span>))
      <span class=\"symbol\">middleware/wrap-x-forwarded-for</span>
      (<span class=\"function\">ring.middleware.stacktrace/wrap-stacktrace</span>)
      (<span class=\"function\">ring.middleware.reload/wrap-reload</span> {<span class=\"symbol\">:dirs</span> [<span class=\"literal\">&quot;test/ganelon/test/pages&quot;</span>]}))
    {<span class=\"symbol\">:port</span> <span class=\"symbol\">port</span> <span class=\"symbol\">:join?</span> <span class=\"symbol\">false</span>}))

(<span class=\"keyword\">let </span>[<span class=\"symbol\">mode</span> <span class=\"symbol\">:dev</span> <span class=\"symbol\">port</span> (<span class=\"function\">Integer.</span> (<span class=\"statement\">get </span>(<span class=\"function\">System/getenv</span>) <span class=\"literal\">&quot;PORT&quot;</span> <span class=\"literal\">&quot;8097&quot;</span>))]
  (<span class=\"function\">swap!</span> <span class=\"symbol\">SERVER</span> (<span class=\"keyword\">fn </span>[<span class=\"symbol\">s</span>] (<span class=\"statement\">when </span><span class=\"symbol\">s</span> (<span class=\"function\">.stop</span> <span class=\"symbol\">s</span>)) (<span class=\"function\">start-demo</span> <span class=\"symbol\">port</span>))))
</pre></div>"]
        [:p "Of course, it is also possible to use lein-war and put your whole web application in a neat .war file. Or
        use highly performant http-kit - anything that is compatible with Ring."]

        [:section#ajax  {:style "padding-top: 40px;"}
         [:h2 "AJAX support"]
         [:p "There is nothing easier in Ganelon framework than executing AJAX request and applying its results to the page contents."]
         [:p "The simple and easy to use mechanism translates JSON response into JavaScript calls. Ganelon provides out-of-the-box support for:"]
         [:ul
          [:li "Almost all of the " [:a {:href "http://api.jquery.com/category/manipulation/"} "jQuery Manipulation"] " methods."]
          [:li "Displaying/hiding Bootstrap's modal window."]
          [:li "Displaying Growl-style notification."]
          [:li "Manipulating web page url"]]
         [:p "The list above can be easily extended using only JavaScript."]
         [:p "Code below updates part of the page (widget) and displays Growl-style notifications:"]
         [:p
"<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"keyword\">defn </span><span class=\"symbol\">demo1-widget</span> [<span class=\"symbol\">msg</span>]
  (<span class=\"function\">widgets/with-div</span>
    [<span class=\"symbol\">:p</span> <span class=\"literal\">&quot;The message is: &quot;</span> [<span class=\"symbol\">:b</span> (<span class=\"function\">hiccup.util/escape-html</span> <span class=\"symbol\">msg</span>)]]
    [<span class=\"symbol\">:ul</span> [<span class=\"symbol\">:li</span> (<span class=\"function\">widgets/action-link</span> <span class=\"literal\">&quot;demo-action&quot;</span>
      {<span class=\"symbol\">:msg</span> <span class=\"literal\">&quot;test1&quot;</span>} {} <span class=\"literal\">&quot;Set message to &lt;b&gt;test1&lt;/b&gt;.&quot;</span>)]
     [<span class=\"symbol\">:li</span> (<span class=\"function\">widgets/action-link</span> <span class=\"literal\">&quot;demo-action&quot;</span>
       {<span class=\"symbol\">:msg</span> <span class=\"literal\">&quot;test2&quot;</span>} {} <span class=\"literal\">&quot;Set message to &lt;b&gt;test2&lt;/b&gt;.&quot;</span>)]]))
</pre></div>"]
         [:p "First, we have defined a widget rendering function. It is a function like any other, and we are just using " [:code "ganelon.web.widgets/with-div"]
          " macro to set widget id. Next, " [:code "ganelon.web.widgets/widget-update-link"] " generates ajax call link with " [:code "msg"] " parameter."]
[:p
 "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">actions/defjsonaction</span> <span class=\"literal\">&quot;demo-action&quot;</span> [<span class=\"symbol\">msg</span> <span class=\"symbol\">widget-id</span>]
  (<span class=\"function\">actions/put-operation!</span> (<span class=\"function\">ui/notification</span> <span class=\"literal\">&quot;Success&quot;</span>
      (<span class=\"function\">h/html</span> <span class=\"literal\">&quot;Message set to: &quot;</span> [<span class=\"symbol\">:b</span> (<span class=\"function\">hiccup.util/escape-html</span> <span class=\"symbol\">msg</span>)])))
  (<span class=\"function\">actions/put-operation!</span> (<span class=\"function\">ui/notification</span> <span class=\"literal\">&quot;Another message&quot;</span>
      (<span class=\"function\">h/html</span> <span class=\"literal\">&quot;Widget-id is: &quot;</span> [<span class=\"symbol\">:b</span> (<span class=\"function\">hiccup.util/escape-html</span> <span class=\"symbol\">widget-id</span>)])))
  (<span class=\"function\">ui/fade</span> <span class=\"symbol\">widget-id</span> (<span class=\"function\">demo1-widget</span> <span class=\"symbol\">msg</span>)))
</pre></div>
"]
         [:p "The action handler returns two operations for client-side JavaScript to perform:"]
         [:ul
          [:li "Display two notifications - " [:code "ganelon.web.ui-operations/notification"] " results are put into operation queue with "
           [:code "ganelon.web.actions/put-operation!"] "."]
          [:li "Replace with a fade effect div content with a new content of " [:code "demo1-widget"] ". The div id is provided from widget-id parameter, autogenerated by "
           [:code "ganelon.web.widgets/with-div"] " in a " [:code "demo1-widget"] " function in the step before."]
          ]
         [:p "Please note, that you can use Ganelon's AJAX action as any other Ring-compliant handlers. The " [:code "defjsonaction"] " macro is here merely for you convience!"]
         [:p "Try it here:"]
         [:div.box-widget (common/demo1-widget "demo1 widget")]]
        [:section#pages {:style "padding-top: 40px;"}
         [:h2 "Defining pages"]
         [:p "Ganelon is 100% Ring-compliant, so it is possible to define handler hierarchy as in any other
              Ring/Compojure app and still benefit from all of the features of the framework." [:br]
          "In addition to that, a " [:code "ganelon.web.dyna-routes"] " package is available."]
         [:p "This package provides a simple in-memory registry for handlers, and the simplest use is as follows: "]
         [:p
          "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">ganelon.web.dyna-routes/defpage</span> <span class=\"literal\">&quot;/routing/sample&quot;</span> []
            <span class=\"literal\">&quot;Hello world!&quot;</span>)</pre></div>"]
         [:p [:a {:href "/routing/sample" :target "_blank"} "Try it (open a new window/tab)"]]
         [:p "It is also possible to use " [:code "setroute!"] " function and define a route as a direct Compojure (or other) route:"]
         [:p
          "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">ganelon.web.dyna-routes/setroute!</span> <span class=\"symbol\">:example2</span>
            (<span class=\"function\">compojure.core/GET</span> <span class=\"literal\">&quot;/routing/sample2&quot;</span> []
              <span class=\"literal\">&quot;Hello world !!!&quot;</span>))</pre></div>"
          ]
         [:p [:a {:href "/routing/sample2" :target "_blank"} "Run compojure sample (opens in a new browser tab/window)"]]

         [:p [:a {:href "/routing"} "More information..."]]]

        ;        [:section#i18n  {:style "padding-top: 40px;"}
;          [:h2 "Internationalization (i18n)"]
;          [:p "With Ganelon and its AJAX support, providing multi-lingual application with content edited on-the-fly is extrelemely easy." [:br]
;              "Just declare a span (with " [:code "ganelon.web.i18n/label"] ") or div (with " [:code "ganelon.web.i18n/text"] ") as an i18n-enabled: "]
;          [:p
;"<!-- HTML generated using hilite.me --><div style=\"background: #ffffff; overflow:auto;width:auto;color:black;background:white;border:solid gray;border-width:.1em .1em .1em .8em;padding:.1em .1em;\"><pre class=\"code\">(<span style=\"color: #0060B0; font-weight: bold\">ganelon.web.i18n/label</span> <span style=\"background-color: #fff0f0\">&quot;i18n.label1&quot;</span>)
;(<span style=\"color: #0060B0; font-weight: bold\">ganelon.web.i18n/text</span> <span style=\"background-color: #fff0f0\">&quot;i18n.text1&quot;</span>)</pre></div>"]
;          [:div {:style "padding: 20px;"} (ganelon.web.i18n/label "i18n.label1" :key1 "value1")]
;          [:div {:style "padding: 20px; padding-top: 0px;"}  (ganelon.web.i18n/text "i18n.text1" :key2 "test123")]
;          [:p "When you are logged in as an <a href='/setadmin'>admin</a> user, the value can be edited live through modal window,
;              speeding the work on staging environment. Since i18n text is stored in a simple key:language:value structure,
;              it can be easily migrated to production." [:br]
;              "Please note, that the i18n values are not html escaped, so they can contain HTML formatting, links or even JavaScript code. Use with caution!"
;           ]
;          [:p "Still, <a href='/unsetadmin'>regular users</a> see only current value of i18n key dedicated for their language."]
;          [:p "The language gets detected automatically through browser's headers, but also can be adjusted manually by the user."]
;          ]
;        [:section#scaffolding {:style "padding-top: 40px;"}
;          [:h2 "Scaffolding"]
;          [:p "Ganelon also provides simple administration interface for persistent entities. Due to a pluggable
;               persistence providers support, scaffolding will work most of the popular persistence providers (e.g. MySQL, PostgreSQL, MongoDB, ...)."]
;          [:p "To enable administration interface for I18N, we just have to use " [:code "ganelon.scaffold.entity-pages/scaffold"] " function, providing entity name and fields to display:"]
;[:p
;"<!-- HTML generated using hilite.me --><div style=\"background: #ffffff; overflow:auto;width:auto;color:black;background:white;border:solid gray;border-width:.1em .1em .1em .8em;padding:.1em .1em;\"><pre class=\"code\">(<span style=\"color: #0060B0; font-weight: bold\">scaffold/scaffold</span> <span style=\"color: #A06000\">:i18nentries</span>
;  [<span style=\"color: #A06000\">&#39;key</span> <span style=\"color: #A06000\">&#39;value</span> (<span style=\"color: #0060B0; font-weight: bold\">renderers/with-form-renderer</span> <span style=\"color: #A06000\">&#39;lang</span>
;   (<span style=\"color: #0060B0; font-weight: bold\">renderers/dropdown</span> (<span style=\"color: #008000; font-weight: bold\">fn </span>[<span style=\"color: #906030\">e</span>] <span style=\"color: #906030\">available-langs</span>)))])</pre></div>"]
;          [:p "Different fields can be configured to use already provided or custom renderer functions. For example, " [:code "lang"] " field is displayed as a dropdown,
;          with its keys and values taken from " [:code "available-langs"] "."]
;         ]
        ]]]]))





