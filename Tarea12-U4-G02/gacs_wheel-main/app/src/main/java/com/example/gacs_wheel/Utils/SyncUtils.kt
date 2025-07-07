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
import com.example.gacs_wheel.Model.Vehiculo
import com.example.gacs_wheel.Model.VehiculoDynamo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

val clave1 = ""
val clave2 = ""
val clave3 = ""
suspend fun sincronizarUsuariosBidireccionalCompleto(context: Context) {
    withContext(Dispatchers.IO) {
        val db = GascDataBase.getDatabase(context)
        val usuarioDao = db.usuarioDao()
        var localUsuarios = usuarioDao.getAllUsuariosDirect()

        try {
            val mapper = AwsClientProvider.provideDynamoDBMapper(
                clave1,
                clave2,
                clave3
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

suspend fun sincronizarVehiculosBidireccionalCompleto(context: Context) {
    withContext(Dispatchers.IO) {
        val db = GascDataBase.getDatabase(context)
        val vehiculoDao = db.vehiculoDao()
        var localVehiculos = vehiculoDao.obtenerTodosLosVehiculos()

        try {
            val mapper = AwsClientProvider.provideDynamoDBMapper(
                clave1,
                clave2,
                clave3
            )

            // 1. Obtener TODOS los vehículos de la nube con paginación
            val vehiculosNube = mutableListOf<VehiculoDynamo>()
            var lastEvaluatedKey: Map<String, AttributeValue>? = null
            do {
                val scanExpression = DynamoDBScanExpression().apply {
                    lastEvaluatedKey?.let { exclusiveStartKey = it }
                }
                val result = mapper.scanPage(VehiculoDynamo::class.java, scanExpression)
                vehiculosNube.addAll(result.results)
                lastEvaluatedKey = result.lastEvaluatedKey
            } while (lastEvaluatedKey != null && lastEvaluatedKey.isNotEmpty())

            val mapaNube = vehiculosNube.associateBy { it.id }

            // 2. Verificar vehículos locales contra la nube
            val vehiculosParaActualizar = mutableListOf<Vehiculo>()
            val vehiculosParaCrearEnNube = mutableListOf<Vehiculo>()
            val idsInexistentes = mutableListOf<Long>()

            for (vehiculoLocal in localVehiculos) {
                if (vehiculoLocal.dynamoId != null && vehiculoLocal.dynamoId != 0L) {
                    val vehiculoNube = mapaNube[vehiculoLocal.dynamoId]

                    if (vehiculoNube == null) {
                        // Vehículo fue eliminado en la nube
                        idsInexistentes.add(vehiculoLocal.dynamoId!!)
                    } else {
                        // Verificar si necesita actualización
                        if (vehiculoLocal.placa != vehiculoNube.placa ||
                            vehiculoLocal.marca != vehiculoNube.marca ||
                            vehiculoLocal.anio != vehiculoNube.anio ||
                            vehiculoLocal.color != vehiculoNube.color ||
                            vehiculoLocal.costoPorDia != vehiculoNube.costoPorDia ||
                            vehiculoLocal.activo != vehiculoNube.activo ||
                            vehiculoLocal.imagen != vehiculoNube.imagen ||
                            vehiculoLocal.usuarioId != vehiculoNube.usuarioId.toInt()) {

                            // Preferencia: cambios locales sobre la nube
                            vehiculosParaActualizar.add(vehiculoLocal)
                        }
                    }
                } else {
                    // Vehículo nuevo (sin dynamoId)
                    vehiculosParaCrearEnNube.add(vehiculoLocal)
                }
            }

            // 3. Resetear dynamoId para vehículos eliminados en la nube
            if (idsInexistentes.isNotEmpty()) {
                vehiculoDao.resetDynamoIds(idsInexistentes)
                Log.d("SYNC", "Reseteados ${idsInexistentes.size} IDs de vehículos eliminados en la nube")
                // Recargar vehículos locales después del cambio
                localVehiculos = vehiculoDao.obtenerTodosLosVehiculos()
            }

            // 4. Crear nuevos vehículos locales en la nube
            for (vehiculoLocal in vehiculosParaCrearEnNube) {
                try {
                    val dynamoId = generarIdUnico()

                    val vehiculoNube = VehiculoDynamo().apply {
                        id = dynamoId
                        placa = vehiculoLocal.placa
                        marca = vehiculoLocal.marca
                        anio = vehiculoLocal.anio
                        color = vehiculoLocal.color
                        costoPorDia = vehiculoLocal.costoPorDia
                        activo = vehiculoLocal.activo
                        imagen = vehiculoLocal.imagen
                        usuarioId = vehiculoLocal.usuarioId.toLong()
                    }

                    mapper.save(vehiculoNube)
                    vehiculoDao.actualizarDynamoId(vehiculoLocal.id, dynamoId)
                    Log.d("SYNC", "Creado vehículo en nube: ${vehiculoLocal.placa} (ID: $dynamoId)")
                } catch (e: Exception) {
                    Log.e("SYNC", "Error al crear vehículo en nube: ${vehiculoLocal.placa}", e)
                }
            }

            // 5. Actualizar vehículos modificados localmente
            for (vehiculoLocal in vehiculosParaActualizar) {
                try {
                    val vehiculoNube = VehiculoDynamo().apply {
                        id = vehiculoLocal.dynamoId!!
                        placa = vehiculoLocal.placa
                        marca = vehiculoLocal.marca
                        anio = vehiculoLocal.anio
                        color = vehiculoLocal.color
                        costoPorDia = vehiculoLocal.costoPorDia
                        activo = vehiculoLocal.activo
                        imagen = vehiculoLocal.imagen
                        usuarioId = vehiculoLocal.usuarioId.toLong()
                    }

                    mapper.save(vehiculoNube)
                    Log.d("SYNC", "Actualizado vehículo en nube: ${vehiculoLocal.placa} (ID: ${vehiculoLocal.dynamoId})")
                } catch (e: Exception) {
                    Log.e("SYNC", "Error al actualizar vehículo en nube: ${vehiculoLocal.placa}", e)
                }
            }

            // 6. Crear en local vehículos nuevos de la nube
            var nuevosEnLocal = 0
            for (vehiculoNube in vehiculosNube) {
                val existeEnLocal = localVehiculos.any { it.dynamoId == vehiculoNube.id }

                if (!existeEnLocal) {
                    try {
                        // Verificar si el vehículo ya existe localmente por placa
                        val vehiculoExistente = vehiculoDao.buscarPorPlaca(vehiculoNube.placa)

                        if (vehiculoExistente == null) {
                            // Vehículo completamente nuevo
                            val nuevoVehiculo = Vehiculo(
                                placa = vehiculoNube.placa,
                                marca = vehiculoNube.marca,
                                anio = vehiculoNube.anio,
                                color = vehiculoNube.color,
                                costoPorDia = vehiculoNube.costoPorDia,
                                activo = vehiculoNube.activo,
                                imagen = vehiculoNube.imagen,
                                usuarioId = vehiculoNube.usuarioId.toInt(),
                                dynamoId = vehiculoNube.id
                            )
                            vehiculoDao.insertarVehiculo(nuevoVehiculo)
                            nuevosEnLocal++
                            Log.d("SYNC", "Creado vehículo en local: ${vehiculoNube.placa} (ID: ${vehiculoNube.id})")
                        } else {
                            // Vehículo existe localmente pero sin dynamoId (resincronización)
                            if (vehiculoExistente.dynamoId == null || vehiculoExistente.dynamoId == 0L) {
                                vehiculoDao.actualizarDynamoId(vehiculoExistente.id, vehiculoNube.id)
                                Log.d("SYNC", "Asociado vehículo existente: ${vehiculoNube.placa} (ID: ${vehiculoNube.id})")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("SYNC", "Error al crear vehículo en local: ${vehiculoNube.placa}", e)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                val mensaje = buildString {
                    append("Sincronización de vehículos completada\n")
                    append("Nuevos en nube: ${vehiculosParaCrearEnNube.size}\n")
                    append("Actualizados: ${vehiculosParaActualizar.size}\n")
                    append("Recuperados: $nuevosEnLocal")
                }

                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("SYNC", "Error en sincronización de vehículos", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al sincronizar vehículos: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

private fun generarIdUnico(): Long {
    return System.currentTimeMillis() + Random.nextLong(1000)
}