hubbub
======

Tools for preparing track-hub-style datasets.

Building
--------

Install [Leiningen](http://leiningen.org/) then:

      lein deps
      lein uberjar

Compiled jar files will end up in the `target` directory.  If you use the -standalone jar file,
there shouldn't be any other requirements to run it.

Converting over.chain files to bigBed
-------------------------------------

        java -cp target/hubbub-0.0.1-SNAPSHOT-standalone.jar \
           hubbub.tools.chain2bed hg18ToHg19.over.chain >hg18ToHg19.bed
        sort -k1,1 -k2,2n hg18ToHg19.bed >hg18ToHg19.sorted.bed
        bedToBigBed -type=bed3+9 -as=as/chain.as hg18ToHg19.sorted.bed chrom.sizes hg18ToHg19.bb