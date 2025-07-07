package com.example.gacs_wheel.View

import android.widget.Toast
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gacs_wheel.Controller.LoginController
import com.example.gacs_wheel.Controller.RegistroController
import com.example.gacs_wheel.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
/*
    LaunchedEffect(Unit) {
        // Llama a crearAdminSiNoExiste pasando tus credenciales AWS Academy
        RegistroController.crearAdminSiNoExiste(
            context,
            awsAccessKey = "a",
            awsSecretKey = "a",
            awsSessionToken = "a"
        )
    }
*/
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
                .fillMaxHeight()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Iniciar Sesión",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(250, 62, 17),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(250, 62, 17),
                    contentColor = Color.White
                ),
                onClick = {
                    scope.launch {
                        val usuario = LoginController.autenticarYObtenerUsuario(context, nombre, password)
                        if (usuario != null) {
                            // Usuario autenticado: navegar a InicioScreen
                            navController.navigate("inicio") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            error = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            if (error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Credenciales inválidas", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = { navController.navigate("registro") }) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}
