import android.view.View
import android.widget.LinearLayout
import com.example.englishcheckers.R
import kotlin.properties.Delegates

class Field(listener: ActionListener?) {

    lateinit var checkersField: Array<Array<Cell?>?>
    private val columns = 8
    private val rows = 8
    private var actionListener: ActionListener? = listener
    private lateinit var selectedCell: Cell
    private var cellCheckedForEat: Cell = Cell(-1, -1, null)
    private var chosenCell: Cell? = null
    private var clickCounter = 0
    private var potentialSteps = mutableMapOf<Cell?, MutableList<Cell?>>()
    private var moveFlag = true
    private lateinit var viewNow: MutableList<Pair<Int, Int>>

    fun actionListener(actionListener: ActionListener?) {
        this.actionListener = actionListener
    }

    interface ActionListener {
        fun checkerAdded(position: Cell)
        fun checkerMoved(start: Cell, end: Cell)
        fun checkerWasEaten(eatenCell: Cell)
        fun boardClear(potentialSteps: Map<Cell?, List<Cell?>>, views: List<View?>)
        fun becomeQueen(cell: Cell)
        fun setColorOfPotentialCell(x: Int, y: Int, color: Colors)
        fun setColorOfChosenCell(x: Int, y: Int, color: Colors): Pair<Int, Int>
        fun viewClearing(viewNow: MutableList<View?>)
        fun addingToView(view: View)
        fun convertCoordinatesToViews(viewNow: MutableList<Pair<Int, Int>>): MutableList<View?>
        fun getCellLayout(cell: Cell): LinearLayout?
    }


    fun field() {
        viewNow = mutableListOf()
        val check1 = arrayOfNulls<Array<Cell?>>(columns)
        checkersField = check1
        for (x in 0..7) {
            checkersField.set(x, arrayOfNulls(rows))
            for (y in 0..7) {
                checkersField.get(x)?.set(y, Cell(x, y, null))
            }
        }
        this.actionListener
    }

    fun fillFieldByCheckers() {
        for (x in 0..7) {
            for (y in 0..2) {
                if ((x + y) % 2 != 0) {
                    val checker = Checker(Colors.WHITE, false)
                    checkersField.get(x)?.get(y)?.setChecker(checker)
                    actionListener?.checkerAdded(Cell(x, y, checker))

                }
            }
            for (y in 5..7)
                if ((y + x) % 2 != 0) {
                    val checker = Checker(Colors.BLACK, false)
                    checkersField.get(x)?.get(y)?.setChecker(checker)
                    actionListener?.checkerAdded(Cell(x, y, checker))
                }
        }
    }

    fun cellInStartField(posX: Int, posY: Int): Boolean = (posX in 0..7 && posY in 0..7)

    fun getCellFromField(posX: Int, posY: Int): Cell? {
        return if (cellInStartField(posX, posY)) {
            checkersField.get(posX)?.get(posY)
        } else null
    }

    fun moveChecker(start: Cell, end: Cell) {
        end.setChecker(start.getChecker())
        start.setChecker(null)
    }

    fun eatChecker(eatenCell: Cell) {
        eatenCell.setChecker(null)
    }

    fun changeToQueen(cell: Cell?, color: Colors?) {
        cell?.setChecker(Checker(color, true))
    }

    //moveFlag: true when BlackPlayer move; false when WhitePlayer move

