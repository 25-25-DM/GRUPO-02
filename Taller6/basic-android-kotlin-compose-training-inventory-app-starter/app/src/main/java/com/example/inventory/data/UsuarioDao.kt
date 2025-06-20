package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(usuario: Usuario)


    // Para obtener un usuario por id
    @Query("SELECT * FROM usuario WHERE id = :id")
    fun getUsuarioById(id: Int): Flow<Usuario?>

    // Para obtener un usuario por nombre y apellido (puede usarse para validar login)
    @Query("SELECT * FROM usuario WHERE nombre = :nombre AND apellido = :apellido LIMIT 1")
    suspend fun getUsuarioByNombreApellido(nombre: String, apellido: String): Usuario?

    // Obtener todos los usuarios (opcional)
    @Query("SELECT * FROM usuario ORDER BY nombre ASC")
    fun getAllUsuarios(): Flow<List<Usuario>>
}
