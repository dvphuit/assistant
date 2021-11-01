package dvp.app.assistant.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * @author PhuDV
 * Created 8/1/21 at 3:37 PM
 */

val startRoute = Screen.Home.route

sealed class Screen(var route: String, var icon: ImageVector, var title: String) {
    object Splash : Screen("splash", Icons.Filled.Favorite, "Splash")
    object Home : Screen("home", Icons.Filled.Favorite, "Home")
    object Library : Screen("library", Icons.Filled.Favorite, "Library")
    object Setting : Screen("setting", Icons.Filled.Favorite, "Setting")
}

val bottomBar = listOf(
    Screen.Home,
    Screen.Library,
    Screen.Setting,
)

sealed class Screens {
    object Home : Screens()
    object Library : Screens()
    object Setting : Screens()
}
