(ns ganelon.test.demo
  (:gen-class)
  (:require [ganelon.test.demo-pages]
            [ring.middleware.stacktrace]
            [ring.middleware.reload]
            [ring.adapter.jetty :as jetty]
            [ganelon.web.middleware :as middleware]
            [ganelon.web.app :as webapp]
            [noir.session :as sess]))

(defonce SERVER (atom nil))

(defn start-demo [port]
  (jetty/run-jetty
    (->
      (ganelon.web.app/app-handler
        (ganelon.web.app/javascript-actions-route))
      middleware/wrap-x-forwarded-for
      (ring.middleware.stacktrace/wrap-stacktrace)
      (ring.middleware.reload/wrap-reload {:dirs ["src/ganelon/test/pages"]}))
    {:port port :join? false}))

(defn -main [& m]
  (let [port (Integer. (or (first m) (get (System/getenv) "PORT" "8097")))]
    (swap! SERVER (fn [s] (when s (.stop s)) (start-demo port)))))

