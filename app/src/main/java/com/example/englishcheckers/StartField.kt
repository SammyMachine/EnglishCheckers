package com.example.englishcheckers

import Cell
import Checker
import Colors
import Field
import android.content.Intent
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
    private lateinit var views: MutableList<View?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_start_field)
        table = findViewById(R.id.table)
        startField.field()
        startField.actionListener(this)
        startField.fillFieldByCheckers()
        moveFlag = true

    }

    fun moveToFinishBlack() {
        val startFieldIntent = Intent(this, FinishedGame::class.java)
        startActivity(startFieldIntent)
    }

    fun moveToFinishWhite() {
        val startFieldIntent = Intent(this, FinishedGameWhite::class.java)
        startActivity(startFieldIntent)
    }

    fun moveToDraw() {
        val startFieldIntent = Intent(this, Draw::class.java)
        startActivity(startFieldIntent)
    }

    private fun findTag(zxc: ViewGroup, tag: Any): View? {
        for (i in 0 until zxc.childCount)
            if (zxc.getChildAt(i).getTag().equals(tag)) return zxc.getChildAt(i)
        return null
    }

    override fun getCheckerLayout(cell: Cell): LinearLayout? {
        val column = findTag(table, String.valueOf(cell.getY()))
        return Objects.requireNonNull(column)?.findViewWithTag(String.valueOf(cell.getX()))
    }

    override fun setColorOfPotentialCell(x: Int, y: Int, color: Colors) {
        val column1 = findTag(table, String.valueOf(y))
        if (color == Colors.POSSIBLE_STEP_COLOR) Objects.requireNonNull(column1)?.findViewWithTag<LinearLayout>(String.valueOf(x))?.setBackgroundColor(
            ContextCompat.getColor(this, R.color.possibleStepColor))

    }

    override fun setColorOfChosenCell(x: Int, y: Int, color: Colors): Pair<Int, Int> {
        val column1 = findTag(table, String.valueOf(y))
        if (color == Colors.POSSIBLE_STEP_COLOR) Objects.requireNonNull(column1)?.findViewWithTag<LinearLayout>(String.valueOf(x))?.setBackgroundColor(
            ContextCompat.getColor(this, R.color.possibleStepColor))
        return Pair(x, y)
    }

    override fun convertCoordinatesToViews(viewNow: MutableList<Pair<Int, Int>>): MutableList<View?> {
        for (i in viewNow.indices) {
            val column1 = findTag(table, String.valueOf(viewNow[i].second))
            views.add(Objects.requireNonNull(column1)?.findViewWithTag<LinearLayout>(String.valueOf(viewNow[i].first))!!)
        }
        return views
    }

    override fun addingToView(view: View) {
        views.add(view)
    }

    override fun checkerAdded(position: Cell) {
        val column = findTag(table, String.valueOf(position.getY()))
        val pictureOfChecker = ImageView(this)
        if (position.getChecker()?.getColorOfChecker() == Colors.WHITE)
            pictureOfChecker.setImageResource(R.drawable.biege_checker)
        else
            pictureOfChecker.setImageResource(R.drawable.red_checker)
        val layout = Objects.requireNonNull(column)
            ?.findViewWithTag<LinearLayout>(String.valueOf(position.getX()))
        if (layout != null) {
            layout.addView(pictureOfChecker)
        }
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

    override fun boardClear(potentialSteps: Map<Cell?, List<Cell?>>, views: List<View?>) {
        for ((_, value) in potentialSteps) {
            for (i in value.indices) {
                val column = findTag(table, String.valueOf(value[i]?.getY()))
                Objects.requireNonNull(column)
                    ?.findViewWithTag<View>(String.valueOf(value[i]?.getX()))
                    ?.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
            }
        }
        for (i in views.indices) {
            views[i]?.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
        }
    }

    override fun viewClearing(viewNow: MutableList<View?>) {
        viewNow.clear()
    }

    override fun becomeQueen(cell: Cell) {
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
        if (startField.startGame(posX, posY) == "winBlack") {
            moveToFinishBlack()
        }
        if (startField.startGame(posX, posY) == "winWhite") {
            moveToFinishWhite()
        }
        if (startField.startGame(posX, posY) == "draw") {
            moveToDraw()
        }

    }
}