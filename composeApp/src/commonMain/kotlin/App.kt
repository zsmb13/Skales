import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val skales = mutableStateListOf<Skale>(
            Skale(
                name = "Boldogság poz 2",
                points = 4,
                indices = setOf(1, 3, 6, 8, 9, 11, 14, 16, 18, 19, 21, 23, 26, 28, 30),
                offset = 173,
            ),
            Skale(
                name = "Boldogság neg 2",
                points = 4,
                reversedIndices = setOf(2, 4, 5, 7, 10, 12, 13, 15, 17, 20, 22, 24, 25, 27, 29),
                offset = 173,
            ),
            Skale(
                name = "MET S 1",
                points = 6,
                indices = setOf(3, 11, 14),
                offset = 47,
            ),
            Skale(
                name = "MET J 1",
                points = 6,
                indices = setOf(1, 16, 20),
                offset = 47,
            ),
        )
        var editingIndex by remember { mutableIntStateOf(-1) }

        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            LazyColumn(Modifier.weight(1f)) {
                itemsIndexed(skales) { index, skale ->
                    SkaleRow(
                        skale = skale,
                        isBeingEdited = index == editingIndex,
                        startEdit = { editingIndex = index },
                        stopEdit = { editingIndex = -1 },
                        deleteSkale = { skales.removeAt(index) },
                        updateSkale = { skales[index] = it },
                        moveUp = { skales.add(index, skales.removeAt(index - 1)) },
                        moveDown = { skales.add(index, skales.removeAt(index + 1)) },
                    )
                }
            }

            BottomStuff(skales)
        }
    }
}

@Composable
private fun BottomStuff(
    skales: SnapshotStateList<Skale>
) {

    var globalOffset by remember { mutableStateOf("") }
    var globalColumn by remember { mutableStateOf("") }

    val derivedOffset by derivedStateOf { globalOffset.toIntOrNull() }
    val derivedColumn by derivedStateOf {
        if (globalColumn.isNotBlank() && globalColumn.all { it.isLetter() }) {
            globalColumn.uppercase()
        } else {
            null
        }
    }

    Column {
        Button({ skales.add(Skale("New scale", 5)) }) {
            Text("New scale")
        }

        Row {
            Text("Global offset")
            TextField(
                globalOffset,
                onValueChange = { globalOffset = it },
                isError = globalOffset.isNotEmpty() && derivedOffset == null
            )
        }
        Row {
            Text("Global column")
            TextField(
                globalColumn,
                onValueChange = { globalColumn = it },
                isError = globalColumn.isNotEmpty() && derivedColumn == null
            )
        }
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button({
            copy(
                skales.joinToString(
                    separator = "\n",
                    transform = { "${it.name}\t${it.toExpression(derivedColumn, derivedOffset)}" }
                )
            )
        }) {
            Text("Copy all")
        }
        TextButton({
            copy(
                skales.joinToString(separator = "\n", transform = { it.name })
            )
        }) {
            Text("Copy names")
        }
        TextButton({
            copy(
                skales.joinToString(
                    separator = "\n",
                    transform = { it.toExpression(derivedColumn, derivedOffset) }
                )
            )
        }) {
            Text("Copy formulas")
        }
    }
}

expect fun copy(string: String)

@Composable
fun SkaleRow(
    skale: Skale,
    isBeingEdited: Boolean,
    startEdit: () -> Unit,
    stopEdit: () -> Unit,
    deleteSkale: () -> Unit,
    updateSkale: (skale: Skale) -> Unit,
    moveUp: () -> Unit,
    moveDown: () -> Unit
) {
    if (!isBeingEdited) {
        ReadOnlyRow(skale, startEdit, deleteSkale, moveUp, moveDown)
    } else {
        EditableRow(skale, stopEdit, updateSkale)
    }
}

@Composable
fun EditableRow(skale: Skale, stopEdit: () -> Unit, updateSkale: (skale: Skale) -> Unit) {
    var name: String by remember { mutableStateOf(skale.name) }
    var points: String by remember { mutableStateOf(skale.points.toString()) }
    var indicesString by remember(skale) { mutableStateOf(skale.indices.joinToString()) }
    var reversedIndicesString by remember(skale) { mutableStateOf(skale.reversedIndices.joinToString()) }

    Row(Modifier.padding(20.dp).fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Row {
                Text("Name")
                TextField(name, onValueChange = { name = it })
            }
            Row {
                Text("Points")
                TextField(points, onValueChange = { points = it })
            }
            Row {
                Text("Normal items")
                TextField(indicesString, onValueChange = { indicesString = it })
            }
            Row {
                Text("Reversed items")
                TextField(reversedIndicesString, onValueChange = { reversedIndicesString = it })
            }
        }
        Row {
            IconButton(onClick = {
                val indices = "\\d+".toRegex().findAll(indicesString).map { it.value.toInt() }.toSet()
                val reversedIndices = "\\d+".toRegex().findAll(reversedIndicesString).map { it.value.toInt() }.toSet()

                if ((indices intersect reversedIndices).isNotEmpty()) {
                    // TODO ("better error handling")
                    return@IconButton
                }

                updateSkale(
                    skale.copy(
                        name = name,
                        points = points.toInt(),
                        indices = indices,
                        reversedIndices = reversedIndices,
                    )
                )
                stopEdit()
            }) {
                Icon(Icons.Default.Done, null)
            }
            IconButton(onClick = { stopEdit() }) {
                Icon(Icons.Default.Close, null)
            }
        }
    }
}

@Composable
fun ReadOnlyRow(
    skale: Skale,
    startEdit: () -> Unit,
    deleteSkale: () -> Unit,
    moveUp: () -> Unit,
    moveDown: () -> Unit
) {
    Row(Modifier.padding(20.dp).fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Text("${skale.name} (1↔${skale.points})", fontWeight = FontWeight.SemiBold)
            if (skale.indices.isNotEmpty()) {
                Text(skale.indices.joinToString())
            }
            if (skale.reversedIndices.isNotEmpty()) {
                Text(skale.reversedIndices.joinToString())
            }
        }
        Row {
            IconButton(onClick = { startEdit() }) {
                Icon(Icons.Default.Edit, null)
            }
            IconButton(onClick = { deleteSkale() }) {
                Icon(Icons.Default.Delete, null)
            }
            IconButton(onClick = { moveDown() }) {
                Icon(Icons.Default.KeyboardArrowDown, null)
            }
            IconButton(onClick = { moveUp() }) {
                Icon(Icons.Default.KeyboardArrowUp, null)
            }
        }
    }
}
