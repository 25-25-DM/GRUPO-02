package com.example.inventory.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.inventory.ui.Controller.LoginController
import kotlinx.coroutines.launch

@Composable
fun RegistroScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (nombre.isBlank() || apellido.isBlank()) {
                    mensaje = "Nombre y Apellido no pueden estar vacíos."
                    return@Button
                }

                isLoading = true
                mensaje = ""

                coroutineScope.launch {
                    val dao = com.example.inventory.data.InventoryDatabase
                        .getDatabase(context)
                        .usuarioDao()

                    val usuarioExistente = dao.getUsuarioByNombreApellido(nombre, apellido)

                    if (usuarioExistente != null) {
                        mensaje = "El usuario ya existe"
                    } else {
                        LoginController.registrarUsuario(context, nombre, apellido)
                        mensaje = "Usuario registrado con éxito"
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("registro") { inclusive = true }
                        }
                    }
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Registrando..." else "Registrarse")
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(mensaje, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}
