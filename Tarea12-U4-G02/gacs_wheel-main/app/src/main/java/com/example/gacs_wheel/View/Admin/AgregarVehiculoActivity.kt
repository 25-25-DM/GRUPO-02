package com.example.gacs_wheel.View

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.gacs_wheel.Controller.VehiculoController
import com.example.gacs_wheel.Model.Vehiculo
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*



@Composable
fun AgregarVehiculoScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // IMPORTANTE: Remover credenciales en producción!
    val awsAccessKey = ""
    val awsSecretKey = ""
    val awsSessionToken = ""

    var placa by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var costoPorDia by remember { mutableStateOf("") }
    var activo by remember { mutableStateOf(false) }
    var imagenSeleccionada by remember { mutableStateOf<String?>(null) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    var hasReadImagesPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val (photoFile, photoUri) = rememberImageFile(context)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasReadImagesPermission =
                permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: hasReadImagesPermission
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imagenSeleccionada = photoUri.toString()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imagenSeleccionada = it.toString() }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotificationPermission = granted
    }


    val defaultRes = context.resources.getIdentifier("carro1", "drawable", context.packageName)
    val imagePainter = rememberAsyncImagePainter(
        model = when {
            imagenSeleccionada == null -> defaultRes
            imagenSeleccionada!!.startsWith("http") -> imagenSeleccionada
            else -> Uri.parse(imagenSeleccionada)
        }
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
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
            onValueChange = { if (it.all(Char::isDigit)) anio = it },
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
            onValueChange = { if (it.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) costoPorDia = it }, // Regex corregido
            label = { Text("Costo por día") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = activo, onCheckedChange = { activo = it })
            Text("¿Activo?")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Image(
                painter = imagePainter,
                contentDescription = "Imagen del vehículo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (!hasCameraPermission || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasReadImagesPermission)) {
                    val perms = mutableListOf<String>()
                    if (!hasCameraPermission) perms.add(Manifest.permission.CAMERA)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasReadImagesPermission)
                        perms.add(Manifest.permission.READ_MEDIA_IMAGES)
                    permissionLauncher.launch(perms.toTypedArray())
                } else {
                    cameraLauncher.launch(photoUri)
                }
            }) { Text("Tomar Foto") }

            Button(onClick = { galleryLauncher.launch("image/*") }) { Text("Elegir de Galería") }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val anioInt = anio.toIntOrNull()
                val costoDouble = costoPorDia.toDoubleOrNull()

                if (placa.isBlank() || marca.isBlank() || anioInt == null || color.isBlank() || costoDouble == null) {
                    Toast.makeText(context, "Completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }

                    scope.launch {
                        val nuevoVehiculo = Vehiculo(
                            placa = placa,
                            marca = marca,
                            anio = anioInt,
                            color = color,
                            costoPorDia = costoDouble,
                            activo = activo,
                            imagen = imagenSeleccionada ?: "",
                            usuarioId = 3
                        )

                        val exito = VehiculoController.insertarVehiculo(
                            context,
                            nuevoVehiculo,
                            awsAccessKey,
                            awsSecretKey,
                            awsSessionToken
                        )

                        if (exito) {
                            Toast.makeText(context, "Vehículo agregado con éxito", Toast.LENGTH_SHORT).show()
                            // ✅ Mostrar notificación de éxito
                            mostrarNotificacion(context, "Éxito", "Vehículo agregado con éxito")
                            // Navegar directamente a inicio y limpiar pila
                            navController.navigate("inicio") {
                                popUpTo("agregar") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Ya existe un vehículo con esta placa", Toast.LENGTH_SHORT).show()
                            // ❌ Mostrar notificación de error
                            mostrarNotificacion(context, "Error", "Ya existe un vehículo con esta placa")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            navController.navigate("inicio") {
                popUpTo("agregar") { inclusive = true }
            }
        }) {
            Text("Cancelar")
        }
    }
}

@Composable
fun rememberImageFile(context: Context): Pair<File, Uri> {
    val timeStamp = remember {
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }
    val imageFile = remember(timeStamp) {
        File.createTempFile("JPEG_${timeStamp}_", ".jpg", context.cacheDir)
    }
    val uri = remember(imageFile) {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
    }
    return Pair(imageFile, uri)
}

fun mostrarNotificacion(context: Context, titulo: String, mensaje: String) {
    val channelId = "canal_gacs"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nombre = "Notificaciones GACS"
        val descripcion = "Canal para mensajes importantes"
        val importancia = NotificationManager.IMPORTANCE_HIGH  // <-- Cambiado a HIGH
        val channel = NotificationChannel(channelId, nombre, importancia).apply {
            description = descripcion
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(titulo)
        .setContentText(mensaje)
        .setPriority(NotificationCompat.PRIORITY_HIGH)  // <-- Cambiado a HIGH
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        .setAutoCancel(true) // Se cierra cuando el usuario toca la notificación

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }
}
