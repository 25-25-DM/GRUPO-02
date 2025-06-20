package com.example.gacs_wheel.Model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val password: String,
    val rol: String,
    var dynamoId: Long? = null  // Nuevo campo para el ID de DynamoDB
)