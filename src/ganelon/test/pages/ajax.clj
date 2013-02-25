;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns ganelon.test.pages.ajax
  (:require [ganelon.web.dyna-routes :as dyna-routes]
            [ganelon.web.widgets :as widgets]
            [ganelon.web.actions :as actions]
            [ganelon.web.ui-operations :as ui]
            [hiccup.page :as hiccup]
            [hiccup.core :as h]
            [hiccup.util]
            [noir.response :as response]
            [noir.session :as sess]
            [compojure.core :as compojure]
            [ganelon.test.pages.common :as common]))


(actions/defjsonaction "ajax/sample0" [msg]
  (ui/fade "#sample0" (hiccup.util/escape-html msg)))

(dyna-routes/setroute! :ajax-sample1
  (compojure/ANY "/a/ajax/sample1" []
    (response/json
      [{:type "dom-fade"
        :id "#alink1"
        :value "Polo!"}])))

(defn microblog-entry-widget [e]
  (widgets/with-div
    [:h4 (hiccup.util/escape-html (:title e))]
    (widgets/action-link "microblog-entry-edit"
      {:id (:id e)} {:class "btn"} "Edit")))

(defn microblog-edit-widget [e]
  (widgets/with-div
    (widgets/action-form "microblog-entry-update" {} {:class "well"}
      [:input {:type "hidden" :name "id" :value (:id e)}]
      [:span "Title: "]
      [:input {:type "text" :name "title" :value (:title e)}] [:br]
      [:button {:type "submit" :class "btn"} "Update entry"])))

(defn microblog-widget []
  (widgets/with-div
    [:h3 "My microblog"]
    ;the form
    (widgets/action-form "microblog-entry-add" {} {:class "well"}
      [:span "Title: "] [:input {:type "text" :name "title"}] [:br]
      [:button {:type "submit" :class "btn"} "Post entry"])
    ;display existing entries
    (for [e (sort (comparator :id) (map second (or (sess/get :microblog) {})))]
      (microblog-entry-widget e) [:br] [:br])  [:br]
    (widgets/action-link "microblog-clear" {} {:class "btn"} "Remove all")))

(actions/defwidgetaction "microblog-entry-add" [title]
  (let [id (str (java.util.UUID/randomUUID))]
    (sess/put! :microblog
      (assoc (or (sess/get :microblog) {}) id {:id id :title title})))
  (microblog-widget))

(actions/defwidgetaction "microblog-entry-edit" [id]
  (microblog-edit-widget (get (sess/get :microblog) id)))

(actions/defwidgetaction "microblog-entry-update" [id title]
  (sess/put! :microblog
    (assoc (or (sess/get :microblog) {}) id {:id id :title title}))
  (microblog-entry-widget {:id id :title title}))

(actions/defwidgetaction "microblog-clear" []
  (sess/put! :microblog {})
  (microblog-widget))

