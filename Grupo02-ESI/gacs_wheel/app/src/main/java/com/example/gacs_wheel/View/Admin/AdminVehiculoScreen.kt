package com.example.gacs_wheel.View

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gacs_wheel.Controller.VehiculoController
import com.example.gacs_wheel.Model.Vehiculo

@Composable
fun AdminVehiculoScreen(navController: NavController) {
    val context = LocalContext.current
    val vehiculos = VehiculoController.obtenerTodos()
    var placaSeleccionada by remember { mutableStateOf("") }
    var vehiculoActual by remember { mutableStateOf<Vehiculo?>(null) }

    // Campos editables
    var marca by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var costoPorDia by remember { mutableStateOf("") }
    var activo by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Administrador de Vehículos", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = placaSeleccionada,
            onValueChange = { value ->
                placaSeleccionada = value
                val encontrado = VehiculoController.obtenerPorPlaca(value)
                vehiculoActual = encontrado

                if (encontrado != null) {
                    marca = encontrado.marca
                    anio = encontrado.anio.toString()
                    color = encontrado.color
                    costoPorDia = encontrado.costoPorDia.toString()
                    activo = encontrado.activo
                }
            },
            label = { Text("Buscar por placa") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (vehiculoActual != null) {
            Image(
                painter = painterResource(id = vehiculoActual!!.imagenResId),
                contentDescription = "Imagen vehículo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = marca,
                onValueChange = { marca = it },
                label = { Text("Marca") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = anio,
                onValueChange = { anio = it },
                label = { Text("Año") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = costoPorDia,
                onValueChange = { costoPorDia = it },
                label = { Text("Costo por día") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = activo, onCheckedChange = { activo = it })
                Text("Disponible")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        try {
                            val vehiculoEditado = Vehiculo(
                                placa = vehiculoActual!!.placa,
                                marca = marca,
                                anio = anio.toInt(),
                                color = color,
                                costoPorDia = costoPorDia.toDouble(),
                                activo = activo,
                                imagenResId = vehiculoActual!!.imagenResId
                            )
                            VehiculoController.editarVehiculo(vehiculoEditado)
                            Toast.makeText(context, "Vehículo modificado", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error al modificar", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Guardar cambios")
                }

                Button(
                    onClick = {
                        VehiculoController.eliminarVehiculo(vehiculoActual!!.placa)
                        vehiculoActual = null
                        placaSeleccionada = ""
                        Toast.makeText(context, "Vehículo eliminado", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                navController.navigate("agregar")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar nuevo vehículo")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                navController.navigate("inicio") {
                    popUpTo("admin") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar")
        }
    }
}