package com.example.gacs_wheel.Controller

import com.example.gacs_wheel.Model.UsuarioStore

object LoginController {
    fun autenticar(nombre: String, apellido: String): Boolean {
        return UsuarioStore.usuarios.any {
            it.nombre.equals(nombre, ignoreCase = true) && it.apellido.equals(apellido, ignoreCase = true)
        }
    }
}
