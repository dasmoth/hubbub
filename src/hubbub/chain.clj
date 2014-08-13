(ns hubbub.chain
  (:use clojure.java.io hubbub.utils)
  (:require [clojure.string :as str]))

(defn- split-string [s exp]
  (if (zero? (count s))
      '()
      (seq (.split s exp))))

(defn- clean-chr [c]
  (if (.startsWith c "chr")
    (subs c 3)
    c))

(defn- read-blocks [block-lines]
  (loop [lines block-lines
         blocks []
         cum-src 0
         cum-dest 0]
    (if (seq lines)
      (let [[bs x y] (map parse-int (str/split (first lines) #"\s"))]
        (recur (rest lines)
               (conj blocks [cum-src cum-dest bs])
               (+ cum-src bs (or x 0))
               (+ cum-dest bs (or y 0))))
      blocks)))
               

(defn- read-chain [chain-line rest-lines ]
   (let [[_ chain-score
	  src-chr src-len src-ori src-min src-max
	  dest-chr dest-len dest-ori dest-min dest-max
	  chain-id] (split-string chain-line " ")

          blocks (read-blocks rest-lines)]
     {:srcChr  (clean-chr src-chr)
      :srcOri  src-ori
      :srcMin  (parse-int src-min)
      :srcMax  (parse-int src-max)
      :destChr (clean-chr dest-chr)
      :destOri dest-ori
      :destMin (parse-int dest-min)
      :destMax (parse-int dest-max)
      :blocks  (if (empty? blocks)
                 [0 0 (- (parse-int src-max) (parse-int src-min))]
                 blocks)}))

(defn- read-chainset* [s]  
  (when (seq s)
    (let [[[chain-line] [next-line]] s]
      (if (or (not next-line)
              (.startsWith next-line "chain"))
        (cons (read-chain chain-line [])
              (lazy-seq (read-chainset* (rest s))))
        (cons (read-chain chain-line (second s))
              (lazy-seq (read-chainset* (rest (rest s)))))))))

(defn- chain-line? [^String l]
  (not (or (empty? l)
           (.startsWith l "#"))))

(defn read-chainset
  "Return a lazy seq of chains from reader `r`"
  [r]
  (->> (line-seq r)
       (filter chain-line?)
       (partition-by (fn [line]
                       (if (.startsWith line "chain") line)))
       (read-chainset*)))

(defn split-chain
  "Split a single chain into multiple chains consisting of n or fewer blocks"
  [n chain]
  (for [blocks (partition n n [] (:blocks chain))
        :let [src-inv?  (= (:srcOri chain) "-")
              dest-inv? (= (:destOri chain) "-")
              fblock (first blocks)
              lblock (last blocks)
              [src-pmin dest-pmin first-len] fblock
              [src-pmax dest-pmax last-len] lblock]]
    {:srcChr   (:srcChr chain)
     :srcOri   (:srcOri chain)
     :srcMin   (if src-inv?
                 (- (:srcMax chain) src-pmax last-len)
                 (+ (:srcMin chain) src-pmin))
     :srcMax   (if src-inv?
                 (- (:srcMax chain) src-pmin)
                 (+ (:srcMin chain) src-pmax last-len))
     :destChr  (:destChr chain)
     :destOri  (:destOri chain)
     :destMin  (if dest-inv?
                 (- (:destMax chain) dest-pmax last-len)
                 (+ (:destMin chain) dest-pmin))
     :destMax  (if dest-inv?
                 (- (:destMax chain) dest-pmin)
                 (+ (:destMin chain) dest-pmax last-len))
     :blocks   (vec (for [[s d l] blocks]
                      [(- s src-pmin)
                       (- d dest-pmin)
                       l]))}))

(defn split-large-chains 
  "Filter a sequence of chains, splitting any which contain more than b blocks"
  [n chains]
  (mapcat (fn [chain]
            (if (> (count (:blocks chain)) n)
              (split-chain n chain)
              [chain]))
          chains))

(defn chain->bedstr 
  "Convert a chain to a bed-file entry (string)."
  [rcd]
  (str/join "\t" 
    [(:destChr rcd)
     (:destMin rcd)
     (:destMax rcd)
     (:destOri rcd)
     (:srcChr rcd)
     (:srcMin rcd)
     (:srcMax rcd)
     (:srcOri rcd)
     (count (:blocks rcd))
     (str/join "," (map first  (:blocks rcd)))
     (str/join "," (map second (:blocks rcd)))
     (str/join "," (map third  (:blocks rcd)))]))


(comment 

(with-outfile "hg18ToHg19.bed"
	(doseq [r (->> (read-chainset-from-file "hg18ToHg19.over.chain")
	               (split-large-chains 50))]
	  (println (chain2bed r))))

)
