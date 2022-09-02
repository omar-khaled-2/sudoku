package com.sphinx.sudoku

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.util.*
import kotlin.concurrent.timerTask


interface Alert{
    val title:String;
    val text:String;
    val confirmText:String;
    val dismissText:String;
    fun onConfirm();
    fun onDismiss();
}





class MainActivityViewModel(application: Application):AndroidViewModel(application),Observer {




    var isDarkMode by mutableStateOf(false)
        private set

    var focusPosition by mutableStateOf<Pair<Int,Int>?>(null)
        private set

    val difficulties = arrayOf("easy","medium","hard")


    private val sudokuFactory = SudokuFactory()



    private val sharedPreferences: SharedPreferences =
        getApplication<Application>().getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val editor by lazy {
        sharedPreferences.edit()
    }

    var difficulty by mutableStateOf(sharedPreferences.getString("default_difficulty",difficulties[0])!!)
        private set



    private var sudoku = sudokuFactory.create(difficulty);


    var board by mutableStateOf(sudoku.board, neverEqualPolicy())
        private set



    init {
        sudoku.addObserver(this)
    }




    var time by mutableStateOf(0)
        private set




    var alert by mutableStateOf<Alert?>(null)

    private val winningAlert by lazy {
        object :Alert{

            override val title: String = "Winner"
            override val text: String = "Wanna play again";
            override val confirmText: String = "yes";
            override val dismissText: String = "no";
            override fun onConfirm(){

                playAgain()
                alert = null
            }
            override fun onDismiss(){
                alert = null
            }
        }
    }

    private var timer:Timer? = null;



    fun resumeTimer(){
        timer = Timer();
        timer!!.schedule(timerTask { time++ },1000,1000)
    }

    fun pauseTimer(){
        timer?.cancel();

    }

    fun resetTimer(){
        time = 0;
    }




    fun focus(position:Pair<Int,Int>){
        focusPosition = position
    }

    fun pressNumber(num:Int){
        if(focusPosition != null && sudoku.board[focusPosition!!.first][focusPosition!!.second].second){
            sudoku.change(focusPosition!!,num)
        }

    }

    fun changeDarkMode(isDarkMode:Boolean){
        this.isDarkMode = isDarkMode
    }

    fun onGameChange(){
        sudoku.addObserver(this);
        board = sudoku.board;
        pauseTimer()
        resetTimer();
        resumeTimer();
    }

    fun playAgain(){
        sudoku = sudokuFactory.create(difficulty);
        onGameChange()
    }



    fun changeDifficulty(difficulty:String){
        this.difficulty = difficulty
        editor.putString("default_difficulty",difficulty).apply()
        sudoku = sudokuFactory.create(difficulty);
        onGameChange()

    }
    override fun update() {
        board = sudoku.board;
        if(sudoku.isWin()){
            alert = winningAlert;
            pauseTimer();
        };
    }


}