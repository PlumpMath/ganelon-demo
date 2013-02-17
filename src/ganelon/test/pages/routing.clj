(ns ganelon.test.pages.routing
  (:require
    [ganelon.web.dyna-routes :as dyna-routes]
    [ganelon.web.widgets :as widgets]
    [ganelon.web.actions :as actions]
    [hiccup.page :as hiccup]
    [hiccup.core :as h]
    [hiccup.util]
    [noir.response :as resp]
    [noir.session :as sess]
    [compojure.core :as compojure]
    [ganelon.test.pages.common :as common]))


(ganelon.web.dyna-routes/defpage "/routing/sample" []
  "Hello world!")

(ganelon.web.dyna-routes/defpage "/routing/samplel" []
  (ganelon.util.logging/enrich-log-msg "This is a test!"))

(ganelon.web.dyna-routes/setroute! :example2
  (compojure.core/GET "/routing/sample2" []
    "Hello world !!!"))

(ganelon.web.dyna-routes/setroute! :example3
  (ganelon.web.actions/JSONACTION "sample3" [x y]
    {:x x :y y :z "!"}))

(ganelon.web.dyna-routes/setroute! :example31
  (ganelon.web.actions/ACTION "sample4" [x y]
    (noir.response/json {:x x :y y :z "!"})))

(defn wrap-fake-admin [handler]
  (fn [req] (if (:admin (:params req))
              (handler req) {:status 401 :body "401 Forbidden"})))

