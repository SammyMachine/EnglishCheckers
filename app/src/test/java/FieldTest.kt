import org.junit.Test
import org.junit.Assert.*

class FieldTest {
    var listener: Field.ActionListener? = null
    val comparedArray = Array(8) { x ->
        Array<Cell?>(8) { y -> Cell(x, y, null) }
    }

    @Test
    fun field() {
        val startField = Field(listener)
        startField.field()
        assertTrue(assertDeepContentEquals(comparedArray, startField.checkersField))
    }

    fun assertDeepContentEquals(array1: Array<Array<Cell?>>, array2: Array<Array<Cell?>?>): Boolean {
        var flag = true
        for (x in array1.indices) {
            for (y in array1[x].indices) {
                flag = array1.get(x).get(y) == array2.get(x)?.get(y)
                if (!flag) break
            }
        }
        return  flag
    }
}