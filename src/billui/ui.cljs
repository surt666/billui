(ns billui.ui
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.data :as data]
            [clojure.string :as string]))

(enable-console-print!)

(def app-state (atom {:menu [{:type :products :name "Products"}
                             {:type :customers :name "Customers"}
                             {:type :accounts :name "Accounts"}
                             {:type :services :name "Services"}
                             {:type :pricebooks :name "Pricebooks"}]
                      :menu-point :products
                      :editproduct nil}))

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
      (dom/div #js {:id "menu" :className "menu"}
        (dom/h2 nil "Menu")
        (apply dom/ul nil
          (om/build-all choose-view (:menu app) {:init-state {:menu-choice menu-choice}}))))))


(defn product-keys []
  (list (dom/option #js {:id "1"} "test")
        (dom/option #js {:id "2"} "test2")
        (dom/option #js {:id "3"} "test3")
        (dom/option #js {:id "4"} "test4")))

(defn find-all-products []
  [{:productid "1101001" :name "Grundpakke"}
   {:productid "1101101" :name "Mellempakke"}
   {:productid "1101201" :name "Fuldpakke"}])

(defn product-line [product owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [viewchan]}]
      (dom/li nil
              (dom/a #js {:href "#"
                          :onClick (fn [e]
                                     (put! viewchan {:editproduct (:productid product)}))} (:productid product)) (dom/span nil (:name product))))))

(defn products-view [app owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [viewchan]}]
      (dom/div #js {:id "view" :className "view"}
               (dom/div #js {:id "find" :className "find"}
                        (dom/label nil "Find")
                        (dom/input #js {:type "text" :ref "findproduct"})
                        (dom/button #js {:onClick (fn [e] (put! viewchan :newproduct))} "New"))
               (apply dom/ul nil
                       (om/build-all product-line (find-all-products) {:init-state {:viewchan viewchan}}))))))

(defn new-product-view [app owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [viewchan]}]
      (let [product (:editproduct app)]
        (.log js/console "Prod " product)
        (dom/div #js {:id "view" :className "view"}
                (dom/h2 nil "New product")
                (dom/label nil "Product id")
                (dom/input #js {:type "text" :ref "productid"})
                (dom/label nil "Name")
                (dom/input #js {:type "text" :ref "name"})
                (dom/label nil "Rollover date")
                (dom/input #js {:type "text" :ref "rolloverdate"})
                (dom/label nil "Rollover product")
                (dom/input #js {:type "text" :ref "rolloverproduct"})
                (dom/label nil "Services")
                (dom/select #js {:size "10" :ref "services"})
                (dom/label nil "Usage")
                (dom/select #js {:size "10" :ref "usage"})
                (dom/label nil "Key")
                (dom/select #js {:ref "key" :value "test2"} (product-keys))
                (dom/label nil "Value")
                (dom/input #js {:type "text" :ref "value"}))))))

(defn customer-keys []
  (list (dom/option #js {:id "1"} "test")
        (dom/option #js {:id "2"} "test2")
        (dom/option #js {:id "3"} "test3")
        (dom/option #js {:id "4"} "test4")))

(defn customer-view [app owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [viewchan]}]
      (dom/div #js {:id "view" :className "view"}
               (dom/h2 nil "Customers")
               (dom/label nil "Customer id")
               (dom/input #js {:type "text" :ref "customerid"})
               (dom/label nil "First Name")
               (dom/input #js {:type "text" :ref "firstname"})
               (dom/label nil "Last Name")
               (dom/input #js {:type "text" :ref "lastname"})
               (dom/label nil "Street address")
               (dom/input #js {:type "text" :ref "streetaddress"})
               (dom/label nil "Zip")
               (dom/input #js {:type "text" :ref "zip"})
               (dom/label nil "City")
               (dom/input #js {:type "text" :ref "city"})
               (dom/label nil "State")
               (dom/input #js {:type "text" :ref "state"})
               (dom/label nil "Country")
               (dom/input #js {:type "text" :ref "country"})
               (dom/label nil "Email")
               (dom/input #js {:type "text" :ref "email"})
               (dom/label nil "Phone")
               (dom/input #js {:type "text" :ref "phone"})
               (dom/label nil "Key")
               (dom/select #js {:ref "key" :value "test2"} (customer-keys))
               (dom/label nil "Value")
               (dom/input #js {:type "text" :ref "value"})))))

(defn accounts-view [app owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [viewchan]}]
      (dom/div #js {:id "view" :className "view"}
        (dom/h2 nil "Accounts")
        (dom/label nil "First Name")
        (dom/input #js {:type "text" :ref "firstname"})
        (dom/label nil "Last Name")
        (dom/input #js {:type "text" :ref "lastname"})))))

(defn service-keys []
  (list (dom/option #js {:id "1"} "test")
        (dom/option #js {:id "2"} "test2")
        (dom/option #js {:id "3"} "test3")
        (dom/option #js {:id "4"} "test4")))

(defn services-view [app owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [viewchan]}]
      (dom/div #js {:id "view" :className "view"}
        (dom/h2 nil "Services")
        (dom/label nil "Service id")
        (dom/input #js {:type "text" :ref "serviceid"})
        (dom/label nil "Type")
        (dom/input #js {:type "text" :ref "type"})
        (dom/label nil "Key")
        (dom/select #js {:ref "key" :value "test2"} (service-keys))
        (dom/label nil "Value")
        (dom/input #js {:type "text" :ref "value"})))))

(defn pricebooks-view [app owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [viewchan]}]
      (dom/div #js {:id "view" :className "view"}
        (dom/h2 nil "Pricebooks")
        (dom/label nil "Name")
        (dom/input #js {:type "text" :ref "name"})))))

(defn view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:viewchan (chan)})
    om/IWillMount
    (will-mount [_]
      (let [viewchan (om/get-state owner :viewchan)]
        (go (while true
              (let [view (<! viewchan)
                    edit (when (map? view) (first (vals map)))
                    view (if (map? view) (first (keys map)) view)]
                (.log js/console (str "log " view edit))
                (when edit
                  (om/transact! app :editproduct
                               (fn [x] edit)))
                (om/transact! app :menu-point
                              (fn [x] view)))))))
    om/IRenderState
    (render-state [this {:keys [viewchan]}]
      (cond
       (= :products (:menu-point app)) (om/build products-view app {:init-state {:viewchan viewchan}})
       (= :newproduct (:menu-point app)) (om/build new-product-view app)
       (= :editproduct (:menu-point app)) (om/build new-product-view app {:state (:editproduct app)})
       (= :customers (:menu-point app)) (om/build customer-view app {:init-state {:viewchan viewchan}})
       (= :accounts (:menu-point app)) (om/build accounts-view app {:init-state {:viewchan viewchan}})
       (= :services (:menu-point app)) (om/build services-view app {:init-state {:viewchan viewchan}})
       (= :pricebooks (:menu-point app)) (om/build pricebooks-view app {:init-state {:viewchan viewchan}})))))

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
      (dom/div #js {:id "container" :className "container"}
               (om/build menu-view app {:init-state {:menu-choice menu-choice}})
               (om/build view app)))))

(om/root app-view app-state
         {:target (. js/document (getElementById "app"))})

(.log js/console "start")

;; (om/root product-view app-state
;;          {:target (. js/document (getElementById "view"))})
