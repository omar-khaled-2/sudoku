package com.sphinx.sudoku

class SudokuFactory {
    fun create(difficulty:String):Sudoku{
        return when(difficulty){
            "easy" -> Sudoku(70)
            "medium" -> Sudoku(50)
            "hard" -> Sudoku(30)
            else -> throw UnsupportedClassVersionError("difficulty isn't available")
        }
    }
}