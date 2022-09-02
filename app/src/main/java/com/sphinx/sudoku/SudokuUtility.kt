package com.sphinx.sudoku

class SudokuUtility {
    companion object{
        fun getSquare(row:Int,column:Int):Int{
            return (row / 3 * 3) +  (column / 3) ;
        }
    }
}