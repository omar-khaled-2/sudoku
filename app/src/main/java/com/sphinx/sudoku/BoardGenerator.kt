package com.sphinx.sudoku

import java.util.*
import kotlin.collections.ArrayList


class BoardGenerator {
    companion object{
    fun create(): IntArray {
        val arr = ArrayList<Int>(9)
        val grid = IntArray(81)
        for (i in 1..9) arr.add(i)

        //loads all boxes with numbers 1 through 9
        for (i in 0..80) {
            if (i % 9 == 0) Collections.shuffle(arr)
            val perBox = i / 3 % 3 * 9 + i % 27 / 9 * 3 + i / 27 * 27 + i % 3
            grid[perBox] = arr[i % 9]
        }

        //tracks rows and columns that have been sorted
        val sorted = BooleanArray(81)
        var i = 0
        while (i < 9) {
            var backtrack = false
            //0 is row, 1 is column
            for (a in 0..1) {
                //every number 1-9 that is encountered is registered
                val registered =
                    BooleanArray(10) //index 0 will intentionally be left empty since there are only number 1-9.
                val rowOrigin = i * 9
                val colOrigin = i
                ROW_COL@ for (j in 0..8) {
                    //row/column stepping - making sure numbers are only registered once and marking which cells have been sorted
                    val step = if (a % 2 == 0) rowOrigin + j else colOrigin + j * 9
                    val num: Int = grid.get(step)
                    if (!registered[num]) registered[num] = true else  //if duplicate in row/column
                    {
                        //box and adjacent-cell swap (BAS method)
                        //checks for either unregistered and unsorted candidates in same box,
                        //or unregistered and sorted candidates in the adjacent cells
                        for (y in j downTo 0) {
                            val scan = if (a % 2 == 0) i * 9 + y else i + 9 * y
                            if (grid.get(scan) === num) {
                                //box stepping
                                for (z in (if (a % 2 == 0) (i % 3 + 1) * 3 else 0)..8) {
                                    if (a % 2 == 1 && z % 3 <= i % 3) continue
                                    val boxOrigin = scan % 9 / 3 * 3 + scan / 27 * 27
                                    val boxStep = boxOrigin + z / 3 * 9 + z % 3
                                    val boxNum: Int = grid.get(boxStep)
                                    if (!sorted[scan] && !sorted[boxStep] && !registered[boxNum]
                                        || sorted[scan] && !registered[boxNum] && if (a % 2 == 0) boxStep % 9 == scan % 9 else boxStep / 9 == scan / 9
                                    ) {
                                        grid[scan] = boxNum
                                        grid[boxStep] = num
                                        registered[boxNum] = true
                                        continue@ROW_COL
                                    } else if (z == 8) //if z == 8, then break statement not reached: no candidates available
                                    {
                                        //Preferred adjacent swap (PAS)
                                        //Swaps x for y (preference on unregistered numbers), finds occurence of y
                                        //and swaps with z, etc. until an unregistered number has been found
                                        var searchingNo = num

                                        //noting the location for the blindSwaps to prevent infinite loops.
                                        val blindSwapIndex = BooleanArray(81)

                                        //loop of size 18 to prevent infinite loops as well. Max of 18 swaps are possible.
                                        //at the end of this loop, if continue or break statements are not reached, then
                                        //fail-safe is executed called Advance and Backtrack Sort (ABS) which allows the
                                        //algorithm to continue sorting the next row and column before coming back.
                                        //Somehow, this fail-safe ensures success.
                                        for (q in 0..17) {
                                            SWAP@ for (b in 0..j) {
                                                val pacing =
                                                    if (a % 2 == 0) rowOrigin + b else colOrigin + b * 9
                                                if (grid.get(pacing) === searchingNo) {
                                                    var adjacentCell = -1
                                                    var adjacentNo = -1
                                                    val decrement = if (a % 2 == 0) 9 else 1
                                                    for (c in 1 until 3 - i % 3) {
                                                        adjacentCell =
                                                            pacing + if (a % 2 == 0) (c + 1) * 9 else c + 1

                                                        //this creates the preference for swapping with unregistered numbers
                                                        if (a % 2 == 0 && adjacentCell >= 81
                                                            || a % 2 == 1 && adjacentCell % 9 == 0
                                                        ) adjacentCell -= decrement else {
                                                            adjacentNo = grid.get(adjacentCell)
                                                            if (i % 3 != 0 || c != 1 || blindSwapIndex[adjacentCell]
                                                                || registered[adjacentNo]
                                                            ) adjacentCell -= decrement
                                                        }
                                                        adjacentNo = grid.get(adjacentCell)

                                                        //as long as it hasn't been swapped before, swap it
                                                        if (!blindSwapIndex[adjacentCell]) {
                                                            blindSwapIndex[adjacentCell] = true
                                                            grid[pacing] = adjacentNo
                                                            grid[adjacentCell] = searchingNo
                                                            searchingNo = adjacentNo
                                                            if (!registered[adjacentNo]) {
                                                                registered[adjacentNo] = true
                                                                continue@ROW_COL
                                                            }
                                                            break@SWAP
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        //begin Advance and Backtrack Sort (ABS)
                                        backtrack = true
                                        break@ROW_COL
                                    }
                                }
                            }
                        }
                    }
                }
                if (a % 2 == 0) for (j in 0..8) sorted[i * 9 + j] = true //setting row as sorted
                else if (!backtrack) for (j in 0..8) sorted[i + j * 9] =
                    true //setting column as sorted
                else  //reseting sorted cells through to the last iteration
                {
                    backtrack = false
                    for (j in 0..8) sorted[i * 9 + j] = false
                    for (j in 0..8) sorted[(i - 1) * 9 + j] = false
                    for (j in 0..8) sorted[i - 1 + j * 9] = false
                    i -= 2
                }
            }
            i++
        }

        return grid;
    }
    }

}