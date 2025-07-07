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
import kotlinx.coroutines.launch

@Composable
fun AdminVehiculoScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var vehiculos by remember { mutableStateOf<List<Vehiculo>>(emptyList()) }

    var placaSeleccionada by remember { mutableStateOf("") }
    var vehiculoActual by remember { mutableStateOf<Vehiculo?>(null) }

    var marca by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var costoPorDia by remember { mutableStateOf("") }
    var activo by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        vehiculos = VehiculoController.obtenerTodos(context)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Administrador de Vehículos", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = placaSeleccionada,
            onValueChange = { value ->
                placaSeleccionada = value
                scope.launch {
                    val encontrado = VehiculoController.obtenerPorPlaca(context, value)
                    vehiculoActual = encontrado

                    if (encontrado != null) {
                        marca = encontrado.marca
                        anio = encontrado.anio.toString()
                        color = encontrado.color
                        costoPorDia = encontrado.costoPorDia.toString()
                        activo = encontrado.activo
                    } else {
                        marca = ""
                        anio = ""
                        color = ""
                        costoPorDia = ""
                        activo = true
                    }
                }
            },
            label = { Text("Buscar por placa") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        vehiculoActual?.let { vehiculo ->

            val imagenId = remember(vehiculo.imagen) {
                context.resources.getIdentifier(vehiculo.imagen, "drawable", context.packageName)
            }

            if (imagenId != 0) {
                Image(
                    painter = painterResource(id = imagenId),
                    contentDescription = "Imagen vehículo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

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
                        scope.launch {
                            try {
                                // 1. Verificar que hay un vehículo seleccionado
                                val vehiculoExistente = vehiculoActual ?: run {
                                    Toast.makeText(context, "No hay vehículo seleccionado", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }

                                // 2. Crear copia actualizada (MANTENIENDO EL MISMO ID)
                                val vehiculoEditado = vehiculoExistente.copy(
                                    marca = marca,
                                    anio = anio.toInt(),
                                    color = color,
                                    costoPorDia = costoPorDia.toDouble(),
                                    activo = activo
                                    // No modificar placa, imagen ni usuarioId si no es necesario
                                )

                                // 3. Llamar al Controller para actualizar
                                val exito = VehiculoController.editarVehiculo(context, vehiculoEditado)

                                if (exito) {
                                    // 4. Actualizar el estado
                                    vehiculoActual = vehiculoEditado
                                    Toast.makeText(context, "¡Vehículo actualizado!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: NumberFormatException) {
                                Toast.makeText(context, "Error en formato numérico", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("Guardar cambios")
                }

                Button(
                    onClick = {
                        scope.launch {
                            vehiculoActual?.let {
                                VehiculoController.eliminarVehiculo(context, it.placa)
                                Toast.makeText(context, "Vehículo eliminado", Toast.LENGTH_SHORT).show()
                                vehiculos = VehiculoController.obtenerTodos(context)
                                vehiculoActual = null
                                placaSeleccionada = ""
                            }
                        }
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