package com.example.gacs_wheel.View

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gacs_wheel.Controller.RegistroController
import kotlinx.coroutines.launch
import android.util.Log

@Composable
fun RegistroScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Función para validar la contraseña en tiempo real
    fun validarContrasena(contrasenia: String): String {
        val mensajesError = mutableListOf<String>()

        if (contrasenia.length < 8) {
            mensajesError.add("• Mínimo 8 caracteres")
        }
        if (!contrasenia.any { it.isUpperCase() }) {
            mensajesError.add("• Al menos 1 mayúscula")
        }
        if (!contrasenia.any { it.isLowerCase() }) {
            mensajesError.add("• Al menos 1 minúscula")
        }
        if (!contrasenia.any { it.isDigit() }) {
            mensajesError.add("• Al menos 1 número")
        }
        if (!contrasenia.matches(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*".toRegex())) {
            mensajesError.add("• Al menos 1 carácter especial")
        }

        return mensajesError.joinToString("\n")
    }

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
            value = password,
            onValueChange = {
                password = it
                passwordError = validarContrasena(it)
            },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError.isNotEmpty()
        )

        // Mostrar pautas de contraseña segura
        Text(
            text = "La contraseña debe contener:",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = if (passwordError.isEmpty()) {
                "• Mínimo 8 caracteres\n• Al menos 1 mayúscula\n• Al menos 1 minúscula\n• Al menos 1 número\n• Al menos 1 carácter especial"
            } else {
                passwordError
            },
            color = if (passwordError.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Validar nuevamente antes de registrar
                passwordError = validarContrasena(password)
                if (passwordError.isNotEmpty()) {
                    mensaje = "La contraseña no cumple los requisitos"
                    return@Button
                }

                mensaje = ""
                coroutineScope.launch {
                    try {
                        val registrado = RegistroController.registrar(
                            context = context,
                            nombre = nombre.trim(),
                            passwordRaw = password,
                            rol = "usuario",
                            awsAccessKey = "a",
                            awsSecretKey = "a",
                            awsSessionToken = "a"
                        )
                        if (registrado) {
                            Toast.makeText(context, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("registro") { inclusive = true }
                            }
                        } else {
                            mensaje = "El usuario ya existe"
                        }
                    } catch (e: Exception) {
                        Log.e("RegistroScreen", "Error en registro", e)
                        mensaje = "Error al registrar: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = passwordError.isEmpty() && nombre.isNotBlank()
        ) {
            Text("Registrarse")
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(mensaje, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}