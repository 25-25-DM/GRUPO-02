package com.example.gacs_wheel.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gacs_wheel.Controller.VehiculoController
import com.example.gacs_wheel.Model.Vehiculo
import com.example.gacs_wheel.R

@Composable
fun InicioScreen(navController: NavController) {
    val vehiculos = VehiculoController.obtenerTodos()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(250,62,17),
                    contentColor = Color.White
                ),onClick = {
                    navController.navigate("login") {
                        popUpTo("inicio") { inclusive = true }
                    }
                }) {
                Text("Cerrar sesión")
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(250,62,17),
                    contentColor = Color.White
                ),onClick = {
                    navController.navigate("admin")
                }) {
                Text("Admin")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(vehiculos.size) { index ->
                VehiculoCard(vehiculo = vehiculos[index])
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun VehiculoCard(vehiculo: Vehiculo) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF201B3B)

        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Usa un placeholder si imagenResId es nulo para evitar crashes
            val imageRes = vehiculo.imagenResId

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Imagen del vehículo ${vehiculo.marca ?: ""}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            val textColorOnDark = Color.White

            Text("Placa: ${vehiculo.placa ?: "N/A"}", color = textColorOnDark)
            Text("Marca: ${vehiculo.marca ?: "N/A"}", color = textColorOnDark)
            Text("Año: ${vehiculo.anio ?: "N/A"}", color = textColorOnDark.copy(alpha = 0.85f))
            Text("Color: ${vehiculo.color ?: "N/A"}", color = textColorOnDark.copy(alpha = 0.85f))
            Text("Costo/día: $${vehiculo.costoPorDia ?: "N/A"}", color = Color(0xFFBB86FC))
            Text(
                "Estado: ${if (vehiculo.activo) "Disponible" else "No disponible"}",
                color = if (vehiculo.activo) Color(0xFF66BB6A) else Color(0xFFEF5350)
            )
            }
        }
}