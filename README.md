# puzzles

[![Build Status](https://travis-ci.org/raxod502/puzzles.svg?branch=master)](https://travis-ci.org/raxod502/puzzles)

## Summary

This library contains parsers and solvers for KenKen (`puzzles.kenken`) and Sudoku (`puzzles.sudoku`) puzzles.

## Example usage

```
puzzles.core> (println sample-kenken/sample-4x4)

  +-------+---+---+
  | 2   - | 2 | 12|
  +-------+---+   |
  | 24  * |     * |
  +---+   +-------+
  | 2 |   | 3   - |
  |   +---+---+---+
  | / | 7   + | 2 |
  +---+-------+---+

nil
puzzles.core> (println (kenken/solve sample-kenken/sample-4x4))
3124
4213
2341
1432
nil
puzzles.core> (println sample-sudoku/sample-1)

  2.. .9. 43.
  4.3 ..7 ...
  ..7 .4. .1.

  ... .63 .8.
  1.. ... ..4
  .7. 85. ...

  .8. .3. 9..
  ... 7.. 1.6
  .64 .2. ..3

nil
puzzles.core> (println (sudoku/solve sample-sudoku/sample-1))
216 598 437
493 617 258
857 342 619

945 163 782
138 279 564
672 854 391

781 436 925
329 785 146
564 921 873
nil
```

## Development

### Emacs integration

First, you will need to make sure CIDER is all set up in your Emacs, at least for Clojure development. Then press `M-x customize-group` and enter `cider`. Navigate to `Cider Cljs Lein Repl` and set the value to `Figwheel-sidecar`. You should now be able to `C-c M-J` or `M-x cider-jack-in-clojurescript` from a Clojure or ClojureScript file in your project to launch Clojure and ClojureScript REPLs and start up Figwheel's hot-code reloading. Commands such as `C-c C-k` and `C-c M-n` should work intelligently with the appropriate REPL, depending on whether the current file is Clojure or ClojureScript.
