package com.example.inventory.ui.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.inventory.R
import com.example.inventory.ui.home.HomeDestination

import com.example.inventory.ui.Controller.LoginController

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest

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
        e.printStackTrace()
        ""
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var processingMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                        Text(
                            processingMessage ?: "Procesando...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
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

                            error = null
                            isLoading = true
                            processingMessage = null

                            val apellidoHash = generateMD5Hash(apellido)
                            processingMessage = "Apellido: $apellido\nHash: $apellidoHash"

                            coroutineScope.launch {
                                //delay(5000)

                                val autenticado = LoginController.autenticar(context, nombre, apellido)
                                if (autenticado) {
                                    navController.navigate(HomeDestination.route) {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    error = "Credenciales inválidas. Intenta de nuevo."
                                }
                                isLoading = false
                                processingMessage = null
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
                    Text("¿No tienes cuenta? Regístrate", color = Color(0xFFFA3E11))
                }
            }
        }
    }
}
