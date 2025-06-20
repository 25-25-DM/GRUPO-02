package com.example.gacs_wheel.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gacs_wheel.Controller.VehiculoController
import com.example.gacs_wheel.Model.Vehiculo
import com.example.gacs_wheel.R
import android.widget.Toast
import androidx.compose.ui.window.Dialog

@Composable
fun AgregarVehiculoScreen(navController: NavController) {
    val context = LocalContext.current

    var placa by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var costoPorDia by remember { mutableStateOf("") }
    var activo by remember { mutableStateOf(false) }

    val imagenes = listOf(
        Pair("carro2", R.drawable.carro2),
        Pair("carro3", R.drawable.carro3),
        Pair("carro4", R.drawable.carro4)
    )
    var imagenSeleccionada by remember { mutableStateOf(imagenes[0].second) }
    var mostrarDialogoImagen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Agregar Nuevo Vehículo", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = placa,
            onValueChange = { placa = it },
            label = { Text("Placa") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = marca,
            onValueChange = { marca = it },
            label = { Text("Marca") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = anio,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    anio = newValue
                }
            },
            label = { Text("Año") },
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
            onValueChange = { input ->
                if (input.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                    costoPorDia = input
                }
            },
            label = { Text("Costo por día") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = activo, onCheckedChange = { activo = it })
            Text("¿Activo?")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = imagenSeleccionada),
            contentDescription = "Imagen seleccionada",
            modifier = Modifier
                .size(200.dp)
                .clickable { mostrarDialogoImagen = true }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { mostrarDialogoImagen = true }) {
            Text("Cambiar Imagen")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val anioInt = anio.toIntOrNull()
                val costoDouble = costoPorDia.toDoubleOrNull()

                if (placa.isBlank() || marca.isBlank() || anioInt == null || color.isBlank() || costoDouble == null) {
                    Toast.makeText(context, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    val nuevoVehiculo = Vehiculo(
                        placa = placa,
                        marca = marca,
                        anio = anioInt,
                        color = color,
                        costoPorDia = costoDouble,
                        activo = activo,
                        imagenResId = imagenSeleccionada
                    )
                    val exito = VehiculoController.insertarVehiculo(nuevoVehiculo)
                    if (exito) {
                        Toast.makeText(context, "Vehículo agregado con éxito", Toast.LENGTH_SHORT).show()
                        navController.navigate("admin") {
                            popUpTo("agregar") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Ya existe esa placa", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate("admin") {
                    popUpTo("agregar") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text("Cancelar")
        }
    }

    if (mostrarDialogoImagen) {
        Dialog(onDismissRequest = { mostrarDialogoImagen = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    items(imagenes.size) { index ->
                        val (nombre, resId) = imagenes[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    imagenSeleccionada = resId
                                    mostrarDialogoImagen = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = nombre,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = nombre, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}