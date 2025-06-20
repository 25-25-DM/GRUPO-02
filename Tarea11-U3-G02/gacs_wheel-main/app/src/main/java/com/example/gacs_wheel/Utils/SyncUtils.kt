package com.example.gacs_wheel.Utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.example.gacs_wheel.Controller.AwsClientProvider
import com.example.gacs_wheel.Model.GascDataBase
import com.example.gacs_wheel.Model.Usuario
import com.example.gacs_wheel.Model.UsuarioDynamo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

suspend fun sincronizarUsuariosBidireccionalCompleto(context: Context) {
    withContext(Dispatchers.IO) {
        val db = GascDataBase.getDatabase(context)
        val usuarioDao = db.usuarioDao()
        var localUsuarios = usuarioDao.getAllUsuariosDirect()

        try {
            val mapper = AwsClientProvider.provideDynamoDBMapper(
                "a",
                "a",
                "a"
            )

            // 1. Obtener TODOS los usuarios de la nube con paginación
            val usuariosNube = mutableListOf<UsuarioDynamo>()
            var lastEvaluatedKey: Map<String, AttributeValue>? = null
            do {
                val scanExpression = DynamoDBScanExpression().apply {
                    lastEvaluatedKey?.let { exclusiveStartKey = it }
                }
                val result = mapper.scanPage(UsuarioDynamo::class.java, scanExpression)
                usuariosNube.addAll(result.results)
                lastEvaluatedKey = result.lastEvaluatedKey
            } while (lastEvaluatedKey != null && lastEvaluatedKey.isNotEmpty())

            val mapaNube = usuariosNube.associateBy { it.id }

            // 2. Verificar usuarios locales contra la nube
            val usuariosParaActualizar = mutableListOf<Usuario>()
            val usuariosParaCrearEnNube = mutableListOf<Usuario>()
            val idsInexistentes = mutableListOf<Long>()

            for (usuarioLocal in localUsuarios) {
                if (usuarioLocal.dynamoId != null && usuarioLocal.dynamoId != 0L) {
                    val usuarioNube = mapaNube[usuarioLocal.dynamoId]

                    if (usuarioNube == null) {
                        // Usuario fue eliminado en la nube
                        idsInexistentes.add(usuarioLocal.dynamoId!!)
                    } else {
                        // Verificar si necesita actualización
                        if (usuarioLocal.nombre != usuarioNube.nombre ||
                            usuarioLocal.password != usuarioNube.password ||
                            usuarioLocal.rol != usuarioNube.rol) {

                            // Preferencia: cambios locales sobre la nube
                            usuariosParaActualizar.add(usuarioLocal)
                        }
                    }
                } else {
                    // Usuario nuevo (sin dynamoId)
                    usuariosParaCrearEnNube.add(usuarioLocal)
                }
            }

            // 3. Resetear dynamoId para usuarios eliminados en la nube
            if (idsInexistentes.isNotEmpty()) {
                usuarioDao.resetDynamoIds(idsInexistentes)
                Log.d("SYNC", "Reseteados ${idsInexistentes.size} IDs de usuarios eliminados en la nube")
                // Recargar usuarios locales después del cambio
                localUsuarios = usuarioDao.getAllUsuariosDirect()
            }

            // 4. Crear nuevos usuarios locales en la nube
            for (usuarioLocal in usuariosParaCrearEnNube) {
                try {
                    val dynamoId = generarIdUnico()

                    val usuarioNube = UsuarioDynamo().apply {
                        id = dynamoId
                        nombre = usuarioLocal.nombre
                        password = usuarioLocal.password
                        rol = usuarioLocal.rol
                    }

                    mapper.save(usuarioNube)
                    usuarioDao.actualizarDynamoId(usuarioLocal.id, dynamoId)
                    Log.d("SYNC", "Creado en nube: ${usuarioLocal.nombre} (ID: $dynamoId)")
                } catch (e: Exception) {
                    Log.e("SYNC", "Error al crear en nube: ${usuarioLocal.nombre}", e)
                }
            }

            // 5. Actualizar usuarios modificados localmente
            for (usuarioLocal in usuariosParaActualizar) {
                try {
                    val usuarioNube = UsuarioDynamo().apply {
                        id = usuarioLocal.dynamoId!!
                        nombre = usuarioLocal.nombre
                        password = usuarioLocal.password
                        rol = usuarioLocal.rol
                    }

                    mapper.save(usuarioNube)
                    Log.d("SYNC", "Actualizado en nube: ${usuarioLocal.nombre} (ID: ${usuarioLocal.dynamoId})")
                } catch (e: Exception) {
                    Log.e("SYNC", "Error al actualizar en nube: ${usuarioLocal.nombre}", e)
                }
            }

            // 6. Crear en local usuarios nuevos de la nube
            var nuevosEnLocal = 0
            for (usuarioNube in usuariosNube) {
                val existeEnLocal = localUsuarios.any { it.dynamoId == usuarioNube.id }

                if (!existeEnLocal) {
                    try {
                        // Verificar si el usuario ya existe localmente por nombre
                        val usuarioExistente = usuarioDao.getUsuarioByNombre(usuarioNube.nombre)

                        if (usuarioExistente == null) {
                            // Usuario completamente nuevo
                            val nuevoUsuario = Usuario(
                                nombre = usuarioNube.nombre,
                                password = usuarioNube.password,
                                rol = usuarioNube.rol,
                                dynamoId = usuarioNube.id
                            )
                            usuarioDao.insert(nuevoUsuario)
                            nuevosEnLocal++
                            Log.d("SYNC", "Creado en local: ${usuarioNube.nombre} (ID: ${usuarioNube.id})")
                        } else {
                            // Usuario existe localmente pero sin dynamoId (resincronización)
                            if (usuarioExistente.dynamoId == null || usuarioExistente.dynamoId == 0L) {
                                usuarioDao.actualizarDynamoId(usuarioExistente.id, usuarioNube.id)
                                Log.d("SYNC", "Asociado usuario existente: ${usuarioNube.nombre} (ID: ${usuarioNube.id})")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("SYNC", "Error al crear en local: ${usuarioNube.nombre}", e)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                val mensaje = buildString {
                    append("Sincronización completada\n")
                    append("Nuevos en nube: ${usuariosParaCrearEnNube.size}\n")
                    append("Actualizados: ${usuariosParaActualizar.size}\n")
                    append("Recuperados: $nuevosEnLocal")
                }

                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("SYNC", "Error en sincronización", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al sincronizar: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

private fun generarIdUnico(): Long {
    return System.currentTimeMillis() + Random.nextLong(1000)
}