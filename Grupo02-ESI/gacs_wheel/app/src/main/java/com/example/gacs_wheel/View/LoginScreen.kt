package com.example.gacs_wheel.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gacs_wheel.Controller.LoginController
import com.example.gacs_wheel.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest // Necesario para tu función Hash

// Tu función para generar el hash MD5
fun generateMD5Hash(input: String): String {
    return try {
        val md = MessageDigest.getInstance("MD5")
        val hashBytes = md.digest(input.toByteArray())
        val hexString = StringBuilder()
        for (byte in hashBytes) {
            val hex = Integer.toHexString(0xff and byte.toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        hexString.toString()
    } catch (e: Exception) {
        e.printStackTrace() // Es buena idea loguear la excepción de forma más estructurada
        "" // Devuelve una cadena vacía en caso de error
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") } // Asumiendo que tienes un campo de email
    var error by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var processingMessage by remember { mutableStateOf<String?>(null) } // Para el mensaje de "Apellido + Hash"

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... (resto del Box con la imagen, sin cambios)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(Color(0xFF201B3B)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.gacswheels),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier
                    .size(350.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Iniciar Sesión",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFA3E11),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = apellido,
                onValueChange = {
                    apellido = it
                    // Opcional: limpiar el mensaje de procesamiento si el apellido cambia
                    // y aún no se ha presionado ingresar.
                    if (isLoading) processingMessage = null
                },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                if (isLoading) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFFFA3E11))
                        Spacer(modifier = Modifier.height(8.dp))
                        if (processingMessage != null) {
                            Text(
                                processingMessage!!, // Mostramos el mensaje con apellido y hash
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        } else {
                            Text( // Mensaje genérico mientras se genera el hash inicialmente
                                "Procesando...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFA3E11),
                            contentColor = Color.White
                        ),
                        onClick = {
                            if (nombre.isBlank() || apellido.isBlank()) {
                                error = "Nombre y Apellido no pueden estar vacíos."
                                return@Button
                            }
                            if (apellido.isBlank()) { // Específicamente para el hash
                                error = "El apellido no puede estar vacío para generar el hash."
                                return@Button
                            }
                            error = null
                            isLoading = true
                            processingMessage = null // Limpiar mensaje previo

                            // Generar el hash y construir el mensaje
                            val apellidoHash = generateMD5Hash(apellido)
                            processingMessage = "Apellido: $apellido\nHash: $apellidoHash"

                            coroutineScope.launch {
                                delay(5000) // Espera 5 segundos

                                if (LoginController.autenticar(nombre, apellido)) {
                                    navController.navigate("inicio") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    error = "Credenciales inválidas. Intenta de nuevo."
                                }
                                isLoading = false
                                processingMessage = null // Limpiar el mensaje después del proceso
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ingresar")
                    }
                }
            }

            if (error != null && !isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (!isLoading) {
                TextButton(onClick = { navController.navigate("registro") }) {
                    Text(
                        "¿No tienes cuenta? Regístrate",
                        color = Color(0xFFFA3E11)
                    )
                }
            }
        }
    }
}