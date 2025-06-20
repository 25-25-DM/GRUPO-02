package com.example.gacs_wheel.Model

import androidx.room.*

@Dao
interface VehiculoDao {

    // Insertar un nuevo vehículo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVehiculo(vehiculo: Vehiculo)

    // Obtener todos los vehículos
    @Query("SELECT * FROM vehiculo")
    suspend fun obtenerTodosLosVehiculos(): List<Vehiculo>

    // Obtener vehículos por ID de usuario
    @Query("SELECT * FROM vehiculo WHERE usuarioId = :usuarioId")
    suspend fun obtenerVehiculosPorUsuario(usuarioId: Int): List<Vehiculo>

    // Buscar vehículo por placa
    @Query("SELECT * FROM vehiculo WHERE placa = :placa LIMIT 1")
    suspend fun buscarPorPlaca(placa: String): Vehiculo?

    // Actualizar vehículo existente
    @Update
    suspend fun actualizarVehiculo(vehiculo: Vehiculo): Int

    // Actualizar por Id
    @Update
    suspend fun actualizarPorId(vehiculo: Vehiculo): Int

    // Eliminar un vehículo
    @Delete
    suspend fun eliminarVehiculo(vehiculo: Vehiculo)
}
