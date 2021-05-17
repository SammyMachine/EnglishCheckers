
import org.junit.Test
import org.junit.Assert.*

class FieldTest {
    var listener: Field.ActionListener? = null

    @Test
    fun field() {
        val comparedArray = Array(8) { x ->
            Array<Cell?>(8) { y -> Cell(x, y, null) }
        }
        val startField = Field(listener)
        startField.field()
        assertTrue(assertDeepContentEquals(comparedArray, startField.checkersField))
    }

    @Test
    fun fillFieldByCheckers() {
        val comparedArrayWithCheckers = Array(8) { x ->
            Array<Cell?>(8) { y ->
                if ((x + y) % 2 != 0) {
                    if (y < 3) Cell(x, y, Checker(Colors.WHITE, false))
                    else if (y > 4) Cell(x, y, Checker(Colors.BLACK, false))
                    else Cell(x, y, null)
                }
                else Cell(x, y, null)}
        }
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        assertTrue(assertDeepContentEquals(comparedArrayWithCheckers, startField.checkersField))
    }

    @Test
    fun cellInStartField() {
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        assertTrue(startField.cellInStartField(1, 2))
        assertTrue(startField.cellInStartField(3, 5))
        assertTrue(startField.cellInStartField(7, 1))
        assertTrue(startField.cellInStartField(0, 7))
        assertFalse(startField.cellInStartField(-1, 2))
        assertFalse(startField.cellInStartField(2, -1))
        assertFalse(startField.cellInStartField(8, 2))
        assertFalse(startField.cellInStartField(2, 8))
        assertFalse(startField.cellInStartField(8, -2))
        assertFalse(startField.cellInStartField(-2, 8))
    }

    @Test
    fun getCellFromField() {
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        assertEquals(Cell(0, 5, Checker(Colors.BLACK, false)), startField.getCellFromField(0, 5))
        assertEquals(Cell(1, 4, null), startField.getCellFromField(1, 4))
        assertEquals(Cell(0, 1, Checker(Colors.WHITE, false)), startField.getCellFromField(0, 1))
        assertEquals(Cell(0, 0, null), startField.getCellFromField(0, 0))
        assertEquals(null, startField.getCellFromField(-1, 5))
        assertEquals(null, startField.getCellFromField(1, -5))
        assertEquals(null, startField.getCellFromField(8, 5))
        assertEquals(null, startField.getCellFromField(1, 8))
        assertEquals(null, startField.getCellFromField(-1, 8))
    }

    @Test
    fun eatChecker() {
        val comparedArrayWithCheckers = Array(8) { x ->
            Array<Cell?>(8) { y ->
                if ((x + y) % 2 != 0) {
                    if (y < 3) Cell(x, y, Checker(Colors.WHITE, false))
                    else if (y > 4) Cell(x, y, Checker(Colors.BLACK, false))
                    else Cell(x, y, null)
                }
                else Cell(x, y, null)}
        }
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        comparedArrayWithCheckers[0][5] = Cell(0, 5, null)
        startField.eatChecker(startField.getCellFromField(0, 5)!!)
        assertDeepContentEquals(comparedArrayWithCheckers, startField.checkersField)
        comparedArrayWithCheckers[1][3] = Cell(1, 3, null)
        startField.eatChecker(startField.getCellFromField(1, 3)!!)
    }

    @Test
    fun moveChecker() {
        val comparedArrayWithCheckers = Array(8) { x ->
            Array<Cell?>(8) { y ->
                if ((x + y) % 2 != 0) {
                    if (y < 3) Cell(x, y, Checker(Colors.WHITE, false))
                    else if (y > 4) Cell(x, y, Checker(Colors.BLACK, false))
                    else Cell(x, y, null)
                }
                else Cell(x, y, null)}
        }
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        comparedArrayWithCheckers[0][5] = Cell(0, 5, null)
        comparedArrayWithCheckers[1][4] = Cell(1, 4, Checker(Colors.BLACK, false))
        startField.moveChecker(startField.getCellFromField(0, 5)!!, startField.getCellFromField(1, 4)!!)
        assertDeepContentEquals(comparedArrayWithCheckers, startField.checkersField)
        comparedArrayWithCheckers[1][3] = Cell(1, 3, null)
        comparedArrayWithCheckers[2][4] = Cell(2, 4, Checker(Colors.WHITE, false))
        startField.moveChecker(startField.getCellFromField(1, 3)!!, startField.getCellFromField(2, 4)!!)
        assertDeepContentEquals(comparedArrayWithCheckers, startField.checkersField)
        comparedArrayWithCheckers[2][5] = Cell(2, 5, null)
        comparedArrayWithCheckers[3][4] = Cell(3, 4, Checker(Colors.BLACK, false))
        startField.moveChecker(startField.getCellFromField(2, 5)!!, startField.getCellFromField(3, 4)!!)
        assertDeepContentEquals(comparedArrayWithCheckers, startField.checkersField)
        comparedArrayWithCheckers[1][4] = Cell(1, 4, null)
        comparedArrayWithCheckers[2][4] = Cell(2, 4, null)
        comparedArrayWithCheckers[0][5] = Cell(0, 5, Checker(Colors.WHITE, false))
        startField.eatChecker(startField.getCellFromField(1, 4)!!)
        startField.moveChecker(startField.getCellFromField(2, 4)!!, startField.getCellFromField(0, 5)!!)
    }

