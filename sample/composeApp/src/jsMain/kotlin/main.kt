import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import it.maicol07.spraypaintkt.sample.App
import org.jetbrains.skiko.wasm.onWasmReady
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        val body = document.body ?: return@onWasmReady
        ComposeViewport(body) {
            App()
        }
    }
}
