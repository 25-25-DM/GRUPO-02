package com.example.gacs_wheel.Model


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(usuario: Usuario)

    @Insert
    suspend fun insertUsuario(usuario: Usuario): Long

    // Obtener un usuario CON SUS VEHÍCULOS (relación 1:N)
    @Transaction
    @Query("SELECT * FROM usuario WHERE id = :usuarioId")
    suspend fun getUsuarioConVehiculos(usuarioId: Int): UsuarioConVehiculos

    // Para obtener un usuario por id
    @Query("SELECT * FROM usuario WHERE id = :id")
    fun getUsuarioById(id: Int): Flow<Usuario?>

    // Para obtener un usuario por nombre y password
    @Query("SELECT * FROM usuario WHERE nombre = :nombre AND password = :password LIMIT 1")
    suspend fun getUsuarioByNombreApellido(nombre: String, password: String): Usuario?

    // Obtener todos los usuarios
    @Query("SELECT * FROM usuario ORDER BY nombre ASC")
    fun getAllUsuarios(): Flow<List<Usuario>>

    //  Obtener usuario por su nombre
    @Query("SELECT * FROM usuario WHERE nombre = :nombre LIMIT 1")
    suspend fun getUsuarioByNombre(nombre: String): Usuario?

    // Obtener todos los usuarios
    @Query("SELECT * FROM usuario ORDER BY nombre ASC")
    suspend fun getAllUsuariosDirect(): List<Usuario>

    // Obtener el rol por su nombre
    @Query("SELECT rol FROM usuario WHERE nombre = :nombre LIMIT 1")
    suspend fun getRolByNombre(nombre: String): String?

    @Update
    suspend fun update(usuario: Usuario)

    @Query("UPDATE usuario SET dynamoId = :dynamoId WHERE id = :localId")
    suspend fun actualizarDynamoId(localId: Int, dynamoId: Long)

    @Query("UPDATE usuario SET dynamoId = NULL WHERE dynamoId IN (:ids)")
    suspend fun resetDynamoIds(ids: List<Long>)



}
