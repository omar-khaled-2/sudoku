package com.sphinx.sudoku

import android.util.Log


class Sudoku(visible:Int):Observable() {



    val board:MutableList<MutableList<Pair<Int?,Boolean>>> = MutableList(9){
        MutableList(9){ Pair(null,true) }
    }



    init {
        val fullBoard = BoardGenerator.create();

        val positions = (0..80).shuffled();

        for(i in 0..visible){
            val position = positions[i]
            board[position / 9][position % 9] = Pair(fullBoard[position],false);
        }
    }


    fun change(position:Pair<Int,Int>,value:Int){
        board[position.first][position.second] = board[position.first][position.second].copy(first = value);
        notifyObservers();
    }



    private fun isBoardFull():Boolean{
        for(row in 0..8){
            for(column in 0..8){
                if(board[row][column].first == null) return false
            }
        }

        return true
    }


    fun isWin():Boolean{
        if(!isBoardFull()) return false;

        var x:Int;



        for(row in 0..8){
            x = 1 xor 2 xor 3 xor 4 xor 5 xor 6 xor 7 xor 8 xor 9;
            for(column in 0..8) x = x xor board[row][column].first!!;
            Log.i("xxx","row ${row} ${x}")

            if(x != 0) return false
        }


        for(column in 0..8){
            x = 1 xor 2 xor 3 xor 4 xor 5 xor 6 xor 7 xor 8 xor 9;

            for(row in 0..8) x = x xor board[row][column].first!!;
            Log.i("xxx","column ${column} ${x}")


            if(x != 0) return false
        }



        for(square in 0..8){
            x = 1 xor 2 xor 3 xor 4 xor 5 xor 6 xor 7 xor 8 xor 9;

            for(i in 0..8) x = x xor board[square / 3 * 3+ i / 3][square % 3 * 3 + i % 3].first!!;
            Log.i("xxx","square ${square} ${x}")

            if(x != 0) return false
        }



        return true
    }
}