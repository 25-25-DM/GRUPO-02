package com.example.gacs_wheel.Controller

import android.content.Context
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.example.gacs_wheel.Model.GascDataBase
import com.example.gacs_wheel.Model.Vehiculo
import com.example.gacs_wheel.Model.VehiculoDynamo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicLong

object VehiculoController {
    private val idGenerator = AtomicLong(System.currentTimeMillis() * 1000)

    // Flow para notificar cambios en los datos
    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger: StateFlow<Int> = _refreshTrigger

    suspend fun insertarVehiculo(
        context: Context,
        vehiculo: Vehiculo,
        awsAccessKey: String? = null,
        awsSecretKey: String? = null,
        awsSessionToken: String? = null,
        bucketName: String = "mi-app-vehiculos-imagenes"
    ): Boolean {
        val dao = GascDataBase.getDatabase(context).vehiculoDao()

        if (dao.buscarPorPlaca(vehiculo.placa) != null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ya existe un vehículo con placa ${vehiculo.placa}", Toast.LENGTH_SHORT).show()
            }
            return false
        }

        // Guardar localmente primero sin la imagen en la nube
        val localId = dao.insertarVehiculo(vehiculo).toInt()

        if (awsAccessKey == null || awsSecretKey == null || awsSessionToken == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Vehículo guardado localmente (ID=$localId)", Toast.LENGTH_SHORT).show()
            }
            _refreshTrigger.value++ // Notificar cambio
            return true
        }

        return withContext(Dispatchers.IO) {
            try {
                val s3 = AwsClientProvider.provideS3Client(awsAccessKey, awsSecretKey, awsSessionToken)
                val mapper = AwsClientProvider.provideDynamoDBMapper(awsAccessKey, awsSecretKey, awsSessionToken)

                var imageUrl = vehiculo.imagen

                // Subir imagen solo si no es una URL HTTP(S)
                if (!imageUrl.startsWith("http")) {
                    val uri = Uri.parse(imageUrl)
                    val inputStream = retryUntilNotNull(maxAttempts = 5, delayMillis = 500) {
                        try {
                            context.contentResolver.openInputStream(uri)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: throw Exception("No se pudo acceder a la imagen para subirla")

                    val bytes = inputStream.use { it.readBytes() }

                    // Generar nombre único con UUID
                    val key = "vehiculos/${UUID.randomUUID()}.jpg"

                    val metadata = ObjectMetadata().apply {
                        contentType = "image/jpeg"
                        contentLength = bytes.size.toLong()
                    }

                    // Subir a S3 SIN ACL (porque el bucket no los permite)
                    s3.putObject(
                        PutObjectRequest(bucketName, key, bytes.inputStream(), metadata)
                    )

                    // Generar URL pública correcta
                    imageUrl = "https://${bucketName}.s3.amazonaws.com/$key"

                    // Actualizar la imagen en la BD local
                    dao.actualizarImagen(localId, imageUrl)
                }

                // Guardar en DynamoDB
                val dynamoId = idGenerator.getAndIncrement()

                val item = VehiculoDynamo().apply {
                    id = dynamoId
                    placa = vehiculo.placa
                    marca = vehiculo.marca
                    anio = vehiculo.anio
                    color = vehiculo.color
                    costoPorDia = vehiculo.costoPorDia
                    activo = vehiculo.activo
                    imagen = imageUrl
                    usuarioId = vehiculo.usuarioId.toLong()
                }
                mapper.save(item)

                dao.actualizarDynamoId(localId, dynamoId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Sincronizado en nube (ID Dynamo: $dynamoId)", Toast.LENGTH_LONG).show()
                }

                _refreshTrigger.value++ // Notificar cambio
                true

            } catch (e: Exception) {
                Log.e("SYNC_ERROR", "Error sincronizando vehículo", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error sincronizando: ${e.message}", Toast.LENGTH_LONG).show()
                }
                false
            }
        }
    }

    private inline fun <T> retryUntilNotNull(
        maxAttempts: Int = 3,
        delayMillis: Long = 300,
        block: () -> T?
    ): T? {
        repeat(maxAttempts - 1) {
            block()?.let { return it }
            SystemClock.sleep(delayMillis)
        }
        return block()
    }

    suspend fun obtenerTodos(context: Context): List<Vehiculo> =
        GascDataBase.getDatabase(context).vehiculoDao().obtenerTodosLosVehiculos()

    suspend fun obtenerPorPlaca(context: Context, placa: String): Vehiculo? =
        GascDataBase.getDatabase(context).vehiculoDao().buscarPorPlaca(placa)

    suspend fun editarVehiculo(
        context: Context,
        vehiculo: Vehiculo,
        awsAccessKey: String? = null,
        awsSecretKey: String? = null,
        awsSessionToken: String? = null,
        bucketName: String = "mi-app-vehiculos-imagenes"
    ): Boolean {
        val dao = GascDataBase.getDatabase(context).vehiculoDao()
        val rows = dao.actualizarVehiculo(vehiculo)
        if (rows <= 0) return false

        if (awsAccessKey != null && awsSecretKey != null && awsSessionToken != null) {
            withContext(Dispatchers.IO) {
                try {
                    val s3 = AwsClientProvider.provideS3Client(awsAccessKey, awsSecretKey, awsSessionToken)
                    val mapper = AwsClientProvider.provideDynamoDBMapper(awsAccessKey, awsSecretKey, awsSessionToken)
                    val dynamoId = dao.obtenerDynamoId(vehiculo.id)
                    if (dynamoId != null) {
                        var imageUrl = vehiculo.imagen

                        // Si la imagen no es URL http(s), se sube a S3
                        if (!imageUrl.startsWith("http")) {
                            // ... (código para subir imagen) ...
                        }

                        // Guardar actualización en DynamoDB
                        val updateItem = VehiculoDynamo().apply {
                            id = dynamoId
                            placa = vehiculo.placa
                            marca = vehiculo.marca
                            anio = vehiculo.anio
                            color = vehiculo.color
                            costoPorDia = vehiculo.costoPorDia
                            activo = vehiculo.activo
                            imagen = imageUrl
                            usuarioId = vehiculo.usuarioId.toLong()
                        }
                        mapper.save(updateItem)
                    } else {
                        // CORRECCIÓN: Usar vehiculo.imagen en lugar de imageUrl
                        val newDynamoId = idGenerator.getAndIncrement()
                        val newItem = VehiculoDynamo().apply {
                            id = newDynamoId
                            placa = vehiculo.placa
                            marca = vehiculo.marca
                            anio = vehiculo.anio
                            color = vehiculo.color
                            costoPorDia = vehiculo.costoPorDia
                            activo = vehiculo.activo
                            imagen = vehiculo.imagen // <--- Aquí está la corrección
                            usuarioId = vehiculo.usuarioId.toLong()
                        }
                        mapper.save(newItem)
                        dao.actualizarDynamoId(vehiculo.id, newDynamoId)
                    }
                    _refreshTrigger.value++
                } catch (e: Exception) {
                    Log.e("SYNC_ERROR", "Error al actualizar vehículo en nube", e)
                }
            }
        } else {
            _refreshTrigger.value++
        }
        return true
    }

    suspend fun eliminarVehiculo(
        context: Context,
        placa: String,
        awsAccessKey: String? = null,
        awsSecretKey: String? = null,
        awsSessionToken: String? = null
    ) {
        val dao = GascDataBase.getDatabase(context).vehiculoDao()
        val vehiculo = dao.buscarPorPlaca(placa) ?: return

        dao.eliminarVehiculo(vehiculo)

        if (awsAccessKey != null && awsSecretKey != null && awsSessionToken != null) {
            withContext(Dispatchers.IO) {
                try {
                    val mapper = AwsClientProvider.provideDynamoDBMapper(
                        awsAccessKey, awsSecretKey, awsSessionToken
                    )
                    val dynamoId = dao.obtenerDynamoId(vehiculo.id)
                    if (dynamoId != null) {
                        mapper.delete(VehiculoDynamo().apply { id = dynamoId })
                        Log.d("SYNC_DEBUG", "Vehículo eliminado en DynamoDB: ID=$dynamoId")
                    } else {
                        // Intentar eliminar por placa si no hay dynamoId
                        val item = mapper.load(VehiculoDynamo::class.java, vehiculo.placa)
                        item?.let { mapper.delete(it) }
                    }
                } catch (e: Exception) {
                    Log.e("SYNC_ERROR", "Error al eliminar vehículo en nube", e)
                }
            }
        }
        _refreshTrigger.value++ // Notificar cambio
    }
}