(ns puzzles.sample.kenken
  "Sample KenKen puzzles.")

(def sample-4x4
  "4x4 puzzle from the 07/09/16 edition of the Daily Camera."
  "
  +-------+---+---+
  | 2   - | 2 | 12|
  +-------+---+   |
  | 24  * |     * |
  +---+   +-------+
  | 2 |   | 3   - |
  |   +---+---+---+
  | / | 7   + | 2 |
  +---+-------+---+
  ")

(def sample-4x4*
  "Same as sample-4x4, but in the alternate notation accepted
  by the parser in this library."
  "
  +---+-+-+
  |   | | |
  +---+-+ |
  |   |   |
  +-+ +---+
  | | |   |
  | +-+-+-+
  | |   | |
  +-+---+-+

  2- . 2 12*
  24* . . .
  2/ . 3- .
  . 7+ . 2
  ")

(def sample-4x4-solution
  "The solution to sample-4x4."
  "
  3124
  4213
  2341
  1432
  ")

(def sample-6x6
  "Puzzle from the 07/09/16 edition of the Daily Camera."
  "
  +---+-------+---+---+---+
  | 1 | 2   / | 3 | 5 | 1 |
  |   +---+---+   +---+   |
  | - | 5 | 5 | - | 24| - |
  +---+   |   +---+   +---+
  | 2 | - | + | 5 | * | 5 |
  |   +---+---+---+---+   |
  | / | 12| 15  + | 1 | - |
  +---+   |   +---+   +---+
  |     + |   | 2 | - | 2 |
  +---+---+---+   +---+---+
  | 6 | 1   - | / | 3   / |
  +---+-------+---+-------+
  ")

(def sample-6x6*
  "Same as sample-6x6, but in the alternate notation accepted
  by the parser in this library."
  "
  +-+---+-+-+-+
  | |   | | | |
  | +-+-+ +-+ |
  | | | | | | |
  +-+ | +-+ +-+
  | | | | | | |
  | +-+-+-+-+ |
  | | |   | | |
  +-+ | +-+ +-+
  |   | | | | |
  +-+-+-+ +-+-+
  | |   | |   |
  +-+---+-+---+

  1- 2/ . 3- 5 1-
  . 5- 5+ . 24* .
  2/ . . 5 . 5-
  . 12+ 15+ . 1- .
  . . . 2/ . 2
  6 1- . . 3/ .
  ")

(def sample-6x6-solution
  "The solution to sample-6x6."
  "
  321654
  412365
  263541
  135426
  546132
  654213
  ")
