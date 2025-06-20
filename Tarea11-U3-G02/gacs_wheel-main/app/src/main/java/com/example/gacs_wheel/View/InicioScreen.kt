package com.example.gacs_wheel.View

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gacs_wheel.Controller.VehiculoController
import com.example.gacs_wheel.Model.Vehiculo
import kotlinx.coroutines.launch

@Composable
fun InicioScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var vehiculos by remember { mutableStateOf<List<Vehiculo>>(emptyList()) }

    // Cargar vehículos al iniciar
    LaunchedEffect(Unit) {
        vehiculos = VehiculoController.obtenerTodos(context)

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Barra superior con botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(250, 62, 17),
                    contentColor = Color.White
                ),
                onClick = {
                    navController.navigate("login") {
                        popUpTo("inicio") { inclusive = true }
                    }
                }
            ) {
                Text("Cerrar sesión")
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(250, 62, 17),
                    contentColor = Color.White
                ),
                onClick = {
                    navController.navigate("admin")
                }
            ) {
                Text("Admin")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de vehículos
        if (vehiculos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay vehículos disponibles")
            }
        } else {
            LazyColumn {
                items(vehiculos) { vehiculo ->
                    VehiculoCard(vehiculo = vehiculo)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun VehiculoCard(vehiculo: Vehiculo) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF201B3B)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Imagen del vehículo
            val imagenId = remember(vehiculo.imagen) {
                context.resources.getIdentifier(vehiculo.imagen, "drawable", context.packageName)
            }

            if (imagenId != 0) {
                Image(
                    painter = painterResource(id = imagenId),
                    contentDescription = "Imagen del vehículo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detalles del vehículo
            val textColorOnDark = Color.White
            Text("Placa: ${vehiculo.placa}", color = textColorOnDark)
            Text("Marca: ${vehiculo.marca}", color = textColorOnDark)
            Text("Año: ${vehiculo.anio}", color = textColorOnDark.copy(alpha = 0.85f))
            Text("Color: ${vehiculo.color}", color = textColorOnDark.copy(alpha = 0.85f))
            Text("Costo/día: $${vehiculo.costoPorDia}", color = Color(0xFFBB86FC))
            Text(
                "Estado: ${if (vehiculo.activo) "Disponible" else "No disponible"}",
                color = if (vehiculo.activo) Color(0xFF66BB6A) else Color(0xFFEF5350)
            )

            // Botón para alquilar (opcional)
            if (vehiculo.activo) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                // Lógica para alquilar vehículo
                                Toast.makeText(context, "Vehículo alquilado", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFBB86FC),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Alquilar ahora")
                }
            }
        }
    }
}