import kotlin.math.abs

class Skale(
    val name: String,
    val maxValue: Int,
    val indices: List<Int> = emptyList(),
    val inverseIndices: List<Int> = emptyList(),
    val offset: Int = 0,
)

fun NegativeAsInverseSkale(
    name: String,
    maxValue: Int,
    indices: List<Int>,
    offset: Int = 0,
): Skale {
    val (regular, inverse) = indices.partition { it >= 0 }
    return Skale(
        name = name,
        maxValue = maxValue,
        offset = offset,
        indices = regular,
        inverseIndices = inverse.map(::abs),
    )
}

fun Skale.toExpression(column: String = "A"): String {
    val skale = this
    val sizeGuesstimate = (skale.indices.size + skale.inverseIndices.size) * 4
    
    return buildString(capacity = sizeGuesstimate) {
        append('=')

        skale.indices.forEachIndexed { index, i ->
            append('+')
            append(column)
            append(i + skale.offset - 1)
        }
        skale.inverseIndices.forEachIndexed { index, i ->
            append('+')
            append('(')
            append(skale.maxValue + 1)
            append('-')
            append(column)
            append(i + skale.offset - 1)
            append(')')
        }
    }
}

fun main() {
    val skales = listOf(
        Skale(
            name = "Boldogság poz 1",
            maxValue = 4,
            indices = listOf(1, 3, 6, 8, 9, 11, 14, 16, 18, 19, 21, 23, 26, 28, 30),
            offset = 17,
        ),
        Skale(
            name = "Boldogság neg 1",
            maxValue = 4,
            inverseIndices = listOf(2, 4, 5, 7, 10, 12, 13, 15, 17, 20, 22, 24, 25, 27, 29),
            offset = 17,
        ),
        Skale(
            name = "Boldogság poz 2",
            maxValue = 4,
            indices = listOf(1, 3, 6, 8, 9, 11, 14, 16, 18, 19, 21, 23, 26, 28, 30),
            offset = 173,
        ),
        Skale(
            name = "Boldogság neg 2",
            maxValue = 4,
            inverseIndices = listOf(2, 4, 5, 7, 10, 12, 13, 15, 17, 20, 22, 24, 25, 27, 29),
            offset = 173,
        ),
        Skale(
            name = "MET S 1",
            maxValue = 6,
            indices = listOf(3, 11, 14),
            offset = 47,
        ),
        Skale(
            name = "MET J 1",
            maxValue = 6,
            indices = listOf(1, 16, 20),
            offset = 47,
        ),
        Skale(
            name = "MET AV 1",
            maxValue = 6,
            indices = listOf(5, 7, 17, 19),
            offset = 47,
        ),
        Skale(
            name = "MET R 1",
            maxValue = 6,
            inverseIndices = listOf(4, 6, 10, 12, 15),
            offset = 47,
        ),
        Skale(
            name = "MET Ö 1",
            maxValue = 6,
            indices = listOf(13),
            inverseIndices = listOf(2, 8, 18),
            offset = 47,
        ),
        Skale(
            name = "MET F 1",
            maxValue = 6,
            indices = listOf(9),
            offset = 47,
        ),
        Skale(
            name = "MET S 2",
            maxValue = 6,
            indices = listOf(3, 11, 14),
            offset = 203,
        ),
        Skale(
            name = "MET J 2",
            maxValue = 6,
            indices = listOf(1, 16, 20),
            offset = 203,
        ),
        Skale(
            name = "MET AV 2",
            maxValue = 6,
            indices = listOf(5, 7, 17, 19),
            offset = 203,
        ),
        Skale(
            name = "MET R 2",
            maxValue = 6,
            inverseIndices = listOf(4, 6, 10, 12, 15),
            offset = 203,
        ),
        Skale(
            name = "MET Ö 2",
            maxValue = 6,
            indices = listOf(13),
            inverseIndices = listOf(2, 8, 18),
            offset = 203,
        ),
        Skale(
            name = "MET F 2",
            maxValue = 6,
            indices = listOf(9),
            offset = 203,
        ),
    )

    for (skale in skales) {
        println(skale.name)
    }
    for (skale in skales) {
        println(skale.toExpression("C"))
    }
}
