package com.example.gacs_wheel.Controller
import android.content.Context
import android.util.Log
import com.example.gacs_wheel.Model.GascDataBase
import com.example.gacs_wheel.Model.Vehiculo

object VehiculoController {

    suspend fun insertarVehiculo(context: Context, vehiculo: Vehiculo): Boolean {
        return try {
            val db = GascDataBase.getDatabase(context)
            db.vehiculoDao().insertarVehiculo(vehiculo)
            true // Inserción exitosa
        } catch (e: Exception) {

            false
        }
    }

    suspend fun obtenerTodos(context: Context): List<Vehiculo> {
        return GascDataBase.getDatabase(context).vehiculoDao().obtenerTodosLosVehiculos()
    }

    suspend fun obtenerPorPlaca(context: Context, placa: String): Vehiculo? {
        return GascDataBase.getDatabase(context).vehiculoDao().buscarPorPlaca(placa)
    }

    suspend fun editarVehiculo(context: Context, vehiculo: Vehiculo): Boolean {
        return try {
            val db = GascDataBase.getDatabase(context)
            db.vehiculoDao().actualizarPorId(vehiculo) > 0
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarVehiculo(context: Context, placa: String) {
        val dao = GascDataBase.getDatabase(context).vehiculoDao()
        val vehiculo = dao.buscarPorPlaca(placa)
        if (vehiculo != null) dao.eliminarVehiculo(vehiculo)
    }
}
