package com.example.gacs_wheel.Controller

import com.example.gacs_wheel.Model.Usuario
import com.example.gacs_wheel.Model.UsuarioStore

object RegistroController {
    fun registrar(nombre: String, apellido: String): Boolean {
        val existe = UsuarioStore.usuarios.any {
            it.nombre.equals(nombre, ignoreCase = true) &&
                    it.apellido.equals(apellido, ignoreCase = true)
        }

        if (!existe) {
            val nuevoUsuario = Usuario(nombre, apellido)
            UsuarioStore.agregarUsuario(nuevoUsuario)
            return true
        }

        return false // Ya existe
    }
}
