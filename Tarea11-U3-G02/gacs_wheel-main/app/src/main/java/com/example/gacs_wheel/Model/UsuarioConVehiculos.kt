package com.example.gacs_wheel.Model

import androidx.room.Embedded
import androidx.room.Relation

data class UsuarioConVehiculos(
    @Embedded val usuario: Usuario,
    @Relation(
        parentColumn = "id", // Campo en Usuario
        entityColumn = "usuarioId" // Campo en Vehiculo
    )
    val vehiculos: List<Vehiculo>
)