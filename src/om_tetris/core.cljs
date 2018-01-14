(ns om-tetris.core
  (:require [om-tetris.tetris :as tetris]))

(enable-console-print!)

(defn main []
  (prn "Tetris Loaded!!")
  (tetris/start)
  (tetris/anim-loop)
  (tetris/capture-keys))

;;Call main on Window!
(set! (.-onload js/window) main)
