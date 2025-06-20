/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.marsphotos

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.marsphotos.ui.MarsPhotosApp
import com.example.marsphotos.ui.theme.MarsPhotosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // Lo mantienes de tu original
        super.onCreate(savedInstanceState)
        setContent {
            // AppNavigation se encargará de mostrar Login o MarsPhotosApp
            // ya no es necesario MarsPhotosTheme aquí directamente si AppNavigation lo maneja.
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    var showLoginScreen by remember { mutableStateOf(true) }

    if (showLoginScreen) {
        // Mostramos LoginScreen, envuelta en tu tema para consistencia
        MarsPhotosTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background // Usar el fondo del tema
            ) {
                LoginScreen(
                    onLoginSuccess = {
                        showLoginScreen = false // Cambia el estado para mostrar MarsPhotosApp
                        Log.d("AppNavigation", "Login Succeeded. Navigating to MarsPhotosApp.")
                    }
                )
            }
        }
    } else {
        // Mostramos tu MarsPhotosApp original, tal como estaba en tu setContent anterior.
        MarsPhotosTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                // El color ya lo toma del MaterialTheme dentro de MarsPhotosTheme
            ) {
                MarsPhotosApp() // Aquí se llama a tu app principal de Mars
                Log.d("AppNavigation", "Displaying MarsPhotosApp.")
            }
        }
    }
}

// --- LoginScreen Composable ---
// Puedes mover esto a su propio archivo (ej: LoginScreen.kt) y luego importarlo,
// o dejarlo aquí si prefieres.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            // Considera agregar: visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Aquí podrías agregar lógica de validación real si es necesario
                // Por ahora, solo navegamos
                Log.d("LoginScreen", "Ingresar button clicked")
                onLoginSuccess()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }
    }
}

