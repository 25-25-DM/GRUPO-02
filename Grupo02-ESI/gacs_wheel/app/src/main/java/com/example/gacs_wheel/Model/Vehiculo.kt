package com.example.gacs_wheel.Model

data class Vehiculo(
    var placa: String,
    var marca: String,
    var anio: Int,
    var color: String,
    var costoPorDia: Double,
    var activo: Boolean,
    var imagenResId: Int
)