    fun startGame(x: Int, y: Int): String {
        var outputToOnClick = "nothing"
        if (clickCounter == 1) {
            val cell1 = this.getCellFromField(x, y)
            if (cell1!!.getChecker() != null) {
                if (cell1.getChecker()?.getColorOfChecker() == selectedCell.getChecker()
                        ?.getColorOfChecker()
                )
                    clickCounter = 0
            } else {
                if (flagToMove(selectedCell, cell1, potentialSteps)) {
                    if (selectedCell in potentialSteps.keys)
                        if (((selectedCell.getX() - (cell1.getX())) == 2 || (selectedCell.getX() - (cell1.getX())) == -2)
                            && ((selectedCell.getY() - (cell1.getY())) == 2 || (selectedCell.getY() - (cell1.getY())) == -2)
                        ) {
                            val entry = mutableMapOf<Cell?, MutableList<Cell?>>()
                            entry[selectedCell] = potentialSteps[selectedCell]!!
                            if (multiEatSituation(cell1, potentialSteps)) {
                                moveChecker(
                                    this.getCellFromField(
                                        selectedCell.getX(),
                                        selectedCell.getY()
                                    )!!, cell1
                                )
                                actionListener?.checkerMoved(selectedCell, cell1)
                                actionListener?.checkerWasEaten(
                                    this.getCellFromField(
                                        (cell1.getX() + selectedCell.getX()) / 2,
                                        (cell1.getY() + selectedCell.getY()) / 2
                                    )!!
                                )
                                eatChecker(
                                    this.getCellFromField(
                                        (cell1.getX() + selectedCell.getX()) / 2,
                                        (cell1.getY() + selectedCell.getY()) / 2
                                    )!!
                                )
                                if (this.checkForEatMore(cell1, entry).isNotEmpty()) {
                                    clickCounter = 0
                                    cellCheckedForEat = cell1
                                } else {
                                    if (cell1.getChecker()?.getColorOfChecker() == Colors.BLACK) {
                                        if (cell1.getY() == 0) {
                                            actionListener?.becomeQueen(cell1)
                                            this.changeToQueen(
                                                cell1,
                                                cell1.getChecker()!!.getColorOfChecker()
                                            )
                                        }
                                    } else {
                                        if (cell1.getY() == 7) {
                                            actionListener?.becomeQueen(cell1)
                                            this.changeToQueen(
                                                cell1,
                                                cell1.getChecker()!!.getColorOfChecker()
                                            )
                                        }
                                    }
                                    clickCounter = 0
                                    moveFlag = !moveFlag
                                }
                            } else {
                                clickCounter = 0
                                moveFlag = !moveFlag
                            }
                        } else {
                            moveChecker(
                                this.getCellFromField(
                                    selectedCell.getX(),
                                    selectedCell.getY()
                                )!!, cell1
                            )
                            actionListener?.checkerMoved(selectedCell, cell1)
                            if (cell1.getChecker()?.getColorOfChecker() == Colors.BLACK) {
                                if (cell1.getY() == 0) {
                                    actionListener?.becomeQueen(cell1)
                                    this.changeToQueen(
                                        cell1,
                                        cell1.getChecker()!!.getColorOfChecker()
                                    )
                                }
                            } else {
                                if (cell1.getY() == 7) {
                                    actionListener?.becomeQueen(cell1)
                                    actionListener?.becomeQueen(cell1)
                                    this.changeToQueen(
                                        cell1,
                                        cell1.getChecker()!!.getColorOfChecker()
                                    )
                                }
                            }
                            moveFlag = !moveFlag
                            clickCounter = 0
                        }
                }
            }
            actionListener?.boardClear(
                potentialSteps,
                actionListener?.convertCoordinatesToViews(viewNow)!!
            )
            chosenCell = cell1
            actionListener?.viewClearing(actionListener?.convertCoordinatesToViews(viewNow)!!)
        }
        if (clickCounter == 0) {
            if (!this.checkForGameFinished().first) {
                val cell = this.getCellFromField(x, y)
                if (cell?.getChecker() != null) {
                    if (flagToPickAnotherColor(cell!!, moveFlag)) {
                        val requiredSteps = this.requiredSteps(moveFlag)
                        val possibleSteps = this.possibleSteps(moveFlag)
                        if (cell in requiredSteps.keys || cell in possibleSteps.keys)
                            if (requiredSteps.isNotEmpty()) {
                                if (cell in requiredSteps.keys) {
                                    if (cell!!.getChecker()!!.getColorOfChecker() != chosenCell!!.getChecker()!!.getColorOfChecker() || cellCheckedForEat == cell) {
                                        potentialSteps = requiredSteps
                                        for ((key, list) in potentialSteps) {
                                            if (key == cell || key!!.getChecker()!!.getColorOfChecker() != chosenCell!!.getChecker()!!.getColorOfChecker()) {
                                                actionListener?.setColorOfChosenCell(
                                                    x,
                                                    y,
                                                    Colors.POSITION_AT_THE_MOMENT
                                                )

                                                actionListener?.addingToView(
                                                    actionListener?.getCellLayout(
                                                        key!!
                                                    ) as View
                                                )
                                                for (i in list.indices) {
                                                    actionListener?.setColorOfPotentialCell(
                                                        list[i]!!.getX(),
                                                        list[i]!!.getY(),
                                                        Colors.POSSIBLE_STEP_COLOR
                                                    )
                                                    actionListener?.addingToView(
                                                        actionListener?.getCellLayout(
                                                            list[i]!!
                                                        ) as View
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                if (cellCheckedForEat == Cell(-1, -1, null)) {
                                    cellCheckedForEat = cell
                                    selectedCell = cell
                                }
                                if (cell.getChecker()!!.getColorOfChecker() != chosenCell!!.getChecker()!!.getColorOfChecker() || cellCheckedForEat == chosenCell) {
                                    cellCheckedForEat = cell
                                    selectedCell = cell
                                }

                                if (flagToPickNonEater(selectedCell, requiredSteps)) clickCounter++
                            } else if (cell in possibleSteps.keys && cell !in requiredSteps.keys) {
                                potentialSteps[cell] = possibleSteps[cell]!!
                                if (cell.getChecker() != null) {
                                    actionListener?.setColorOfChosenCell(
                                        cell.getX(),
                                        cell.getY(),
                                        Colors.POSITION_AT_THE_MOMENT
                                    )
                                    actionListener?.addingToView(actionListener?.getCellLayout(cell) as View)
                                }
                                for (i in potentialSteps[cell]!!.indices) {
                                    actionListener?.setColorOfPotentialCell(
                                        potentialSteps[cell]?.get(
                                            i
                                        )!!.getX(),
                                        potentialSteps[cell]?.get(i)!!.getY(),
                                        Colors.POSSIBLE_STEP_COLOR
                                    )
                                    actionListener?.addingToView(
                                        actionListener?.getCellLayout(
                                            potentialSteps[cell]?.get(i)!!
                                        ) as View
                                    )
                                }
                                cellCheckedForEat = Cell(-1, -1, null)
                                selectedCell = cell
                                clickCounter++
                            } else {
                                clickCounter++
                            }
                    }
                }
            } else {
                outputToOnClick = if (this.checkForGameFinished().second) {
                    "winBlack"
                } else {
                    "winWhite"
                }
            }
        }
        return outputToOnClick
    }

    fun requiredSteps(moveFlag: Boolean): MutableMap<Cell?, MutableList<Cell?>> {
        val varyList = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        val requiredSteps = mutableMapOf<Cell?, MutableList<Cell?>>()
        val listOfPotentialCells = mutableListOf<Cell?>()
        for (x in 0 until rows)
            for (y in 0 until columns) {
                val cell = getCellFromField(x, y)
                if (cell!!.getChecker() != null) {
                    if ((cell.getChecker()!!
                            .getColorOfChecker() == Colors.BLACK && moveFlag) || (cell.getChecker()!!
                            .getColorOfChecker() == Colors.WHITE && !moveFlag)
                    ) {
                        if (cell.getChecker()!!.getInfoAboutQueen()) {
                            for (element in varyList) {
                                val potentialCell = getCellFromField(
                                    cell.getX() + 2 * element.first,
                                    cell.getY() + 2 * element.second
                                )
                                if (potentialCell != null)
                                    if (potentialCell.getChecker() == null) {
                                        val cellBetweenOurAndPotential = getCellFromField(
                                            cell.getX() + element.first,
                                            cell.getY() + element.second
                                        )
                                        if (cellBetweenOurAndPotential!!.getChecker() != null &&
                                            cellBetweenOurAndPotential.getChecker()
                                                ?.getColorOfChecker()
                                            != cell.getChecker()?.getColorOfChecker()
                                        ) {
                                            listOfPotentialCells.add(potentialCell)
                                        }
                                    }
                            }
                            if (listOfPotentialCells.size != 0) {
                                requiredSteps[cell] =
                                    mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                                listOfPotentialCells.clear()
                            }
                        } else {
                            if (cell.getChecker()!!.getColorOfChecker() == Colors.BLACK) {
                                for (i in 0 until varyList.size - 1 step 2) {
                                    val potentialCell = getCellFromField(
                                        cell.getX() + 2 * varyList[i].first,
                                        cell.getY() + 2 * varyList[i].second
                                    )
                                    if (potentialCell != null)
                                        if (potentialCell.getChecker() == null) {
                                            val cellBetweenOurAndPotential = getCellFromField(
                                                cell.getX() + varyList[i].first,
                                                cell.getY() + varyList[i].second
                                            )
                                            if (cellBetweenOurAndPotential!!.getChecker() != null &&
                                                cellBetweenOurAndPotential.getChecker()
                                                    ?.getColorOfChecker() == Colors.WHITE
                                            )
                                                listOfPotentialCells.add(potentialCell)
                                        }
                                }
                                if (listOfPotentialCells.size != 0) {
                                    requiredSteps[cell] =
                                        mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                                    listOfPotentialCells.clear()
                                }
                            } else {
                                for (i in 1 until varyList.size step 2) {
                                    val potentialCell = getCellFromField(
                                        cell.getX() + 2 * varyList[i].first,
                                        cell.getY() + 2 * varyList[i].second
                                    )
                                    if (potentialCell != null)
                                        if (potentialCell.getChecker() == null) {
                                            val cellBetweenOurAndPotential = getCellFromField(
                                                cell.getX() + varyList[i].first,
                                                cell.getY() + varyList[i].second
                                            )
                                            if (cellBetweenOurAndPotential!!.getChecker() != null &&
                                                cellBetweenOurAndPotential.getChecker()
                                                    ?.getColorOfChecker() == Colors.BLACK
                                            )
                                                listOfPotentialCells.add(potentialCell)
                                        }
                                }
                                if (listOfPotentialCells.size != 0) {
                                    requiredSteps[cell] =
                                        mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                                    listOfPotentialCells.clear()
                                }
                            }
                        }
                    }
                }
            }
        return requiredSteps
    }


    fun possibleSteps(moveFlag: Boolean): MutableMap<Cell?, MutableList<Cell?>> {
        val varyList = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        val possibleSteps = mutableMapOf<Cell?, MutableList<Cell?>>()
        val listOfPotentialCells = mutableListOf<Cell?>()
        for (x in 0 until rows) {
            for (y in 0 until columns) {
                val cell = getCellFromField(x, y)
                if (cell?.getChecker() != null) {
                    if ((cell.getChecker()!!.getColorOfChecker() == Colors.BLACK && moveFlag) ||
                        (cell.getChecker()!!.getColorOfChecker() == Colors.WHITE && !moveFlag)
                    ) {
                        if (cell.getChecker()!!.getInfoAboutQueen()) {
                            for (element in varyList) {
                                val potentialCell = getCellFromField(
                                    cell.getX() + element.first,
                                    cell.getY() + element.second
                                )
                                if (potentialCell != null)
                                    if (potentialCell.getChecker() == null)
                                        listOfPotentialCells.add(potentialCell)
                            }
                            if (listOfPotentialCells.size != 0) {
                                possibleSteps[cell] =
                                    mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                                listOfPotentialCells.clear()
                            }
                        }
                        if (!cell.getChecker()!!.getInfoAboutQueen()) {
                            if (cell.getChecker()?.getColorOfChecker() == Colors.BLACK) {
                                for (i in 0 until varyList.size - 1 step 2) {
                                    val potentialCell = getCellFromField(
                                        cell.getX() + varyList[i].first,
                                        cell.getY() + varyList[i].second
                                    )
                                    if (potentialCell != null)
                                        if (potentialCell.getChecker() == null)
                                            listOfPotentialCells.add(potentialCell)
                                }
                                if (listOfPotentialCells.size != 0) {
                                    possibleSteps[cell] =
                                        mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                                    listOfPotentialCells.clear()
                                }
                            }
                            if (cell.getChecker()?.getColorOfChecker() == Colors.WHITE) {
                                for (i in 1 until varyList.size step 2) {
                                    val potentialCell = getCellFromField(
                                        cell.getX() + varyList[i].first,
                                        cell.getY() + varyList[i].second
                                    )
                                    if (potentialCell != null)
                                        if (potentialCell.getChecker() == null)
                                            listOfPotentialCells.add(potentialCell)
                                }
                                if (listOfPotentialCells.size != 0) {
                                    possibleSteps[cell] =
                                        mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                                    listOfPotentialCells.clear()
                                }

                            }
                        }
                    }
                }
            }
        }
        return possibleSteps
    }

    fun checkForEatMore(
        selectedCell: Cell?,
        possibleSteps: MutableMap<Cell?, MutableList<Cell?>>
    ): MutableMap<Cell?, MutableList<Cell?>> {
        possibleSteps.clear()
        val listOfPotentialCells = mutableListOf<Cell?>()
        val varyList = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        if (selectedCell?.getChecker()!!.getInfoAboutQueen()) {
            for (i in varyList.indices) {
                val potentialCell = getCellFromField(
                    selectedCell.getX() + 2 * varyList[i].first,
                    selectedCell.getY() + 2 * varyList[i].second
                )
                if (potentialCell != null)
                    if (potentialCell.getChecker() == null) {
                        val cellBetweenOurAndPotential = getCellFromField(
                            selectedCell.getX() + varyList[i].first,
                            selectedCell.getY() + varyList[i].second
                        )
                        if (cellBetweenOurAndPotential!!.getChecker() != null && cellBetweenOurAndPotential.getChecker()
                                ?.getColorOfChecker() != selectedCell.getChecker()
                                ?.getColorOfChecker()
                        )
                            listOfPotentialCells.add(potentialCell)
                    }
                if (listOfPotentialCells.size != 0) {
                    possibleSteps[selectedCell] =
                        mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                    listOfPotentialCells.clear()
                }
            }
        } else {
            if (selectedCell.getChecker()!!.getColorOfChecker() == Colors.BLACK) {
                for (i in 0 until varyList.size - 1 step 2) {
                    val potentialCell = getCellFromField(
                        selectedCell.getX() + 2 * varyList[i].first,
                        selectedCell.getY() + 2 * varyList[i].second
                    )
                    if (potentialCell != null)
                        if (potentialCell.getChecker() == null) {
                            val cellBetweenOurAndPotential = getCellFromField(
                                selectedCell.getX() + varyList[i].first,
                                selectedCell.getY() + varyList[i].second
                            )
                            if (cellBetweenOurAndPotential!!.getChecker() != null && cellBetweenOurAndPotential.getChecker()
                                    ?.getColorOfChecker() != selectedCell.getChecker()
                                    ?.getColorOfChecker()
                            )
                                listOfPotentialCells.add(potentialCell)
                        }
                }
                if (listOfPotentialCells.size != 0) {
                    possibleSteps[selectedCell] =
                        mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                    listOfPotentialCells.clear()
                }
            } else {
                for (i in 1 until varyList.size step 2) {
                    val potentialCell = getCellFromField(
                        selectedCell.getX() + 2 * varyList[i].first,
                        selectedCell.getY() + 2 * varyList[i].second
                    )
                    if (potentialCell != null)
                        if (potentialCell.getChecker() == null) {
                            val cellBetweenOurAndPotential = getCellFromField(
                                selectedCell.getX() + varyList[i].first,
                                selectedCell.getY() + varyList[i].second
                            )
                            if (cellBetweenOurAndPotential!!.getChecker() != null && cellBetweenOurAndPotential.getChecker()
                                    ?.getColorOfChecker() != selectedCell.getChecker()
                                    ?.getColorOfChecker()
                            )
                                listOfPotentialCells.add(potentialCell)
                        }
                }
                if (listOfPotentialCells.size != 0) {
                    possibleSteps[selectedCell] =
                        mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                    listOfPotentialCells.clear()
                }
            }
        }
        return possibleSteps
    }

    fun flagToMove(
        selectedCell: Cell,
        cell: Cell,
        possibleSteps: Map<Cell?, List<Cell?>>
    ): Boolean =
        possibleSteps[selectedCell]?.contains(cell)!!

    fun flagToPickAnotherColor(cell: Cell, moveFlag: Boolean): Boolean {
        return if (moveFlag) cell.getChecker()?.getColorOfChecker() == Colors.BLACK
        else cell.getChecker()?.getColorOfChecker() == Colors.WHITE
    }

    fun flagToPickNonEater(selectedCell: Cell, requiredSteps: Map<Cell?, List<Cell?>>): Boolean {
        var flag = false
        for (i in requiredSteps.keys) {
            if (selectedCell == i) flag = true
        }
        return flag
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

    fun checkForGameFinished(): Pair<Boolean, Boolean> {
        var flag = false
        var flagForColorForWin = false // true = winBlack, false = winWhite
        var counterForBlack = 0
        var counterForWhite = 0
        for (x in 0 until rows) {
            for (y in 0 until columns) {
                val cell = getCellFromField(x, y)
                if (cell?.getChecker()?.getColorOfChecker() == Colors.BLACK) counterForBlack++
                if (cell?.getChecker()?.getColorOfChecker() == Colors.WHITE) counterForWhite++
            }
        }
        if (counterForWhite == 0) {
            flag = true
            flagForColorForWin = true
        }

        if (counterForBlack == 0) {
            flag = true
            flagForColorForWin = false
        }
        return Pair(flag, flagForColorForWin)
    }
}

data class Cell(var posX: Int, var posY: Int, var checkerForCell: Checker?) {

    fun cell(posX: Int, posY: Int, checkerForCell: Checker?) {
        this.posX = posX
        this.posY = posY
        this.checkerForCell = checkerForCell
    }

    fun setChecker(checker: Checker?) {
        this.checkerForCell = checker
    }

    fun getX(): Int {
        return posX
    }

    fun getY(): Int {
        return posY
    }

    fun getChecker(): Checker? {
        return checkerForCell
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Cell?) false
        else {
            !(this.posX != other?.posX || this.posY != other?.posY || this.checkerForCell != other?.checkerForCell)
        }
    }

    override fun hashCode(): Int {
        var result = posX
        result = 31 * result + posY
        result = 31 * result + (checkerForCell?.hashCode() ?: 0)
        return result
    }

}

enum class Colors(val rgb: Int) {
    WHITE(R.color.white),
    BLACK(R.color.black),
    POSITION_AT_THE_MOMENT(R.color.positionAtTheMomentColor),
    POSSIBLE_STEP_COLOR(R.color.possibleStepColor)
}

data class Checker(var color: Colors?, var queen: Boolean) {

    fun checker(color: Colors, queen: Boolean) {
        this.color = color
        this.queen = queen
    }

    fun getColorOfChecker(): Colors {
        return color!!
    }

    fun getInfoAboutQueen(): Boolean {
        return queen
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Checker) false
        else {
            !(this.queen != other.queen || this.color != other.color)
        }
    }

}