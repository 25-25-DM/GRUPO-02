package com.example.inventory.ui.Controller

import com.example.inventory.data.InventoryDatabase
import android.content.Context
import com.example.inventory.data.Usuario


object LoginController {

    suspend fun autenticar(context: Context, nombre: String, apellido: String): Boolean {
        val dao = InventoryDatabase.getDatabase(context).usuarioDao()
        val usuario = dao.getUsuarioByNombreApellido(nombre, apellido)
        return usuario != null
    }

    suspend fun registrarUsuario(context: Context, nombre: String, apellido: String) {
        val dao = InventoryDatabase.getDatabase(context).usuarioDao()
        val nuevoUsuario = Usuario(nombre = nombre, apellido = apellido)
        dao.insert(nuevoUsuario)
    }
}