    @Test
    fun changeToQueen() {
        val comparedArrayWithCheckers = Array(8) { x ->
            Array<Cell?>(8) { y ->
                if ((x + y) % 2 != 0) {
                    if (y < 3) Cell(x, y, Checker(Colors.WHITE, false))
                    else if (y > 4) Cell(x, y, Checker(Colors.BLACK, false))
                    else Cell(x, y, null)
                }
                else Cell(x, y, null)}
        }
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        comparedArrayWithCheckers[0][5] = Cell(0, 5, null)
        comparedArrayWithCheckers[1][4] = Cell(1, 4, Checker(Colors.BLACK, false))
        startField.moveChecker(startField.getCellFromField(0, 5)!!, startField.getCellFromField(1, 4)!!)
        comparedArrayWithCheckers[1][2] = Cell(1, 2, null)
        comparedArrayWithCheckers[2][3] = Cell(2, 3, Checker(Colors.WHITE, false))
        startField.moveChecker(startField.getCellFromField(1, 2)!!, startField.getCellFromField(2, 3)!!)
        comparedArrayWithCheckers[2][5] = Cell(2, 5, null)
        comparedArrayWithCheckers[3][4] = Cell(3, 4, Checker(Colors.BLACK, false))
        startField.moveChecker(startField.getCellFromField(2, 5)!!, startField.getCellFromField(3, 4)!!)
        comparedArrayWithCheckers[1][4] = Cell(1, 4, null)
        comparedArrayWithCheckers[2][3] = Cell(2, 3, null)
        comparedArrayWithCheckers[0][5] = Cell(0, 5, Checker(Colors.WHITE, false))
        startField.eatChecker(startField.getCellFromField(1, 4)!!)
        startField.moveChecker(startField.getCellFromField(2, 3)!!, startField.getCellFromField(0, 5)!!)
        comparedArrayWithCheckers[1][6] = Cell(1, 6, null)
        comparedArrayWithCheckers[2][5] = Cell(2, 5, Checker(Colors.BLACK, false))
        startField.moveChecker(startField.getCellFromField(1, 6)!!, startField.getCellFromField(2, 5)!!)
        comparedArrayWithCheckers[3][2] = Cell(3, 2, null)
        comparedArrayWithCheckers[4][3] = Cell(4, 3, Checker(Colors.WHITE, false))
        startField.moveChecker(startField.getCellFromField(3, 2)!!, startField.getCellFromField(4, 3)!!)
        comparedArrayWithCheckers[2][7] = Cell(2, 7, null)
        comparedArrayWithCheckers[1][6] = Cell(1, 6, Checker(Colors.BLACK, false))
        startField.moveChecker(startField.getCellFromField(2, 7)!!, startField.getCellFromField(1, 6)!!)
        comparedArrayWithCheckers[1][6] = Cell(1, 6, null)
        comparedArrayWithCheckers[0][5] = Cell(0, 5, null)
        comparedArrayWithCheckers[2][7] = Cell(2, 7, Checker(Colors.WHITE, true))
        startField.moveChecker(startField.getCellFromField(0, 5)!!, startField.getCellFromField(2, 7)!!)
        startField.eatChecker(startField.getCellFromField(1, 6)!!)
        startField.changeToQueen(startField.getCellFromField(2, 7)!!)
        assertDeepContentEquals(comparedArrayWithCheckers, startField.checkersField)


    }

