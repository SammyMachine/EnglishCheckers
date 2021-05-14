import com.example.englishcheckers.R

class Field(listener: ActionListener) {

    lateinit var checkersField: Array<Array<Cell?>?>
    private val columns = 8
    private val rows = 8
    private var actionListener: ActionListener = listener

    fun actionListener(actionListener: ActionListener) {
        this.actionListener = actionListener
    }

    fun getActionListener(): ActionListener {
        return actionListener
    }

    interface ActionListener {
        fun checkerAdded(position: Cell)
        fun checkerMoved(start: Cell, end: Cell)
        fun checkerWasEaten(eatenCell: Cell)
    }


    fun field() {
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
                    actionListener.checkerAdded(Cell(x, y, checker))

                }
            }
            for (y in 5..7)
                if ((y + x) % 2 != 0) {
                    val checker = Checker(Colors.BLACK, false)
                    checkersField.get(x)?.get(y)?.setChecker(checker)
                    actionListener.checkerAdded(Cell(x, y, checker))
                }
        }
    }

    fun cellInStartField(posX: Int, posY: Int): Boolean = (posX in 0..7 && posY in 0..7)

    fun getCellFromField(posX: Int, posY: Int): Cell? {
        return if (cellInStartField(posX, posY)) {
            checkersField.get(posX)?.get(posY)
        } else null
    }

    //moveFlag: true when BlackPlayer move; false when WhitePlayer move

    fun requiredSteps(moveFlag: Boolean): MutableMap<Cell?, MutableList<Cell?>> {
        val varyList = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        val requiredSteps = mutableMapOf<Cell?, MutableList<Cell?>>()
        val listOfPotentialCells = mutableListOf<Cell?>()
        for (x in 0 until rows)
            for (y in 0 until columns) {
                val cell = getCellFromField(x, y)
                if (cell!!.getChecker() != null) {
                    if ((cell.getChecker()!!.getColorOfChecker() == Colors.BLACK && moveFlag) || (cell.getChecker()!!.getColorOfChecker() == Colors.WHITE && !moveFlag)) {
                        if (cell.getChecker()!!.getInfoAboutQueen()) {
                            for (element in varyList) {
                                val potentialCell = getCellFromField(cell.getX() + 2 * element.first, cell.getY() + 2 * element.second)
                                if (potentialCell != null)
                                    if (potentialCell.getChecker() == null) {
                                        val cellBetweenOurAndPotential = getCellFromField(cell.getX() + element.first, cell.getY() + element.second)
                                        if (cellBetweenOurAndPotential!!.getChecker() != null && cellBetweenOurAndPotential.getChecker()?.getColorOfChecker() != cell.getChecker()?.getColorOfChecker()) {
                                            listOfPotentialCells.add(potentialCell)
                                        }
                                    }
                            }
                            if (listOfPotentialCells.size != 0) {
                                requiredSteps[cell] = mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                                listOfPotentialCells.clear()
                            }
                        } else {
                            if (cell.getChecker()!!.getColorOfChecker() == Colors.BLACK) {
                                for (i in 0 until varyList.size - 1 step 2) {
                                    val potentialCell = getCellFromField(cell.getX() + 2 * varyList[i].first, cell.getY() + 2 * varyList[i].second)
                                    if (potentialCell != null)
                                        if (potentialCell.getChecker() == null) {
                                            val cellBetweenOurAndPotential = getCellFromField(cell.getX() + varyList[i].first, cell.getY() + varyList[i].second)
                                            if (cellBetweenOurAndPotential!!.getChecker() != null && cellBetweenOurAndPotential.getChecker()?.getColorOfChecker() == Colors.WHITE)
                                                listOfPotentialCells.add(potentialCell)
                                        }
                                }
                                if (listOfPotentialCells.size != 0) {
                                    requiredSteps[cell] = mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                                    listOfPotentialCells.clear()
                                }
                            } else {
                                for (i in 1 until varyList.size step 2) {
                                    val potentialCell = getCellFromField(cell.getX() + 2 * varyList[i].first, cell.getY() + 2 * varyList[i].second)
                                    if (potentialCell != null)
                                        if (potentialCell.getChecker() == null) {
                                            val cellBetweenOurAndPotential = getCellFromField(cell.getX() + varyList[i].first, cell.getY() + varyList[i].second)
                                            if (cellBetweenOurAndPotential!!.getChecker() != null && cellBetweenOurAndPotential.getChecker()?.getColorOfChecker() == Colors.BLACK)
                                                listOfPotentialCells.add(potentialCell)
                                        }
                                }
                                if (listOfPotentialCells.size != 0) {
                                    requiredSteps[cell] = mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                                    listOfPotentialCells.clear()
                                }
                            }
                        }
                    }
                }
            }
        return requiredSteps
    }


    fun possibleSteps(cell: Cell, moveFlag: Boolean): MutableList<Cell?> {
        val varyList = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        val possibleSteps = mutableListOf<Cell?>()
        if ((cell.getChecker()!!.getColorOfChecker() == Colors.BLACK && moveFlag) || (cell.getChecker()!!.getColorOfChecker() == Colors.WHITE && !moveFlag)) {
            if (cell.getChecker()!!.getInfoAboutQueen()) {
                for (element in varyList) {
                    val potentialCell = getCellFromField(cell.getX() + element.first, cell.getY() + element.second)
                    if (potentialCell != null)
                        if (potentialCell.getChecker() == null)
                            possibleSteps.add(potentialCell)
                }
            }
            if (!cell.getChecker()!!.getInfoAboutQueen()) {
                if (cell.getChecker()?.getColorOfChecker() == Colors.BLACK) {
                    for (i in 0 until varyList.size - 1 step 2) {
                        val potentialCell = getCellFromField(cell.getX() + varyList[i].first, cell.getY() + varyList[i].second)
                        if (potentialCell != null)
                            if (potentialCell.getChecker() == null)
                                possibleSteps.add(potentialCell)
                    }
                }
                if (cell.getChecker()?.getColorOfChecker() == Colors.WHITE)
                    for (i in 1 until varyList.size step 2) {
                        val potentialCell = getCellFromField(cell.getX() + varyList[i].first, cell.getY() + varyList[i].second)
                        if (potentialCell != null)
                            if (potentialCell.getChecker() == null)
                                possibleSteps.add(potentialCell)
                    }
            }
        }
        return possibleSteps
    }

    fun checkForEatMore(selectedCell: Cell?, possibleSteps: MutableMap<Cell?, MutableList<Cell?>>): MutableMap<Cell?, MutableList<Cell?>> {
        possibleSteps.clear()
        val listOfPotentialCells = mutableListOf<Cell?>()
        val varyList = listOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
        if (selectedCell?.getChecker()!!.getInfoAboutQueen()) {
            for (i in varyList.indices) {
                val potentialCell = getCellFromField(selectedCell.getX() + 2 * varyList[i].first, selectedCell.getY() + 2 * varyList[i].second)
                if (potentialCell != null)
                    if (potentialCell.getChecker() == null) {
                        val cellBetweenOurAndPotential = getCellFromField(selectedCell.getX() + varyList[i].first, selectedCell.getY() + varyList[i].second)
                        if (cellBetweenOurAndPotential!!.getChecker() != null && cellBetweenOurAndPotential.getChecker()?.getColorOfChecker() != selectedCell.getChecker()?.getColorOfChecker())
                            listOfPotentialCells.add(potentialCell)
                    }
                if (listOfPotentialCells.size != 0) {
                    possibleSteps[selectedCell] = mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                    listOfPotentialCells.clear()
                }
            }
        } else {
            if (selectedCell.getChecker()!!.getColorOfChecker() == Colors.BLACK) {
                for (i in 0 until varyList.size - 1 step 2) {
                    val potentialCell = getCellFromField(selectedCell.getX() + 2 * varyList[i].first, selectedCell.getY() + 2 * varyList[i].second)
                    if (potentialCell != null)
                        if (potentialCell.getChecker() == null) {
                            val cellBetweenOurAndPotential = getCellFromField(selectedCell.getX() + varyList[i].first, selectedCell.getY() + varyList[i].second)
                            if (cellBetweenOurAndPotential!!.getChecker() != null && cellBetweenOurAndPotential.getChecker()?.getColorOfChecker() != selectedCell.getChecker()?.getColorOfChecker())
                                listOfPotentialCells.add(potentialCell)
                        }
                }
                if (listOfPotentialCells.size != 0) {
                    possibleSteps[selectedCell] = mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                    listOfPotentialCells.clear()
                }
            } else {
                for (i in 1 until varyList.size step 2) {
                    val potentialCell = getCellFromField(selectedCell.getX() + 2 * varyList[i].first, selectedCell.getY() + 2 * varyList[i].second)
                    if (potentialCell != null)
                        if (potentialCell.getChecker() == null) {
                            val cellBetweenOurAndPotential = getCellFromField(selectedCell.getX() + varyList[i].first, selectedCell.getY() + varyList[i].second)
                            if (cellBetweenOurAndPotential!!.getChecker() != null && cellBetweenOurAndPotential.getChecker()?.getColorOfChecker() != selectedCell.getChecker()?.getColorOfChecker())
                                listOfPotentialCells.add(potentialCell)
                        }
                }
                if (listOfPotentialCells.size != 0) {
                    possibleSteps[selectedCell] = mutableListOf<Cell?>().apply { addAll(listOfPotentialCells) }
                    listOfPotentialCells.clear()
                }
            }
        }
        return possibleSteps
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
        return if (other !is Cell) false
        else {
            !(this.posX != other.posX || this.posY != other.posY)
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
}

data class Checker(var color: Colors, var queen: Boolean) {

    fun checker(color: Colors, queen: Boolean) {
        this.color = color
        this.queen = queen
    }

    fun getColorOfChecker(): Colors {
        return color
    }

    fun getInfoAboutQueen(): Boolean {
        return queen
    }

}