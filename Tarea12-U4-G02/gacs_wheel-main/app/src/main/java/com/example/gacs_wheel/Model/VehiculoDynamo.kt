package com.example.gacs_wheel.Model

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*

@DynamoDBTable(tableName = "Vehiculos")
class VehiculoDynamo {
    @get:DynamoDBHashKey(attributeName = "id")
    var id: Long = 0  // Clave primaria (equivalente a PrimaryKey en Room)

    @get:DynamoDBAttribute(attributeName = "placa")
    var placa: String = ""

    @get:DynamoDBAttribute(attributeName = "marca")
    var marca: String = ""

    @get:DynamoDBAttribute(attributeName = "anio")
    var anio: Int = 0

    @get:DynamoDBAttribute(attributeName = "color")
    var color: String = ""

    @get:DynamoDBAttribute(attributeName = "costoPorDia")
    var costoPorDia: Double = 0.0

    @get:DynamoDBAttribute(attributeName = "activo")
    var activo: Boolean = true

    @get:DynamoDBAttribute(attributeName = "imagen")
    var imagen: String = ""

    @get:DynamoDBAttribute(attributeName = "usuarioId")
    var usuarioId: Long = 0  // Foreign Key (equivalente a usuarioId en Room)
}