    @Test
    fun requiredSteps() {
        val comparedArrayWithCheckers = Array(8) { x ->
            Array<Cell?>(8) { y ->
                if ((x + y) % 2 != 0) {
                    if (y < 3) Cell(x, y, Checker(Colors.WHITE, false))
                    else if (y > 4) Cell(x, y, Checker(Colors.BLACK, false))
                    else Cell(x, y, null)
                }
                else Cell(x, y, null)}
        }
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        val requiredSteps = mutableMapOf<Cell?, MutableList<Cell?>>()
        assertEquals(requiredSteps, startField.requiredSteps(true))
        comparedArrayWithCheckers[0][5] = Cell(0, 5, null)
        comparedArrayWithCheckers[1][4] = Cell(1, 4, Checker(Colors.BLACK, false))
        startField.moveChecker(startField.getCellFromField(0, 5)!!, startField.getCellFromField(1, 4)!!)
        comparedArrayWithCheckers[7][2] = Cell(7, 2, null)
        comparedArrayWithCheckers[6][3] = Cell(6, 3, Checker(Colors.WHITE, false))
        startField.moveChecker(startField.getCellFromField(7, 2)!!, startField.getCellFromField(6, 3)!!)
        comparedArrayWithCheckers[1][4] = Cell(1, 4, null)
        comparedArrayWithCheckers[2][3] = Cell(2, 3, Checker(Colors.BLACK, false))
        startField.moveChecker(startField.getCellFromField(1, 4)!!, startField.getCellFromField(2, 3)!!)
        requiredSteps[comparedArrayWithCheckers[1][2]] = mutableListOf(comparedArrayWithCheckers[3][4])
        requiredSteps[comparedArrayWithCheckers[3][2]] = mutableListOf(comparedArrayWithCheckers[1][4])
        assertEquals(requiredSteps, startField.requiredSteps(false))

    }

    @Test
    fun possibleSteps() {
        val comparedArrayWithCheckers = Array(8) { x ->
            Array<Cell?>(8) { y ->
                if ((x + y) % 2 != 0) {
                    if (y < 3) Cell(x, y, Checker(Colors.WHITE, false))
                    else if (y > 4) Cell(x, y, Checker(Colors.BLACK, false))
                    else Cell(x, y, null)
                }
                else Cell(x, y, null)}
        }
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        val possibleSteps = mutableMapOf<Cell?, MutableList<Cell?>>()
        possibleSteps[comparedArrayWithCheckers[0][5]] = mutableListOf(comparedArrayWithCheckers[1][4])
        possibleSteps[comparedArrayWithCheckers[2][5]] = mutableListOf(comparedArrayWithCheckers[1][4], comparedArrayWithCheckers[3][4])
        possibleSteps[comparedArrayWithCheckers[4][5]] = mutableListOf(comparedArrayWithCheckers[3][4], comparedArrayWithCheckers[5][4])
        possibleSteps[comparedArrayWithCheckers[6][5]] = mutableListOf(comparedArrayWithCheckers[5][4], comparedArrayWithCheckers[7][4])
        assertEquals(possibleSteps, startField.possibleSteps(true))
    }

    @Test
    fun flagToMove() {
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        val chosenCell = startField.getCellFromField(3, 4)
        assertEquals(true, startField.flagToMove(startField.getCellFromField(2, 5)!!, chosenCell!!, startField.possibleSteps(true)))
    }

    @Test
    fun flagToPickAnotherColor() {
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        val chosenCell1 = startField.getCellFromField(2, 5)
        val chosenCell2 = startField.getCellFromField(1, 2)
        assertEquals(true, startField.flagToPickAnotherColor(chosenCell1!!, true))
        assertEquals(false, startField.flagToPickAnotherColor(chosenCell2!!, true))
        startField.moveChecker(startField.getCellFromField(0, 5)!!, startField.getCellFromField(4, 1)!!)
        assertEquals(true, startField.flagToPickAnotherColor(chosenCell2, false))
        assertEquals(false, startField.flagToPickAnotherColor(chosenCell1, false))
    }

    @Test
    fun flagToPickNonEater() {
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        val chosenCell2 = startField.getCellFromField(5, 2)
        val chosenCell3 = startField.getCellFromField(1, 2)
        val chosenCell4 = startField.getCellFromField(3, 2)
        startField.moveChecker(startField.getCellFromField(0, 5)!!, startField.getCellFromField(1, 4)!!)
        startField.moveChecker(startField.getCellFromField(7, 2)!!, startField.getCellFromField(6, 3)!!)
        val chosenCell1 = startField.getCellFromField(6, 3)
        startField.moveChecker(startField.getCellFromField(1, 4)!!, startField.getCellFromField(2, 3)!!)
        assertEquals(false, startField.flagToPickNonEater(chosenCell1!!, startField.requiredSteps(false)))
        assertEquals(false, startField.flagToPickNonEater(chosenCell2!!, startField.requiredSteps(false)))
        assertEquals(true, startField.flagToPickNonEater(chosenCell3!!, startField.requiredSteps(false)))
        assertEquals(true, startField.flagToPickNonEater(chosenCell4!!, startField.requiredSteps(false)))
    }

