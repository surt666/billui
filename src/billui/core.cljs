(ns billui.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.data :as data]
            [clojure.string :as string]))

(enable-console-print!)

(def app-state (atom {:menu [{:type :products :name "Products"}
                             {:type :users :name "Users"}]
                      :menu-point :products}))

(defn choose-view [menupoint owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [menu-choice]}]
      (dom/li nil
              (dom/a #js {:href "#"
                          :onClick (fn [e] (put! menu-choice (:type @menupoint)))} (:name menupoint))))))

(defn menu-view [app owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [menu-choice]}]
      (dom/div #js {:id "menu"}
        (dom/h2 nil "Menu")
        (apply dom/ul nil
          (om/build-all choose-view (:menu app) {:init-state {:menu-choice menu-choice}}))))))

(defn product-view [app owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [menu-choice]}]
      (dom/div #js {:id "view"}
        (dom/h2 nil "Products")
        (dom/label nil "Product id")
        (dom/input #js {:type "text" :ref "productid"})
        (dom/label nil "Name")
        (dom/input #js {:type "text" :ref "name"})))))

(defn users-view [app owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [menu-choice]}]
      (dom/div #js {:id "view"}
        (dom/h2 nil "Users")
        (dom/label nil "First Name")
        (dom/input #js {:type "text" :ref "firstname"})
        (dom/label nil "Last Name")
        (dom/input #js {:type "text" :ref "lastname"})))))

(defn view [app owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [menu-choice]}]
      (if (= :products (:menu-point app))
        (om/build product-view app)
        (om/build users-view app)))))

(defn app-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:menu-choice (chan)})
    om/IWillMount
    (will-mount [_]
      (let [menu-choice (om/get-state owner :menu-choice)]
        (go (while true
              (let [choice (<! menu-choice)]
                (.log js/console (str "log" choice))
                (om/transact! app :menu-point
                              (fn [x] choice)))))))
    om/IRenderState
    (render-state [this {:keys [menu-choice]}]
      (dom/div #js {:id "app"}
               (om/build menu-view app {:init-state {:menu-choice menu-choice}})
               (om/build view app)))))

(om/root app-view app-state
         {:target (. js/document (getElementById "app"))})

(.log js/console "start")

;; (om/root product-view app-state
;;          {:target (. js/document (getElementById "view"))})
