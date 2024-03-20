import kotlin.math.abs

data class Skale(
    val name: String,
    val points: Int,
    val indices: Set<Int> = emptySet(),
    val reversedIndices: Set<Int> = emptySet(),
    val offset: Int? = null,
    val column: String? = null,
)

fun Skale.toExpression(column: String? = null, offset: Int? = null): String {
    val skale = this
    val sizeGuesstimate = (skale.indices.size + skale.reversedIndices.size) * 4

    val realColumn = column ?: skale.column ?: "A"
    val realOffset = offset ?: skale.offset ?: 0

    return buildString(capacity = sizeGuesstimate) {
        append('=')

        skale.indices.forEach { itemIndex ->
            append('+')
            append(realColumn)
            append(itemIndex + (realOffset) - 1)
        }
        skale.reversedIndices.forEach { itemIndex ->
            append('+')
            append('(')
            append(skale.points + 1)
            append('-')
            append(realColumn)
            append(itemIndex + (realOffset) - 1)
            append(')')
        }
    }
}
