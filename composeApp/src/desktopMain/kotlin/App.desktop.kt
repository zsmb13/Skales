import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual fun copy(string: String) {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val data = StringSelection(string)
    clipboard.setContents(data, data)
}
