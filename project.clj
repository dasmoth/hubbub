(defproject hubbub "0.0.1-SNAPSHOT"
  :description "Conversion tools for making track-hubbish genomics resources."
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [org.clojars.nathell/clojure-jna "1.0.0-SNAPSHOT"]]
  :repl-options {:init (use 'hubbub.utils)}
  :jvm-opts ["-Xmx7G"]
  :aot [hubbub.tools.chain2bed])
