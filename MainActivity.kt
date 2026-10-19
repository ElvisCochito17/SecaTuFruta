package cl.duoc.secatufruta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.secatufruta.ui.screens.AuthScreen
import cl.duoc.secatufruta.ui.screens.BienvenidaScreen
import cl.duoc.secatufruta.ui.screens.MainScaffold
import cl.duoc.secatufruta.viewmodel.OylViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val navController = rememberNavController()
                    val oylViewModel: OylViewModel = viewModel()

                    NavHost(navController = navController, startDestination = "bienvenida") {
                        composable("bienvenida") {
                            BienvenidaScreen(onComenzar = { navController.navigate("auth") })
                        }
                        composable("auth") {
                            AuthScreen(
                                vm = oylViewModel,
                                onLoginSuccess = {
                                    navController.navigate("main") {
                                        popUpTo("bienvenida") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("main") {
                            MainScaffold(
                                vm = oylViewModel,
                                onLogout = {
                                    navController.navigate("auth") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}