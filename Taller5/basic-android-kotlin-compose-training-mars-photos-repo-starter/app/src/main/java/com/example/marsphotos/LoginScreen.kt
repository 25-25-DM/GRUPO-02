package com.example.marsphotos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- INICIO: Agregar el Logo ---
        Image(
            painter = painterResource(id = R.drawable.logo_grupo), // Carga tu imagen
            contentDescription = "Logo del Grupo", // Descripción para accesibilidad
            // Puedes usar stringResource(R.string.logo_content_desc)
            modifier = Modifier
                .size(120.dp) // Ajusta el tamaño según necesites
                .padding(bottom = 24.dp) // Espacio entre el logo y el título
        )
        // --- FIN: Agregar el Logo ---
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
            // Aquí podrías agregar un `visualTransformation = PasswordVisualTransformation()`
            // para ocultar la contraseña. Necesitarías la dependencia:
            // implementation("androidx.compose.ui:ui-text-google-fonts:1.x.x") (o similar)
            // y luego `import androidx.compose.ui.text.input.PasswordVisualTransformation`
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Aquí podrías agregar lógica de validación si es necesario
                android.util.Log.d("LoginScreen", "Ingresar button clicked")
                onLoginSuccess() // ESTA LÍNEA ES FUNDAMENTAL
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Puedes envolverlo con tu tema si tienes uno definido
    // MarsPhotosTheme {
    LoginScreen(onLoginSuccess = {})
    // }
}