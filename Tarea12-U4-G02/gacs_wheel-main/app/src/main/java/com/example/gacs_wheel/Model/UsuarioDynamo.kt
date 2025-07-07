package com.example.gacs_wheel.Model

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*

@DynamoDBTable(tableName = "Usuarios")
class UsuarioDynamo {
    @get:DynamoDBHashKey(attributeName = "id")
    var id: Long = 0  // Asegúrate que no sea nullable

    @get:DynamoDBAttribute(attributeName = "nombre")
    var nombre: String = ""

    @get:DynamoDBAttribute(attributeName = "password")
    var password: String = ""

    @get:DynamoDBAttribute(attributeName = "rol")
    var rol: String = ""
}