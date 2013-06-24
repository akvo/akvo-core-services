(ns akvo-auth.core
  (:require [clojure.string :refer [lower-case]]
            [compojure [core :refer [GET defroutes]]
                       [handler :refer [site]]
                       [route :refer [not-found resources]]]
            [org.httpkit.server :refer [run-server]]
            [hiccup.middleware :refer [wrap-base-url]]
            [ring.middleware.reload :refer [wrap-reload]]
            [akvo-auth.html :as html]))

(defroutes ^:private routes
  (GET "/" [] (html/home))
  (resources "/")
  (not-found (html/not-found)))

(defn- in-dev-mode? []
  (if-let [dev-mode (lower-case (System/getenv "LOCAL_DEV"))]
    (= (or "true" "1") dev-mode)))

(defn -main [& [port]]
  (let [app (if (in-dev-mode?)
              (wrap-reload (wrap-base-url (site #'routes)))
              (wrap-base-url (site routes)))]
    (run-server app {:port (if port (Integer. port) 8000)})))