(dyna-routes/defpage "/ajax" []
  (common/layout
    (common/banner "AJAX")
    [:div.container {:style "padding-top: 30px;"}
     [:div.row [:div#sidenav.span3 [:ul.nav.nav-list.affix-top.nav-pills.nav-stacked {:style "margin-top: 0px; top: 50px;" :data-spy "affix" :data-offset-top "200"}
                                    [:li.active [:a {:href "#actions"} "Actions"]]
                                    [:li [:a {:href "#widgets"} "Widgets"]]
                                    [:li [:a {:href "#operations"} "Operations"]]
                                    [:li [:a {:href "#queue"} "Operation queue"]]
                                    ]]
      [:div.span9 {}
       [:p "The key feature provided by Ganelon and the main purpose for its existence is a framework allowing
             server-side code to finely control the contents and behaviour of browser's page, while still having
             the ability to render full page contents using the same code."]
       [:section#actions [:p "There are other popular frameworks following this principle, such as: <a href='http://vaadin.com' target='_blank'>Vaadin
        (Java, widget-oriented, only client-side rendering)</a>, <a href='http://common-lisp.net/project/cl-weblocks/'
        target='_blank'>Weblocks (Common LISP, continuations-based)</a> or
        <a href='http://www.zkoss.org/' target='_blank'>ZK (XML oriented, only client-side rendering) </a> and
        <a href='http://wicket.apache.org/' target='_blank'>Wicket (just like Vaadin, but with less complex widgets)</a>."]
        [:h2 "Actions"]
        [:p "Action in its simplest is an AJAX-enabled web request handler, which returns a list of parametrised operations to a thin
            layer of JavaScript running in the browser."]
        [:div {:style "text-align: center"} [:img {:src "/ganelon-demo/img/arch-server-side.png"}]]
        [:p "One example of such operation can be an update of a certain page fragment or fragments. Another - displaying of a
            modal window or a Growl-style notification. But most importantly, it is very easy to provide in additional actions -
            just by referencing another .js file."]
;        [:div {:style "text-align: center"} [:img {:src "/ganelon-demo/img/arch-operations.png"}]]

        [:p "Code defining DIV#sample0 and a link invoking the action:"]
        [:p "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">[<span class=\"symbol\">:div#sample0.alert.alert-success</span> <span class=\"literal\">&quot;I&#39;m a DIV, update me with AJAX!&quot;</span>]
[<span class=\"symbol\">:p</span> (<span class=\"function\">widgets/action-link</span> <span class=\"literal\">&quot;ajax/sample0&quot;</span> {<span class=\"symbol\">:msg</span> <span class=\"literal\">&quot;There ya go!&quot;</span>} {} [<span class=\"symbol\">:span</span> <span class=\"literal\">&quot;Sure!&quot;</span>])]
</pre></div>"
         ]
        [:p "And the code defining Ganelon action is as follows:"]
        [:p "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">actions/defjsonaction</span> <span class=\"literal\">&quot;ajax/sample0&quot;</span> [<span class=\"symbol\">msg</span>]
  (<span class=\"function\">ui/fade</span> <span class=\"literal\">&quot;#sample0&quot;</span> (<span class=\"function\">hiccup.util/escape-html</span> <span class=\"symbol\">msg</span>)))</pre></div>"]
        [:p "Try it in action:"]
        [:div#sample0.alert.alert-success "I'm a DIV, update me with AJAX!"]
        [:p (widgets/action-link "ajax/sample0" {:msg "There ya go!"} {} [:span "Sure!"])]
        [:h3 "Referencing actions - server-side"]
        [:p "To generate HTML/JavaScript code invoking an action, we can use one of the provided macros/functions:"]
        [:ul
         [:li [:code "ganelon.web.widgets/action-link"] " - a simple link, which adds widget-id parameter automatically (you can overwrite it though)"]
         [:li [:code "ganelon.web.widgets/action-button"] " - a button, which works as link above"]
         [:li [:code "ganelon.web.widgets/action-form"] " - render a form, which will invoke action on its submit"]
         ]
        [:p "The links for actions above are rendered using the simplest code of all:"]
        [:p "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">widgets/action-link</span> <span class=\"literal\">&quot;ajax/sample0&quot;</span> {<span class=\"symbol\">:msg</span> <span class=\"literal\">&quot;There ya go!&quot;</span>} {} [<span class=\"symbol\">:span</span> <span class=\"literal\">&quot;Sure!&quot;</span>])</pre></div>"]
        [:h3 "Referencing actions - client-side"]
        [:p "If you include " [:code "ganelon.web.app/javascript-handler"] " in your ring routes,
        and reference <a href='/ganelon/actions.js'>/ganelon/actions.js</a> in your web page, all the defined actions will be available to you as JavaScript functions:"]
        [:input#input1 {:type "text" :style "width: 100%" :placeholder "Provide a value..."}]
        [:a {:href "javascript:GanelonAction.ajax_sample0($('#input1').val(), null, function(data) { alert('done: ' + JSON.stringify(data));});"}
         "Invoke: " [:p "<!-- HTML generated using hilite.me -->
<div class=\"code\">
<pre class=\"code\">GanelonAction.ajax_sample0($(<span class=\"literal\">&#39;#input1&#39;</span>).val(), <span class=\"keyword\">null</span>,
                       <span class=\"keyword\">function</span>(data) { alert(<span class=\"literal\">&#39;done: &#39;</span> + JSON.stringify(data)););});</pre></div>"] ]

        [:p "Just as we have referenced
            " [:code "/ajax/sample0"] " action using " [:code "ganelon.web.widgets/action-link"] ", we can reference it using JavaScript function "
         [:code "GanelonAction.ajax_sample0"] " and integrate it with our client-side logic."]

       [:h3 "Custom actions"]
        [:p "Technically, anything that returns JSON string can be an action in Ganelon:"]
        [:p "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">dyna-routes/setroute!</span> <span class=\"symbol\">:ajax-sample1</span>
  (<span class=\"function\">compojure/ANY</span> <span class=\"literal\">&quot;/a/ajax/sample1&quot;</span> []
    (<span class=\"function\">response/json</span>
      [{<span class=\"symbol\">:type</span> <span class=\"literal\">&quot;dom-fade&quot;</span>
        <span class=\"symbol\">:id</span> <span class=\"literal\">&quot;#sample0&quot;</span>
        <span class=\"symbol\">:value</span> <span class=\"literal\">&quot;Polo!&quot;</span>}])))</pre></div>"]
        [:p "Try it: "]
        [:p (widgets/action-link "ajax/sample1" {} {:id "alink1"} "Marco?")]

        ]
       [:section#widgets {:style "padding-top: 40px"}
        [:h2 "Widgets"]
        [:p "Widget in Ganelon framework simply defines a scope in a browser's DOM tree - usually a DIV, that is
             marked with an identificator - either fixed or randomly generated."]

        [:div {:style "text-align: center"} [:img {:src "/ganelon-demo/img/arch-web-page.png"}]]

        [:p "This widget's identificator can be accessed by internal action references and used to seamlessly update div contents."]
        [:p "When we want to mark a DOM tree element, we can use one of the following macros:"]
        [:ul
          [:li [:code "ganelon.web.widgets/with-div"] " - generate random widget UID, and wrap its body with a DIV tag."]
          [:li [:code "ganelon.web.widgets/with-span"] " - generate random widget UID, and wrap its body with a SPAN tag."]
          [:li [:code "ganelon.web.widgets/with-widget"] " - take widget UID as parameter, and wrap its body with a DIV tag."]
          [:li [:code "ganelon.web.widgets/with-set-id"] " - just establish taken widget id as a scope, and evalute the body."]
          [:li [:code "ganelon.web.widgets/with-id"] " - just establish generated widget id as a scope, and evalute the body."]]
        [:h3 "Example - microblog"]
        [:p "With widgets, we can provide fully functional AJAX UI fragments, with a distinct separation between presentation (widgets) and
             UI logic (actions). The example below provides a simple microblogging engine (with entries stored in user's session, so don't worry!"]
        [:h4 "Widgets"]
        [:p "First we define the widget displaying single entry, with an Edit button:"]
        [:p
"<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"keyword\">defn </span><span class=\"symbol\">microblog-entry-widget</span> [<span class=\"symbol\">e</span>]
  (<span class=\"function\">widgets/with-div</span>
    [<span class=\"symbol\">:h4</span> (<span class=\"function\">hiccup.util/escape-html</span> (<span class=\"symbol\">:title</span> <span class=\"symbol\">e</span>))]
    (<span class=\"function\">widgets/action-link</span> <span class=\"literal\">&quot;microblog-entry-edit&quot;</span>
      {<span class=\"symbol\">:id</span> (<span class=\"symbol\">:id</span> <span class=\"symbol\">e</span>)} {<span class=\"symbol\">:class</span> <span class=\"literal\">&quot;btn&quot;</span>} <span class=\"literal\">&quot;Edit&quot;</span>)))
</pre></div>"]
        [:p "Please note, that we have used " [:code "with-div"] " macro to establish a widget, and a " [:code "widget-update-link"]
         " function to render a button that enables edit mode. Widget's id is passed silently as a parameter."]
       [:p "An entry edit form:"]
       [:p
"<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"keyword\">defn </span><span class=\"symbol\">microblog-edit-widget</span> [<span class=\"symbol\">e</span>]
  (<span class=\"function\">widgets/with-div</span>
    (<span class=\"function\">widgets/action-form</span> <span class=\"literal\">&quot;microblog-entry-update&quot;</span> {} {<span class=\"symbol\">:class</span> <span class=\"literal\">&quot;well&quot;</span>}
      [<span class=\"symbol\">:input</span> {<span class=\"symbol\">:type</span> <span class=\"literal\">&quot;hidden&quot;</span> <span class=\"symbol\">:name</span> <span class=\"literal\">&quot;id&quot;</span> <span class=\"symbol\">:value</span> (<span class=\"symbol\">:id</span> <span class=\"symbol\">e</span>)}]
      [<span class=\"symbol\">:span</span> <span class=\"literal\">&quot;Title: &quot;</span>]
      [<span class=\"symbol\">:input</span> {<span class=\"symbol\">:type</span> <span class=\"literal\">&quot;text&quot;</span> <span class=\"symbol\">:name</span> <span class=\"literal\">&quot;title&quot;</span> <span class=\"symbol\">:value</span> (<span class=\"symbol\">:title</span> <span class=\"symbol\">e</span>)}] [<span class=\"symbol\">:br</span>]
      [<span class=\"symbol\">:button</span> {<span class=\"symbol\">:type</span> <span class=\"literal\">&quot;submit&quot;</span> <span class=\"symbol\">:class</span> <span class=\"literal\">&quot;btn&quot;</span>} <span class=\"literal\">&quot;Update entry&quot;</span>])))
</pre></div>"]
       [:p "The new element here is " [:code "widget-form"] ", which renders a form invoking AJAX action on submit."]
       [:p "Then, a widget with form for new entry and a list of existing ones:"]

       [:p
"<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"keyword\">defn </span><span class=\"symbol\">microblog-widget</span> []
  (<span class=\"function\">widgets/with-div</span>
    [<span class=\"symbol\">:h3</span> <span class=\"literal\">&quot;My microblog&quot;</span>]
    <span class=\"comment\">;the form</span>
    (<span class=\"function\">widgets/action-form</span> <span class=\"literal\">&quot;microblog-entry-add&quot;</span> {} {<span class=\"symbol\">:class</span> <span class=\"literal\">&quot;well&quot;</span>}
      [<span class=\"symbol\">:span</span> <span class=\"literal\">&quot;Title: &quot;</span>] [<span class=\"symbol\">:input</span> {<span class=\"symbol\">:type</span> <span class=\"literal\">&quot;text&quot;</span> <span class=\"symbol\">:name</span> <span class=\"literal\">&quot;title&quot;</span>}] [<span class=\"symbol\">:br</span>]
      [<span class=\"symbol\">:button</span> {<span class=\"symbol\">:type</span> <span class=\"literal\">&quot;submit&quot;</span> <span class=\"symbol\">:class</span> <span class=\"literal\">&quot;btn&quot;</span>} <span class=\"literal\">&quot;Post entry&quot;</span>])
    <span class=\"comment\">;display existing entries    </span>
    (<span class=\"statement\">for </span>[<span class=\"symbol\">e</span> (<span class=\"statement\">sort </span>(<span class=\"statement\">comparator </span><span class=\"symbol\">:id</span>) (<span class=\"statement\">map second </span>(<span class=\"statement\">or </span>(<span class=\"function\">sess/get</span> <span class=\"symbol\">:microblog</span>) {})))]
      (<span class=\"function\">microblog-entry-widget</span> <span class=\"symbol\">e</span>) [<span class=\"symbol\">:br</span>] [<span class=\"symbol\">:br</span>])  [<span class=\"symbol\">:br</span>]
    (<span class=\"function\">widgets/action-link</span> <span class=\"literal\">&quot;microblog-clear&quot;</span> {} {<span class=\"symbol\">:class</span> <span class=\"literal\">&quot;btn&quot;</span>} <span class=\"literal\">&quot;Remove all&quot;</span>)))
</pre></div>"]
       [:p "No new components were used, but please take note on the widget hierarchy - the main widget contains all of the sub-widgets, nesting their respective widget ids."]

       [:h4 "Actions"]
       [:p "Now, we can define corresponding actions. Since they are div-oriented actions, they just invoke necessary widget function."]
       [:p "Action for adding the entry updates session, and returns new version of the widget's HTML code:"]
       [:p
"<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">actions/defwidgetaction</span> <span class=\"literal\">&quot;microblog-entry-add&quot;</span> [<span class=\"symbol\">title</span>]
  (<span class=\"keyword\">let </span>[<span class=\"symbol\">id</span> (<span class=\"statement\">str </span>(<span class=\"function\">java.util.UUID/randomUUID</span>))]
    (<span class=\"function\">sess/put!</span> <span class=\"symbol\">:microblog</span>
      (<span class=\"statement\">assoc </span>(<span class=\"statement\">or </span>(<span class=\"function\">sess/get</span> <span class=\"symbol\">:microblog</span>) {}) <span class=\"symbol\">id</span> {<span class=\"symbol\">:id</span> <span class=\"symbol\">id</span> <span class=\"symbol\">:title</span> <span class=\"symbol\">title</span>})))
  (<span class=\"function\">microblog-widget</span>))
</pre></div>"]
       [:p "Action that enables edit of an entry simply replaces one div with another in-place:"]
       [:p
"<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">actions/defwidgetaction</span> <span class=\"literal\">&quot;microblog-entry-edit&quot;</span> [<span class=\"symbol\">id</span>]
  (<span class=\"function\">microblog-edit-widget</span> (<span class=\"statement\">get </span>(<span class=\"function\">sess/get</span> <span class=\"symbol\">:microblog</span>) <span class=\"symbol\">id</span>)))
</pre></div>"
        ]
       [:p "Entry content update action replaces the view widget back:"]
       [:p
"<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">actions/defwidgetaction</span> <span class=\"literal\">&quot;microblog-entry-update&quot;</span> [<span class=\"symbol\">id</span> <span class=\"symbol\">title</span>]
  (<span class=\"function\">sess/put!</span> <span class=\"symbol\">:microblog</span>
    (<span class=\"statement\">assoc </span>(<span class=\"statement\">or </span>(<span class=\"function\">sess/get</span> <span class=\"symbol\">:microblog</span>) {}) <span class=\"symbol\">id</span> {<span class=\"symbol\">:id</span> <span class=\"symbol\">id</span> <span class=\"symbol\">:title</span> <span class=\"symbol\">title</span>}))
  (<span class=\"function\">microblog-entry-widget</span> {<span class=\"symbol\">:id</span> <span class=\"symbol\">id</span> <span class=\"symbol\">:title</span> <span class=\"symbol\">title</span>}))
</pre></div>"]
       [:p "And finally, an action to clear all entries in session and update widget (with subwidgets):"]
       [:p
"<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">actions/defwidgetaction</span> <span class=\"literal\">&quot;microblog-clear&quot;</span> []
  (<span class=\"function\">sess/put!</span> <span class=\"symbol\">:microblog</span> {})
  (<span class=\"function\">microblog-widget</span>))
</pre></div>"       
        ]
       [:p "Try it here:"]
       [:div.box-widget {:style "padding: 10px;"} (microblog-widget)]
        ]
       [:section#operations {:style "padding-top: 40px"}
        [:h2 "Operations"]
        [:p "An operation is a JSON object, trasmitted from server-side (Clojure), to client-side (JavaScript),
             usually performing side effects - from simple operations such as DOM manipulation to communication with
             complex application-tailored browser-side constructs."]
        [:div {:style "text-align: center"} [:img {:src "/ganelon-demo/img/arch-operations.png"}]]
        [:p "Client-side Ganelon framework expects to receive JSON array with objects. They are directed to specific
            JavaScript functions using " [:code "type:"] " field, for example, operations data can be a vector with Clojure map defined below and sent from Ganelon action:"]
        [:p "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">[{<span class=\"symbol\">:type</span> <span class=\"literal\">&quot;dom-html&quot;</span>
  <span class=\"symbol\">:id</span> <span class=\"literal\">&quot;#alink1&quot;</span>
  <span class=\"symbol\">:value</span> <span class=\"literal\">&quot;Polo!&quot;</span>}]</pre></div>"]
        [:p "This operation would invoke " [:code "dom-html"] " dispatcher, registered with " [:code "Ganelon.registerOperation"] ":"]
        [:p "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">Ganelon.registerOperation(<span class=\"literal\">&#39;dom-html&#39;</span>, <span class=\"keyword\">function</span>(o) {$(o.id).html(o.value);});
</pre></div>"]
        [:p "It is possible and adviced to define own operation types, for example "]

[:p "Ganelon framework comes with a set of prebuilt AJAX components and operations, including mapping for most of the "
 [:a {:href "http://api.jquery.com/category/manipulation/"} "jQuery Manipulation"] " methods, for example:"]
[:ul
 [:li [:code "ganelon.web.widgets/div-replace"] " - replace content of a div named by :id keyword"]
 [:li [:code "ganelon.web.widgets/div-fade"] " - replace content of a div named by :id keyword - with a fade effect"]
 [:li [:code "ganelon.web.widgets/refresh-page"] " - reload page contents"]
 [:li [:code "ganelon.web.widgets/modal"] " - display modal window (using Twitter Bootstrap modal plugin)"]
 [:li [:code "ganelon.web.widgets/remove-modal"] " - remove modal window (using Twitter Bootstrap modal plugin)"]
 [:li [:code "ganelon.web.widgets/notification"] " - display Growl-style notification"]]
        ]
       [:section#queue {:style "padding-top: 40px"}
        [:h2 "Operation queue"]
        [:p "A real life operation does not always return all side effects in the end. Sometimes it is more convienent
         and understable, to apply certain effect before the final result."]
        [:p "With " [:code "ganelon.web.actions/put-operation!"] " it is possible to achieve that in an idiomatic way.
        The code below produces two side effects:"]
        [:ul
          [:li "Two Growl-style notifications, put into queue with " [:code "put-operation!"] " calls."]
          [:li "Replacement with fade effect of a DOM tree fragment."]]

        [:p "<!-- HTML generated using hilite.me --><div class=\"code\"><pre class=\"code\">(<span class=\"function\">actions/defjsonaction</span> <span class=\"literal\">&quot;demo-action&quot;</span> [<span class=\"symbol\">msg</span> <span class=\"symbol\">widget-id</span>]
  (<span class=\"function\">actions/put-operation!</span> (<span class=\"function\">ui/notification</span> <span class=\"literal\">&quot;Success&quot;</span>
      (<span class=\"function\">h/html</span> <span class=\"literal\">&quot;Message set to: &quot;</span> [<span class=\"symbol\">:b</span> (<span class=\"function\">hiccup.util/escape-html</span> <span class=\"symbol\">msg</span>)])))
  (<span class=\"function\">actions/put-operation!</span> (<span class=\"function\">ui/notification</span> <span class=\"literal\">&quot;Another message&quot;</span>
      (<span class=\"function\">h/html</span> <span class=\"literal\">&quot;Widget-id is: &quot;</span> [<span class=\"symbol\">:b</span> (<span class=\"function\">hiccup.util/escape-html</span> <span class=\"symbol\">widget-id</span>)])))
  (<span class=\"function\">ui/fade</span> <span class=\"symbol\">widget-id</span> (<span class=\"function\">demo1-widget</span> <span class=\"symbol\">msg</span>)))
</pre></div>
"]
        [:p "Try it here:"]
        [:div.box-widget (common/demo1-widget "Operations queue example")]

        ]

        ]]]))





