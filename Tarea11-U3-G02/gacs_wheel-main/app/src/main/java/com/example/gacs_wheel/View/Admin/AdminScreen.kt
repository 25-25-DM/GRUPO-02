package com.example.gacs_wheel.View.Admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gacs_wheel.View.SessionViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.ui.Alignment

import androidx.compose.ui.unit.dp



@Composable
fun AdminScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel = viewModel()
) {
    val usuario = sessionViewModel.usuarioLogueado.collectAsState().value

    // Si no hay sesión, redirige a login
    if (usuario == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("admin") { inclusive = true }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido Admin: ${usuario.nombre}",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))


        Button(
            onClick = {
                sessionViewModel.cerrarSesion()
                navController.navigate("login") {
                    popUpTo("admin") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
