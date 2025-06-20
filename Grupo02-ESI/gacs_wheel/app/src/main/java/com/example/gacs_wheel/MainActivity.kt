package com.example.gacs_wheel

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gacs_wheel.View.InicioScreen
import com.example.gacs_wheel.View.LoginScreen
import com.example.gacs_wheel.View.RegistroScreen
import com.example.gacs_wheel.View.AdminVehiculoScreen
import com.example.gacs_wheel.View.AgregarVehiculoScreen
import com.example.gacs_wheel.ui.theme.Gacs_wheelTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Gacs_wheelTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(navController)
                        }
                        composable("registro") {
                            RegistroScreen(navController)
                        }
                        composable("inicio") {
                            InicioScreen(navController)
                        }
                        composable("admin") {
                            AdminVehiculoScreen(navController) // ← nombre correcto como composable
                        }
                        composable("agregar"){
                            AgregarVehiculoScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
