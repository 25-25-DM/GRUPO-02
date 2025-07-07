package com.example.gacs_wheel.Controller

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.example.gacs_wheel.Model.GascDataBase
import com.example.gacs_wheel.Model.Usuario
import com.example.gacs_wheel.Model.UsuarioDynamo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong

object RegistroController {
    // Generador de IDs mejorado para evitar colisiones
    private val idGenerator = AtomicLong(System.currentTimeMillis() * 1000)

    suspend fun registrar(
        context: Context,
        nombre: String,
        passwordRaw: String,
        rol: String = "usuario",
        awsAccessKey: String? = null,
        awsSecretKey: String? = null,
        awsSessionToken: String? = null
    ): Boolean {
        val db = GascDataBase.getDatabase(context)
        val dao = db.usuarioDao()

        // Verificar si el usuario ya existe
        if (dao.getUsuarioByNombre(nombre) != null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "El usuario '$nombre' ya existe", Toast.LENGTH_SHORT).show()
            }
            return false
        }

        val passwordHash = HashUtil.md5(passwordRaw)
        val nuevoUsuario = Usuario(nombre = nombre, password = passwordHash, rol = rol)

        // Insertar localmente
        val localId = dao.insertUsuario(nuevoUsuario)
        val localIdInt = localId.toInt()

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Usuario guardado localmente (ID=$localId)", Toast.LENGTH_SHORT).show()
        }

        // Sincronizar con DynamoDB si hay credenciales
        if (awsAccessKey != null && awsSecretKey != null && awsSessionToken != null) {
            withContext(Dispatchers.IO) {
                try {
                    val mapper = AwsClientProvider.provideDynamoDBMapper(
                        awsAccessKey, awsSecretKey, awsSessionToken
                    )

                    // Generar ID único de 13 dígitos
                    val dynamoId = idGenerator.getAndIncrement()

                    // Crear item para DynamoDB con ID explícito
                    val dynamoUser = UsuarioDynamo().apply {
                        id = dynamoId  // Asignación EXPLÍCITA del ID
                        this.nombre = nombre
                        this.password = passwordHash
                        this.rol = rol
                    }

                    // DEBUG: Verificar antes de guardar
                    Log.d("SYNC_DEBUG", "Intentando guardar en Dynamo: ID=$dynamoId, Nombre=$nombre")

                    // Guardar en DynamoDB
                    mapper.save(dynamoUser)

                    // DEBUG: Verificar después de guardar
                    val savedUser = mapper.load(UsuarioDynamo::class.java, dynamoId)
                    if (savedUser == null) {
                        Log.e("SYNC_ERROR", "No se pudo recuperar el usuario recién guardado")
                    } else {
                        Log.d("SYNC_DEBUG", "Usuario guardado correctamente: ID=${savedUser.id}, Nombre=${savedUser.nombre}")
                    }

                    // Actualizar usuario local con dynamoId
                    dao.actualizarDynamoId(localIdInt, dynamoId)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Sincronizado en nube (ID Dynamo: $dynamoId)",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    return@withContext true
                } catch (e: Exception) {
                    Log.e("SYNC_ERROR", "Error al sincronizar usuario", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Error sincronizando: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@withContext false
                }
            }
        }
        return true
    }

}