(ganelon.web.dyna-routes/setroute! :example4
  (compojure.core/GET "/routing/sample4/*" []
    (->
      (compojure.core/routes
        (compojure.core/GET "/routing/sample4/test1" []
          (hiccup.core/html "A very sensitive information,
                             requiring admin privileges"))
        (compojure.core/GET "/routing/sample4/test2" []
          (hiccup.core/html "A moderately sensitive information,
                              requiring admin privileges")))
      wrap-fake-admin)))

(ganelon.web.dyna-routes/setroute! :sample-admin :example51
  (compojure.core/GET "/routing/sample5/test1" []
      "Even more sensitive information,
       requiring admin privileges."))

(ganelon.web.dyna-routes/setroute! :sample-admin :example52
  (compojure.core/GET "/routing/sample5/test2" []
      "Quite moderately sensitive information,
       requiring admin privileges."))

(ganelon.web.dyna-routes/setroute! :example5
  (compojure.core/GET "/routing/sample5/*" []
    (-> (ganelon.web.dyna-routes/route-ns-fn :sample-admin)
        wrap-fake-admin)))

(dyna-routes/defpage "/routing" []
  (common/layout
    (common/banner "Routing")
    [:div.container {:style "padding-top: 30px;"}
     [:div.row
      [:div#sidenav.span3
        [:ul.nav.nav-list.affix-top.nav-pills.nav-stacked {:style "margin-top: 0px; top: 50px;" :data-spy "affix" :data-offset-top "200"}
          [:li.active [:a {:href "#simple"} "Simple route"]]
          [:li [:a {:href "#compojure"} "Compojure routes"]]
          [:li [:a {:href "#ring" } "Ring support"]]
          [:li [:a {:href "#groups"} "Route groups"]]
          ]]
      [:div.span9
       [:p "Ganelon framework comes with several convienience functions for defining and managing Ring routes,
        while still retaining the power and flexibility of Compojure/Ring."]
       [:section#simple ;{:style "padding-top: 40px;"}
        [:p "It is not required to use them - if you retain classical Ring approach,
        AJAX support and other features provided by Ganelon will still be available."]
        [:h2 "Simple route" ]
       [:p "To define a simple route, just use Noir style " [:code "ganelon.web.dyna-routes/defpage"] " macro:"]
[:p "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">ganelon.web.dyna-routes/defpage</span> <span class=\"literal\">&quot;/routing/sample&quot;</span> []
  <span class=\"literal\">&quot;Hello world!&quot;</span>)
</pre></div>"]
       [:p "The defined route will respond to any HTTP request type (GET, POST, PUT, DELETE, ...)."]
       [:p [:a {:href "/routing/sample" :target "_blank"} "Run sample (opens in a new browser tab/window)"]]]
       [:section#compojure {:style "padding-top: 40px;"}
        [:h2 "Compojure routes"]
        [:p "It is also possible to use " [:code "setroute!"] " function and define a route as a direct Compojure (or any other Ring-compliant) route:"]
        [:p
"<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">ganelon.web.dyna-routes/setroute!</span> <span class=\"symbol\">:example2</span>
  (<span class=\"function\">compojure.core/GET</span> <span class=\"literal\">&quot;/routing/sample2&quot;</span> []
    <span class=\"literal\">&quot;Hello world !!!&quot;</span>))</pre></div>
"
             ]
        [:p [:a {:href "/routing/sample2" :target "_blank"} "Run compojure sample (opens in a new browser tab/window)"]]
        [:p "You can also use this to define " [:a {:href "/ajax"} "AJAX"] " operations or any other Compojure/Ring compliant handlers/middleware (for example -
        <a href='https://github.com/cemerick/friend' target='_blank'>Cemerick's friend</a> framework):"]
        [:p "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">ganelon.web.dyna-routes/setroute!</span> <span class=\"symbol\">:example2</span>
  (<span class=\"function\">compojure.core/GET</span> <span class=\"literal\">&quot;/routing/sample2&quot;</span> []
    <span class=\"literal\">&quot;Hello world !!!&quot;</span>))
</pre></div>
"]
        [:p [:a {:href "/a/sample3?x=5&y=\"6\"" :target "_blank"} "Run sample action (opens in a new browser tab/window)"]]]
       [:section#ring {:style "padding-top: 40px"}
        [:h2 "Ring support"]
        [:p "The mechanisms provided in " [:code "ganelon.web.dyna-routes"] " are 100% Ring-compatible. You can mix them with
             Ring middleware or other Ring-complaint functions. " ]
        [:p "The example below wraps all requests to " [:code "/routing/sample4/*"]
            " with a middleware that checks for an admin parameter and if it is not present - blocks the call with 401 Forbidden HTTP error code."]
        [:p
"<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"keyword\">defn </span><span class=\"symbol\">wrap-fake-admin</span> [<span class=\"symbol\">handler</span>]
  (<span class=\"keyword\">fn </span>[<span class=\"symbol\">req</span>] (<span class=\"keyword\">if </span>(<span class=\"symbol\">:admin</span> (<span class=\"symbol\">:params</span> <span class=\"symbol\">req</span>))
              (<span class=\"function\">handler</span> <span class=\"symbol\">req</span>) {<span class=\"symbol\">:status</span> <span class=\"numeric\">401</span> <span class=\"symbol\">:body</span> <span class=\"literal\">&quot;401 Forbidden&quot;</span>})))

(<span class=\"function\">ganelon.web.dyna-routes/setroute!</span> <span class=\"symbol\">:example4</span>
  (<span class=\"function\">compojure.core/GET</span> <span class=\"literal\">&quot;/routing/sample4/*&quot;</span> []
    (<span class=\"function\">-&gt;</span>
      (<span class=\"function\">compojure.core/routes</span>
        (<span class=\"function\">compojure.core/GET</span> <span class=\"literal\">&quot;/routing/sample4/test1&quot;</span> []
          (<span class=\"function\">hiccup.core/html</span> <span class=\"literal\">&quot;A very sensitive information,</span>
<span class=\"literal\">                             requiring admin privileges&quot;</span>))
        (<span class=\"function\">compojure.core/GET</span> <span class=\"literal\">&quot;/routing/sample4/test2&quot;</span> []
          (<span class=\"function\">hiccup.core/html</span> <span class=\"literal\">&quot;A moderately sensitive information,</span>
<span class=\"literal\">                              requiring admin privileges&quot;</span>)))
      <span class=\"symbol\">wrap-fake-admin</span>)))
</pre></div>
"]
        [:p [:a {:href "/routing/sample4/test1" :target "_blank"} "Sample#1 without admin (opens in a new browser tab/window)"]]
        [:p [:a {:href "/routing/sample4/test2" :target "_blank"} "Sample#2 without admin (opens in a new browser tab/window)"]]
        [:p [:a {:href "/routing/sample4/test1?admin=1" :target "_blank"} "Sample#1 with admin param (opens in a new browser tab/window)"]]
        [:p [:a {:href "/routing/sample4/test2?admin=1" :target "_blank"} "Sample#2 with admin param (opens in a new browser tab/window)"]]
]
       [:section#groups {:style "padding-top: 40px"}
        [:h2 "Route groups"]
        [:p "Routes can be grouped into specific subsets by supplying namespace to " [:code "ganelon.web.dyna-routes/setroute!"]
         ", and referenced later on using " [:code "ganelon.web.dyna-routes/route-ns-fn" ] "."]
        [:p "When namespace is not supplied, the " [:code ":default"] " namespace is used."]
        [:p "The example below approaches this using " [:code ":sample-admin"] " group and applying " [:code "wrap-fake-admin"] " (see above) middleware to it."]
        [:p
"<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">ganelon.web.dyna-routes/setroute!</span> <span class=\"symbol\">:sample-admin</span> <span class=\"symbol\">:example51</span>
  (<span class=\"function\">compojure.core/GET</span> <span class=\"literal\">&quot;/routing/sample5/test1&quot;</span> []
      <span class=\"literal\">&quot;Even more sensitive information,</span>
<span class=\"literal\">       requiring admin privileges.&quot;</span>))

(<span class=\"function\">ganelon.web.dyna-routes/setroute!</span> <span class=\"symbol\">:sample-admin</span> <span class=\"symbol\">:example52</span>
  (<span class=\"function\">compojure.core/GET</span> <span class=\"literal\">&quot;/routing/sample5/test2&quot;</span> []
      <span class=\"literal\">&quot;Quite moderately sensitive information,</span>
<span class=\"literal\">       requiring admin privileges.&quot;</span>))

(<span class=\"function\">ganelon.web.dyna-routes/setroute!</span> <span class=\"symbol\">:example5</span>
  (<span class=\"function\">compojure.core/GET</span> <span class=\"literal\">&quot;/routing/sample5/*&quot;</span> []
    (<span class=\"statement\">-&gt; </span>(<span class=\"function\">ganelon.web.dyna-routes/route-ns-fn</span> <span class=\"symbol\">:sample-admin</span>)
        <span class=\"symbol\">wrap-fake-admin</span>)))
</pre></div>"]
        [:p [:a {:href "/routing/sample5/test1" :target "_blank"} "Sample#1 without admin (opens in a new browser tab/window)"]]
        [:p [:a {:href "/routing/sample5/test2" :target "_blank"} "Sample#2 without admin (opens in a new browser tab/window)"]]
        [:p [:a {:href "/routing/sample5/test1?admin=1" :target "_blank"} "Sample#1 with admin param (opens in a new browser tab/window)"]]
        [:p [:a {:href "/routing/sample5/test2?admin=1" :target "_blank"} "Sample#2 with admin param (opens in a new browser tab/window)"]]

        [:p "Route groups can also be referenced when starting ring adapter (e.g. jetty)."]
;        [:p "The example below starts and sets up
;            this demo page. Please notice, that scaffold and i18n admin actions are protected by a wrap-admin middleware."]
;        [:p
;"<!-- HTML generated using hilite.me --><div style=\"background: #ffffff; overflow:auto;width:auto;color:black;background:white;border:solid gray;border-width:.1em .1em .1em .8em;padding:.1em .1em;\">
;<pre style=\"padding:0;margin: 0; line-height: 125%\">(<span style=\"color: #008000; font-weight: bold\">defn </span><span style=\"color: #906030\">start-demo</span> [<span style=\"color: #906030\">port</span>]
;  (<span style=\"color: #0060B0; font-weight: bold\">jetty/run-jetty</span>
;    (<span style=\"color: #0060B0; font-weight: bold\">-&gt;</span>
;      (<span style=\"color: #0060B0; font-weight: bold\">ganelon.web.app/app-handler</span>
;        (<span style=\"color: #0060B0; font-weight: bold\">compojure.core/ANY</span> <span style=\"background-color: #fff0f0\">&quot;/a/i18n*&quot;</span> []
;          (<span style=\"color: #0060B0; font-weight: bold\">wrap-admin</span> (<span style=\"color: #0060B0; font-weight: bold\">webapp/i18n-handler</span>)))
;        (<span style=\"color: #0060B0; font-weight: bold\">compojure.core/ANY</span> <span style=\"background-color: #fff0f0\">&quot;/scaffold/*&quot;</span> []
;          (<span style=\"color: #0060B0; font-weight: bold\">wrap-admin</span> (<span style=\"color: #0060B0; font-weight: bold\">dyna-routes/route-ns-fn</span> <span style=\"color: #A06000\">:scaffold</span>))))
;      <span style=\"color: #906030\">middleware/wrap-x-forwarded-for</span>)
;    {<span style=\"color: #A06000\">:port</span> <span style=\"color: #906030\">port</span> <span style=\"color: #A06000\">:join?</span> <span style=\"color: #906030\">false</span>}))</pre></div>"
;         ]
        [:p "Please take note of the " [:code "ganelon.web.app/app-handler"]
         " function, which sets up a default routing and utility middleware, providing access to keywordized params and making resources from "
         [:code "/public"] " path available. This is a convienience function and you don't have to use it."]
        [:p "In production, sensitive resources can be protected by " [:a {:href "https://github.com/cemerick/friend"} "cemerick/friend"] " or any other Ring-compliant solution."]
       ]
        ]]]))
                                   
                                   
                                   
                                   
                                   
                                   