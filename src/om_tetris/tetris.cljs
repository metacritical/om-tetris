(ns om-tetris.tetris
  (:require [om-tetris.components :as components]
            [clojure.pprint :as pprint]
            [clojure.string :as str]))

(declare start new-tetromino rotate update-board)

(defonce app-state
  (atom {:message "Game of Tetris in clojurescript and Om" 
         :author "Pankaj Doharey"}))

(defn draw-board [width height]
  (vec (repeat height (vec (repeat width nil)))))

(defn reset-state []
  (swap! app-state assoc :tetromino (new-tetromino) :score 0 :x 3 :y 0 :level 0
         :width 10 :height 20 :last-time 0 :world-speed 1000 :pixel 20
         :color (name (rand-nth components/color-set))
         :board (draw-board 10 20)))
         
(defn new-tetromino []
  (rand-nth components/tetrominoes))

(defn update-state [key value]
  (swap! app-state assoc key value))
    
(defn get-state [key]
  "Get the value of a key from app-state."
  (@app-state key))

(defn update-board []
  (let [board (atom (draw-board (get-state :width) (get-state :height)))]
    (doseq [[row array] (map-indexed (fn [i j] [i j]) (get-state :tetromino))]
      (doseq [[column pixel] (map-indexed (fn [i j] [i j]) array)]
        (if (pos? pixel)
          (swap! board assoc-in 
                 [(+ row (get-state :y)) (+ column (get-state :x))] 
                 (first (str/capitalize (get-state :color)))))))
    (pprint/print-table (vec (range 0 12)) @board)))

(defn move [direction action]
  (->> direction
   (get-state)
   (action)
   (update-state direction)))

(defn move-left []
  (move :x dec))

(defn move-right []
  (move :x inc))

(defn move-down []
  (move :y inc))

(defn transpose [matrix]
  (apply mapv vector matrix))

(defn flip [matrix]
  (vec (reverse matrix)))

(defn rotate []
  (->>
   (get-state :tetromino)
   (flip)
   (transpose)
   (update-state :tetromino)))

(defn world-speed []
  (get-state :world-speed))

(def key-map
  (fn [event] 
     (.preventDefault event)
     (case event.key
       "ArrowDown" (move-down) 
       "ArrowLeft" (move-left) 
       "ArrowRight" (move-right)
       "ArrowUp" (rotate) 
       "r" (reset-state)
       ;;Reload Page!
       "Escape" (.reload js/window.location) 
       "Do Nothing!")
     (update-board)))

(defn capture-keys []
  (.removeEventListener js/window "keydown" key-map) 
  (.addEventListener js/window "keydown" key-map))

;; Initialized world time with 0 timestamp
;; Not using setInterval because it is inaccurate over a period of 
;; time the best way is to calculate time difference injected through 
;; requestAnimationFrame
(defn anim-loop 
  ([] (anim-loop 0))
  ([timestamp] 
   (components/clear-canvas app-state)
   (components/draw-tetromino app-state)
   (let [deltatime (- timestamp (get-state :last-time))]
     (if (>= deltatime (world-speed))
       (do
         (update-state :last-time timestamp)
         ;; (move-down)
         ))
     (.requestAnimationFrame js/window anim-loop))))

(defn start []
  ;; Reset Tetris app state.
  (reset-state)
  ;; Inject state in app root
  (components/init-root app-state))
