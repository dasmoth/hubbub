table chain
"..."
  (
   string chrom;       "Reference sequence chromosome or scaffold"
   uint   chromStart;  "Start position in chromosome"
   uint   chromEnd;    "End position in chromosome"
   string ori;         "Orientation on dest sequence"
   string srcChrom;    "Source chromosome or scaffold name"
   uint   srcStart;    "Source start position"
   uint	  srcEnd;      "Source end position"
   string srcOri;      "Orientation on source sequence"
   int    blockCount;  "Number of blocks in alignment"
   uint[blockCount] srcStarts;    "Offsets of block starts within source region"
   uint[blockCount] destStarts;   "Offsets of block starts within dest region"
   uint[blockCount] blockLens;  "Block lengths"
 )
