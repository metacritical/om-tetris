(ns om-tetris.components
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

;; [om.core :as om :include-macros true]
(defn canvas-board [this]
  (let [{:keys [message author score level width height pixel]} (om/props this)]
   (dom/div 
    nil
    (dom/h2 nil "~~ Om-Tetris ~~"
     (dom/div #js{:style #js{:marginBottom "15px"}} 
              (dom/q nil message)))
    (dom/div #js{:id "surrounding-border"}
     (dom/canvas 
      #js{:id "board" :width (* pixel width) :height (* pixel height)
          :style #js{:border "1px solid black"}})
     (dom/h3 nil "Author : " author)
     (dom/h1 #js{:style #js{:width "40%" :margin "0 auto" :float "left"}} 
             "Level : " level)
     (dom/h1 #js{:style #js{:width "40%" :margin "0 auto"}} 
             "Score : " score)))))

(defn clear-canvas [state]
  (let [canvas (.getElementById js/document "board")
        ctx (.getContext canvas "2d") size (@state :pixel)
        width (* size (@state :width)) 
        height (* size (@state :height))]

    (doto ctx
      (.clearRect 0 0 width height)
      (set! -fillStyle "black")
      (.fillRect 0 0 width height))))

(def color-set
  [:yellowgreen :royalblue :orange :steelblue :tomato :fuchsia :darkorchid])

(defn draw-block [column row state]
  (let [canvas (.getElementById js/document "board") 
        ctx (.getContext canvas "2d") size (@state :pixel)
        x (+ (* size (@state :x)) (* column size)) 
        y (+ (* size (@state :y)) (* row size)) 
        color (@state :color)]
    
    (doto ctx
      (set! -fillStyle color)
      (.fillRect x y size size)
      (set! -lineWidth 2)
      (set! -strokeStyle "black")
      (.strokeRect x y size size))))

(defn draw-tetromino [state]
   (doseq [[row array] (map-indexed (fn [i j] [i j]) (@state :tetromino))]
     (doseq [[column pixel] (map-indexed (fn [i j] [i j]) array)]
       (if (pos? pixel)
         (draw-block column row state)))))


(defui app-container
  Object
  (render [this] (canvas-board this)))

(def tetrominoes
  [
   [[1 1]
   [1 1]] 
   [[0 0 0]
    [1 1 1]
    [0 1 0]]
   [[0 1 0]
    [0 1 0]
    [1 1 0]]
   [[0 1 0]
    [0 1 0]
    [0 1 1]]
   [[0 0 1]
    [0 1 1]
    [0 1 0]]
   [[0 1 0]
    [0 1 1]
    [0 0 1]]
   [[1 1 0]
    [0 1 0]
    [0 1 1]]
   [[0 1 1]
    [0 1 0]
    [1 1 0]]
   [[0 0 0 0]
    [1 1 1 1]
    [0 0 0 0]
    [0 0 0 0]]
   ])

(defn init-root [app-state]
  (om/add-root! 
   (om/reconciler {:state app-state}) app-container 
   (gdom/getElement "app")))

