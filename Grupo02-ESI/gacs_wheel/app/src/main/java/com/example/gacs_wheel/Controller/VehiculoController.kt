package com.example.gacs_wheel.Controller

import com.example.gacs_wheel.Model.Vehiculo
import com.example.gacs_wheel.Model.VehiculoStore

object VehiculoController {

    fun obtenerTodos(): List<Vehiculo> {
        return VehiculoStore.vehiculos
    }

    fun agregarVehiculo(vehiculo: Vehiculo) {
        VehiculoStore.agregarVehiculo(vehiculo)
    }

    fun editarVehiculo(vehiculo: Vehiculo) {
        VehiculoStore.editarVehiculo(vehiculo)
    }

    fun eliminarVehiculo(placa: String) {
        VehiculoStore.eliminarVehiculo(placa)
    }

    fun obtenerPorPlaca(placa: String): Vehiculo? {
        return VehiculoStore.vehiculos.find { it.placa == placa }
    }

    fun insertarVehiculo(vehiculo: Vehiculo): Boolean {
        val existe = VehiculoStore.vehiculos.any { it.placa == vehiculo.placa }
        return if (!existe) {
            VehiculoStore.agregarVehiculo(vehiculo)
            true
        } else {
            false
        }
    }

}