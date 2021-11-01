package dvp.app.assistant.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import dvp.app.assistant.ui.components.TopBar
import dvp.app.assistant.ui.screens.home.HomeScreen


@Composable
fun MainGraph() {
    val navController = rememberNavController()
    val viewModel = AppViewModel()
    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .statusBarsPadding(),
        topBar = {
            TopBar(viewModel = viewModel)
        },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                NavHost(navController = navController, startDestination = startRoute) {
                    composable(Screen.Home.route) {
                        HomeScreen()
                    }
                }
            }
        }
    )

}



