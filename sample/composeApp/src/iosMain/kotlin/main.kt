import androidx.compose.ui.window.ComposeUIViewController
import it.maicol07.spraypaintkt.sample.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
