(ns hubbub.tools.chain2bed
  (:use clojure.java.io hubbub.chain hubbub.utils)
  (:gen-class))

(def max-chain-size 100)

(defn -main [& args]
  (unless (= (count args) 1)
    (fail "Usage: java hubbub.tools.chain2bed input.over.chain"))
  (doseq [c (->> (first args)
                 (reader)
                 (read-chainset)
                 (split-large-chains max-chain-size))]
    (println (chain->bedstr c))))
