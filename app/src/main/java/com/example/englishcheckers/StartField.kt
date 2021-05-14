package com.example.englishcheckers

import Cell
import Checker
import Colors
import Field
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.lang.Math.abs
import java.lang.String
import java.util.*
import kotlin.math.abs
import kotlin.properties.Delegates


class StartField : AppCompatActivity(), Field.ActionListener {

    private lateinit var table: LinearLayout
    private var startField = Field(this)
    private var moveFlag by Delegates.notNull<Boolean>()
    private lateinit var selectedCell: Cell
    private var clickCounter = 0
    private lateinit var viewNow: MutableList<View?>
    private var potentialSteps = mutableMapOf<Cell?, MutableList<Cell?>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_start_field)
        table = findViewById(R.id.table)
        startField.field()
        startField.actionListener(this)
        startField.fillFieldByCheckers()
        moveFlag = true
        viewNow = mutableListOf()

    }

    private fun findTag(zxc: ViewGroup, tag: Any): View? {
        for (i in 0 until zxc.childCount)
            if (zxc.getChildAt(i).getTag().equals(tag)) return zxc.getChildAt(i)
        return null
    }

    private fun flagToPickAnotherColor(cell: Cell, moveFlag: Boolean): Boolean {
        return if (moveFlag) cell.getChecker()?.getColorOfChecker() == Colors.BLACK
        else cell.getChecker()?.getColorOfChecker() == Colors.WHITE
    }

    private fun flagToPickNonEater(selectedCell: Cell, requiredSteps: Map<Cell?, List<Cell?>>): Boolean {
        var flag = false
        for (i in requiredSteps.keys) {
            if (selectedCell == i) flag = true
        }
        return flag
    }

    private fun flagToMove(selectedCell: Cell, cell: Cell, possibleSteps: Map<Cell?, List<Cell?>>): Boolean = possibleSteps[selectedCell]?.contains(cell)!!

    override fun checkerAdded(position: Cell) {
        val column = findTag(table, String.valueOf(position.getY()))
        val pictureOfChecker = ImageView(this)
        if (position.getChecker()?.getColorOfChecker() == Colors.WHITE)
            pictureOfChecker.setImageResource(R.drawable.biege_checker)
        else
            pictureOfChecker.setImageResource(R.drawable.red_checker)
        val layout = Objects.requireNonNull(column)?.findViewWithTag<LinearLayout>(String.valueOf(position.getX()))
        if (layout != null) {
            layout.addView(pictureOfChecker)
        }
    }

    private fun getCheckerLayout(cell: Cell): LinearLayout? {
        val column = findTag(table, String.valueOf(cell.getY()))
        return Objects.requireNonNull(column)?.findViewWithTag(String.valueOf(cell.getX()))
    }

    override fun checkerMoved(start: Cell, end: Cell) {
        val pictureOfChecker = getCheckerLayout(start)?.getChildAt(0)
        getCheckerLayout(start)?.removeView(pictureOfChecker)
        getCheckerLayout(end)?.addView(pictureOfChecker)
    }

    override fun checkerWasEaten(eatenCell: Cell) {
        val pictureOfChecker = getCheckerLayout(eatenCell)?.getChildAt(0)
        getCheckerLayout(eatenCell)?.removeView(pictureOfChecker)
    }

    fun boardClear(potentialSteps: Map<Cell?, List<Cell?>>, views: List<View?>) {
        for ((_, value) in potentialSteps) {
            for (i in value.indices) {
                val column = findTag(table, String.valueOf(value[i]?.getY()))
                Objects.requireNonNull(column)?.findViewWithTag<View>(String.valueOf(value[i]?.getX()))?.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
            }
        }
        for (i in views.indices) {
            views[i]?.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
        }
    }

    fun multiEatSituation(cell: Cell?, requiredSteps: Map<Cell?, List<Cell?>>): Boolean {
        var flag = false
        for ((key, value) in requiredSteps) {
            if (selectedCell == key && cell in value) {
                flag = true
                break
            }
        }
        return flag
    }

    fun becomeQueen(cell: Cell) {
        var pictureOfChecker = getCheckerLayout(cell)?.getChildAt(0)
        getCheckerLayout(cell)?.removeView(pictureOfChecker)
        pictureOfChecker = ImageView(this)
        if (cell.getChecker()?.getColorOfChecker() == Colors.WHITE)
            pictureOfChecker.setImageResource(R.drawable.biege_queen_checker)
        else
            pictureOfChecker.setImageResource(R.drawable.red_queen_checker)
        getCheckerLayout(cell)?.addView(pictureOfChecker)
    }

    fun onClick(view: View) {
        val posX = view.tag.toString().toInt()
        val posY = (view.parent as ViewGroup).tag.toString().toInt()
        if (clickCounter == 1) {
            val cell1 = startField.getCellFromField(posX, posY)
            if (cell1!!.getChecker() != null) {
                if (cell1.getChecker()?.getColorOfChecker() == selectedCell.getChecker()?.getColorOfChecker())
                    clickCounter = 0
            } else {
                if (flagToMove(selectedCell, cell1, potentialSteps)) {
                    if (selectedCell in potentialSteps.keys)
                        if (((selectedCell.getX() - (cell1.getX())) == 2 || (selectedCell.getX() - (cell1.getX())) == -2)
                                && ((selectedCell.getY() - (cell1.getY())) == 2 || (selectedCell.getY() - (cell1.getY())) == -2)) {
                            val entry = mutableMapOf<Cell?, MutableList<Cell?>>()
                            entry[selectedCell] = potentialSteps[selectedCell]!!
                            if (multiEatSituation(cell1, potentialSteps)) {
                                startField.getCellFromField(posX, posY)?.setChecker(selectedCell.getChecker())
                                startField.getCellFromField(selectedCell.getX(), selectedCell.getY())?.setChecker(null)
                                checkerMoved(selectedCell, cell1)
                                checkerWasEaten(startField.getCellFromField((cell1.getX() + selectedCell.getX()) / 2, (cell1.getY() + selectedCell.getY()) / 2)!!)
                                startField.getCellFromField((cell1.getX() + selectedCell.getX()) / 2, (cell1.getY() + selectedCell.getY()) / 2)!!.setChecker(null)
                                if (startField.checkForEatMore(cell1, entry).isNotEmpty()) {
                                    clickCounter = 0
                                } else {
                                    if (cell1.getChecker()?.getColorOfChecker() == Colors.BLACK) {
                                        if (cell1.getY() == 0) {
                                            becomeQueen(cell1)
                                            cell1.setChecker(Checker(cell1.getChecker()!!.getColorOfChecker(), true))
                                        }
                                    }
                                    else {
                                        if (cell1.getY() == 7) {
                                            becomeQueen(cell1)
                                            cell1.setChecker(Checker(cell1.getChecker()!!.getColorOfChecker(), true))
                                        }
                                    }
                                    clickCounter = 0
                                    moveFlag = !moveFlag
                                }
                            }
                            else {
                                if (startField.checkForEatMore(cell1, entry).isNotEmpty()) {
                                    clickCounter = 0
                                } else {
                                    clickCounter = 0
                                    moveFlag = !moveFlag
                                }
                            }
                        } else {
                            startField.getCellFromField(posX, posY)?.setChecker(selectedCell.getChecker())
                            startField.getCellFromField(selectedCell.getX(), selectedCell.getY())?.setChecker(null)
                            checkerMoved(selectedCell, cell1)
                            if (cell1.getChecker()?.getColorOfChecker() == Colors.BLACK) {
                                if (cell1.getY() == 0) {
                                    becomeQueen(cell1)
                                    cell1.setChecker(Checker(cell1.getChecker()!!.getColorOfChecker(), true))
                                }
                            }
                            else {
                                if (cell1.getY() == 7) {
                                    becomeQueen(cell1)
                                    cell1.setChecker(Checker(cell1.getChecker()!!.getColorOfChecker(), true))
                                }
                            }
                            moveFlag = !moveFlag
                            clickCounter = 0
                        }
                }
            }
            boardClear(potentialSteps, viewNow)
            viewNow.clear()
        }
        if (clickCounter == 0) {
            val cell = startField.getCellFromField(posX, posY)
            if (cell?.getChecker() != null) {
                if (flagToPickAnotherColor(cell, moveFlag)) {
                    val requiredSteps = startField.requiredSteps(moveFlag)
                    val possibleSteps = startField.possibleSteps(cell, moveFlag)
                    if (requiredSteps.isNotEmpty()) {
                        potentialSteps = requiredSteps
                        for ((key, list) in potentialSteps) {
                            val column1 = findTag(table, String.valueOf(key?.getY()))
                            Objects.requireNonNull(column1)?.findViewWithTag<LinearLayout>(String.valueOf(column1?.getX()))?.setBackgroundColor(ContextCompat.getColor(this, R.color.positionAtTheMomentColor))
                            for (i in list.indices) {
                                val column = findTag(table, String.valueOf(list[i]?.getY()))
                                Objects.requireNonNull(column)?.findViewWithTag<LinearLayout>(String.valueOf(list[i]?.getX()))?.setBackgroundColor(ContextCompat.getColor(this, R.color.possibleStepColor))
                            }
                        }
                        selectedCell = cell
                        if (flagToPickNonEater(selectedCell, requiredSteps)) clickCounter++
                    } else {
                        potentialSteps[cell] = possibleSteps
                        if (cell.getChecker() != null) {
                            view.setBackgroundColor(ContextCompat.getColor(this, R.color.positionAtTheMomentColor));
                            viewNow.add(view)
                        }
                        for (i in potentialSteps[cell]!!.indices) {
                            val column = findTag(table, String.valueOf(potentialSteps[cell]?.get(i)?.getY()))
                            Objects.requireNonNull(column)?.findViewWithTag<LinearLayout>(String.valueOf(potentialSteps[cell]?.get(i)?.getX()))?.setBackgroundColor(ContextCompat.getColor(this, R.color.possibleStepColor))
                        }
                        selectedCell = cell
                        clickCounter++
                    }
                }
            }
        }
    }
}