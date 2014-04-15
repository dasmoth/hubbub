(ns hubbub.utils
  (:use net.n01se.clojure-jna)
  (:use clojure.java.io))

(defn cd
  "Change directory"
  [dir]
  (let [rc (jna-invoke Integer c/chdir dir)]
    (when (not= rc 0)
      (throw (new Exception (str "Couldn't change directory, errno=" rc))))))

(defn pwd
  "Return current working directory"
  []
  (jna-invoke String c/getcwd 'nil 0))

(defn parse-int [str]
  (Integer/parseInt str))

(defn parse-double [str]
  (Double/parseDouble str))

(defmacro with-outfile 
  "Execute body with *out* bound to a writer of f"
  [f & body]
  (let [fh (gensym)]
    `(with-open [~fh (writer ~f)]
       (binding [*out* ~fh]
	 ~@body))))

(defn mapconj [m k v]
  (assoc m k (conj (or (get m k) []) v)))

(defn map-by [f xs]
  (reduce (fn [m x]
	    (mapconj m (f x) x))
	  {} xs))

(defmacro unless [condition & body]
    `(when (not ~condition)
       ~@body))

(defn fail [& args]
  (binding [*out* *err*]
    (apply println args)
    (System/exit 1)))


(defn to-byte-buffer 
  "Return a ByteBuffer containing an mmap of f"
  [f]
  (let [channel (.getChannel (new java.io.FileInputStream f))
	size (.size channel)]
    (.map channel java.nio.channels.FileChannel$MapMode/READ_ONLY 0 size)))



(defn toke 
  "Split s into tokens, as from a java.util.StringTokenizer"
  [s]
  (loop [st    (java.util.StringTokenizer. s)
        toks  []]
    (if (.hasMoreTokens st)
      (recur st (conj toks (.nextToken st)))
      toks)))

(defn rand-pick [l]
  (nth l (rand-int (count l))))


(defn third [s]
  (first (next (next s))))

(defn fourth [s]
  (first (next (next (next s)))))
