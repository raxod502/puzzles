(ns puzzles.sample.sudoku
  "Sample Sudoku puzzles.")

(def sample-1
  "Puzzle from the 07/08/16 edition of the Daily Camera."
  "
  2.. .9. 43.
  4.3 ..7 ...
  ..7 .4. .1.

  ... .63 .8.
  1.. ... ..4
  .7. 85. ...

  .8. .3. 9..
  ... 7.. 1.6
  .64 .2. ..3
  ")

(def sample-1-solution
  "The solution to sample-1."
  "
  216 598 437
  493 617 258
  857 342 619

  945 163 782
  138 279 564
  672 854 391

  781 436 925
  329 785 146
  564 921 873
  ")

(def sample-2
  "Puzzle from the 07/09/16 edition of the Daily Camera."
  "
  7.. .4. .1.
  .89 ... ...
  ..1 .75 .6.

  9.. .63 ..5
  ... .1. ...
  3.. 95. ..4

  .6. 48. 9..
  ... ... 45.
  .4. .9. ..7
  ")

(def sample-2-solution
  "The solution to sample-2."
  "
  726 349 518
  589 621 743
  431 875 269

  974 263 185
  658 714 392
  312 958 674

  265 487 931
  897 132 456
  143 596 827
  ")