    @Test
    fun multiEatSituation() {
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        startField.moveChecker(startField.getCellFromField(0, 5)!!, startField.getCellFromField(1, 4)!!)
        startField.moveChecker(startField.getCellFromField(1, 2)!!, startField.getCellFromField(2, 3)!!)
        startField.moveChecker(startField.getCellFromField(2, 5)!!, startField.getCellFromField(3, 4)!!)
        startField.eatChecker(startField.getCellFromField(1, 4)!!)
        startField.moveChecker(startField.getCellFromField(2, 3)!!, startField.getCellFromField(0, 5)!!)
        startField.moveChecker(startField.getCellFromField(1, 6)!!, startField.getCellFromField(2, 5)!!)
        startField.moveChecker(startField.getCellFromField(3, 2)!!, startField.getCellFromField(4, 3)!!)
        startField.moveChecker(startField.getCellFromField(2, 7)!!, startField.getCellFromField(1, 6)!!)
        startField.moveChecker(startField.getCellFromField(0, 5)!!, startField.getCellFromField(2, 7)!!)
        startField.eatChecker(startField.getCellFromField(1, 6)!!)
        startField.changeToQueen(startField.getCellFromField(2, 7)!!)
        startField.moveChecker(startField.getCellFromField(6, 5)!!, startField.getCellFromField(7, 4)!!)
        startField.moveChecker(startField.getCellFromField(0, 1)!!, startField.getCellFromField(1, 2)!!)
        startField.moveChecker(startField.getCellFromField(4, 5)!!, startField.getCellFromField(5, 4)!!)
        startField.moveChecker(startField.getCellFromField(2, 7)!!, startField.getCellFromField(4, 5)!!)
        startField.eatChecker(startField.getCellFromField(3, 6)!!)
        val cell1 = startField.getCellFromField(2, 3)
        val cell2 = startField.getCellFromField(6, 3)
        val cell3 = startField.getCellFromField(6, 7)
        val cell4 = startField.getCellFromField(2, 7)
        startField.selectedCell = startField.getCellFromField(4, 5)!!
        startField.potentialSteps = startField.requiredSteps(false)
        assertEquals(true, startField.multiEatSituation(cell1, startField.potentialSteps))
        assertEquals(true, startField.multiEatSituation(cell2, startField.potentialSteps))
        assertEquals(false, startField.multiEatSituation(cell3, startField.potentialSteps))
        assertEquals(false, startField.multiEatSituation(cell4, startField.potentialSteps))
    }

    @Test
    fun checkForEatMore() {
        val startField = Field(listener)
        startField.field()
        startField.fillFieldByCheckers()
        startField.moveChecker(startField.getCellFromField(0, 5)!!, startField.getCellFromField(1, 4)!!)
        startField.moveChecker(startField.getCellFromField(1, 2)!!, startField.getCellFromField(2, 3)!!)
        startField.moveChecker(startField.getCellFromField(2, 5)!!, startField.getCellFromField(3, 4)!!)
        startField.eatChecker(startField.getCellFromField(1, 4)!!)
        startField.moveChecker(startField.getCellFromField(2, 3)!!, startField.getCellFromField(0, 5)!!)
        startField.moveChecker(startField.getCellFromField(1, 6)!!, startField.getCellFromField(2, 5)!!)
        startField.moveChecker(startField.getCellFromField(3, 2)!!, startField.getCellFromField(4, 3)!!)
        startField.moveChecker(startField.getCellFromField(2, 7)!!, startField.getCellFromField(1, 6)!!)
        startField.moveChecker(startField.getCellFromField(0, 5)!!, startField.getCellFromField(2, 7)!!)
        startField.eatChecker(startField.getCellFromField(1, 6)!!)
        startField.changeToQueen(startField.getCellFromField(2, 7)!!)
        startField.moveChecker(startField.getCellFromField(6, 5)!!, startField.getCellFromField(7, 4)!!)
        startField.moveChecker(startField.getCellFromField(0, 1)!!, startField.getCellFromField(1, 2)!!)
        startField.moveChecker(startField.getCellFromField(4, 5)!!, startField.getCellFromField(5, 4)!!)
        startField.moveChecker(startField.getCellFromField(2, 7)!!, startField.getCellFromField(4, 5)!!)
        startField.eatChecker(startField.getCellFromField(3, 6)!!)
        startField.selectedCell = startField.getCellFromField(4, 5)!!
        startField.potentialSteps = startField.requiredSteps(false)
        val entry = mutableMapOf<Cell?, MutableList<Cell?>>()
        entry[startField.selectedCell] = startField.potentialSteps[startField.selectedCell]!!
        val requiredSteps = mutableMapOf<Cell?, MutableList<Cell?>>()
        requiredSteps[startField.selectedCell] = mutableListOf(startField.getCellFromField(2, 3), startField.getCellFromField(6, 3))
        assertEquals(requiredSteps ,startField.checkForEatMore(startField.selectedCell, entry))
    }

    private fun assertDeepContentEquals(array1: Array<Array<Cell?>>, array2: Array<Array<Cell?>?>): Boolean {
        var flag = true
        Loop@ for (x in array1.indices) {
            for (y in array1[x].indices) {
                flag = array1[x][y] == array2[x]?.get(y)
                if (!flag) break@Loop
            }
        }
        return  flag
    }
}