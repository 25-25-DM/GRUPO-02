package com.example.gacs_wheel.Model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(
    tableName = "vehiculo",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"]
        )
    ]
)
data class Vehiculo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val placa: String,
    val marca: String,
    val anio: Int,
    val color: String,
    val costoPorDia: Double,
    val activo: Boolean,
    val imagen: String,
    val usuarioId: Int

)