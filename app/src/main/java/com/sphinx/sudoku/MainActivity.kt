package com.sphinx.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sphinx.sudoku.ui.theme.SudokuTheme


fun Int.toSecondsAndMinutes():String{
    val minutes = this / 60;
    val seconds = this % 60
    return "${if(minutes > 9) minutes else "0$minutes"} : ${if(seconds > 9) seconds else "0$seconds"}"
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {

            SudokuTheme(viewModel.isDarkMode){
                Surface(color = MaterialTheme.colors.background) {
                    App(
                        board = viewModel.board,
                        onPressNumber = {
                                        viewModel.pressNumber(it)
                        },
                        onFocus = {
                            viewModel.focus(it)
                        },
                        focusPosition = viewModel.focusPosition,
                        isDarkMode = viewModel.isDarkMode,
                        onChangeDarkMode = {viewModel.changeDarkMode(it)},
                        onPlayAgain = {
                            viewModel.playAgain()
                        },
                        difficulty = viewModel.difficulty,
                        difficulties = viewModel.difficulties,
                        onDifficultyChange = {
                            viewModel.changeDifficulty(it)
                        },
                        time = viewModel.time,
                        alert = viewModel.alert
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseTimer()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumeTimer()

    }
}


@Composable
fun App(
    board:List<List<Pair<Int?,Boolean>>>,
    onPressNumber: (num: Int) -> Unit,
    onFocus: (position: Pair<Int, Int>) -> Unit,
    focusPosition:Pair<Int,Int>?,
    isDarkMode:Boolean,
    onChangeDarkMode:(Boolean) -> Unit,
    onPlayAgain:() -> Unit,
    difficulty:String,
    difficulties: Array<String>,
    onDifficultyChange:(String) -> Unit,
    time:Int,
    alert: Alert?
    ){

    var difficultiesMenuVisability by remember {
        mutableStateOf(false)
    }

    if(alert != null) AlertDialog(
        onDismissRequest = { alert.onDismiss() },
        title = {
                Text(text = alert.title)
        },
        text = {
            Text(text = alert.text)
        },
        dismissButton = {
            Button(onClick = {alert.onDismiss()}) {
                Text(text = alert.dismissText)
            }
        },
        confirmButton = {
            Button(onClick = {alert.onConfirm()}) {
                Text(text = alert.confirmText)
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(value = difficulty.uppercase(), onValueChange = {},
                        trailingIcon = {
                            IconButton(onClick = {
                                difficultiesMenuVisability = true
                            }) {
                                Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                            }
                        },
                        readOnly = true,


                    )

                    DropdownMenu(expanded = difficultiesMenuVisability, onDismissRequest = { difficultiesMenuVisability = false }) {
                        for(difficulty in difficulties){
                            DropdownMenuItem(onClick = {
                                onDifficultyChange(difficulty)
                                difficultiesMenuVisability = false
                            }) {
                                Text(text = difficulty)
                            }
                        }

                    }
                },
                actions = {
                    IconButton(onClick = onPlayAgain) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onChangeDarkMode(!isDarkMode) }) {
                        Icon(imageVector = if(isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode, contentDescription = null)
                    }
                }
            )
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),


        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp,alignment = Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp, 0.dp)
            ) {
                Text(text = time.toSecondsAndMinutes(),fontWeight = FontWeight.Bold,fontSize = 20.sp)
                Board(board = board,onFocus = onFocus,focusPosition = focusPosition)

            }
            Keyboard(onPress = onPressNumber)
        }
    }

}


@Composable
fun Board(
    board:List<List<Pair<Int?,Boolean>>>,
    modifier: Modifier = Modifier,
    onFocus:(position:Pair<Int,Int>) -> Unit,
    focusPosition:Pair<Int,Int>?,
    elevation:Dp = 10.dp,
    shape: Shape = MaterialTheme.shapes.large
    ){

    Surface(
        border = BorderStroke(1.dp,MaterialTheme.colors.onSurface),
        shape = shape,
        elevation = elevation,


    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f)

        ) {
            for(row in 0..8){

                if(row > 0) Divider(
                    color = MaterialTheme.colors.onSurface.copy(alpha = if(row % 3 == 0) 1f else .2f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {

                    for(column in 0..8){
                        if(column > 0) Divider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp),
                            color = MaterialTheme.colors.onSurface.copy(alpha = if(column % 3 == 0) 1f else .2f)

                        )
                        Surface(
                            color = when{
                                focusPosition?.first == row && focusPosition.second == column -> MaterialTheme.colors.primary
                                focusPosition?.first == row || focusPosition?.second == column -> MaterialTheme.colors.primary.copy(alpha = .5f)
                                else -> MaterialTheme.colors.surface
                            },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clickable { onFocus(Pair(row, column)) }

                        ) {
                            Box(

                                contentAlignment = Alignment.Center,

                                ) {
                                if(board[row][column].first != null) Text(text = board[row][column].first.toString(),fontWeight = FontWeight.Bold,color = LocalContentColor.current.copy(alpha = if(board[row][column].second) 1f else .6f))
                            }
                        }



                    }

                }
            }

        }
    }

}

@Composable
fun Keyboard(
    onPress:(num:Int) -> Unit,
    modifier:Modifier = Modifier
){
   Column(
       modifier = modifier.fillMaxWidth(),

   ) {
       for(i in 0..2){
           Row(

               modifier = Modifier.fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween,
           ) {
               for(j in 0..2){
                   val num = i * 3 + j + 1;
                   TextButton(onClick = { onPress(num) },modifier = Modifier
                       .weight(1f)
                       .aspectRatio(2f)) {
                       Text(text = num.toString())
                   }
               }
           }
       }
   }




}