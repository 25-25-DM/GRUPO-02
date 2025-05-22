package com.example.gacs_wheel.Model

object UsuarioStore {
    val usuarios = mutableListOf<Usuario>()

    init {
        // Usuarios iniciales
        usuarios.add(Usuario("alexis", "hurtado"))
        usuarios.add(Usuario("john", "quinatoa"))
        usuarios.add(Usuario("gabriela", "rodriguez"))
        usuarios.add(Usuario("dario", "vergara"))
    }

    fun agregarUsuario(usuario: Usuario): Boolean {
        val existe = usuarios.any {
            it.nombre.equals(usuario.nombre, ignoreCase = true) &&
                    it.apellido.equals(usuario.apellido, ignoreCase = true)
        }

        return if (!existe) {
            usuarios.add(usuario)
            true
        } else {
            false
        }
    }

    fun autenticar(nombre: String, apellido: String): Boolean {
        return usuarios.any {
            it.nombre.equals(nombre, ignoreCase = true) &&
                    it.apellido.equals(apellido, ignoreCase = true)
        }
    }

    fun obtenerUsuarios(): List<Usuario> = usuarios
}