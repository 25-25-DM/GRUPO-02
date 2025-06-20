package com.example.gacs_wheel.Model

import com.example.gacs_wheel.R

object VehiculoStore {
    val vehiculos = mutableListOf<Vehiculo>()

    init {
        vehiculos.add(Vehiculo("ABC123", "Toyota", 2019, "Rojo", 45.0, true, R.drawable.carro2))
        vehiculos.add(Vehiculo("XYZ789", "Honda", 2020, "Azul", 55.0, true, R.drawable.carro3))
        vehiculos.add(Vehiculo("LMN456", "Ford", 2018, "Negro", 40.0, false, R.drawable.carro4))
    }

    fun obtenerVehiculos(): List<Vehiculo> = vehiculos

    fun agregarVehiculo(vehiculo: Vehiculo) {
        vehiculos.add(vehiculo)
    }

    fun eliminarVehiculo(placa: String) {
        val vehiculoAEliminar = vehiculos.find { it.placa == placa }
        if (vehiculoAEliminar != null) {
            vehiculos.remove(vehiculoAEliminar)
        }
    }

    fun editarVehiculo(vehiculo: Vehiculo) {
        val index = vehiculos.indexOfFirst { it.placa == vehiculo.placa }
        if (index != -1) {
            vehiculos[index] = vehiculo
        }
    }
}