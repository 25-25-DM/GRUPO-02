package com.example.gacs_wheel.Controller

import android.content.Context
import com.example.gacs_wheel.Model.GascDataBase
import com.example.gacs_wheel.Model.Usuario

object LoginController {
    suspend fun autenticar(context: Context, nombre: String, password: String): Boolean {
        val dao = GascDataBase.getDatabase(context).usuarioDao()
        val passwordHash = HashUtil.md5(password)
        val usuario = dao.getUsuarioByNombreApellido(nombre, passwordHash)
        return usuario != null
    }
    suspend fun autenticarYObtenerUsuario(context: Context, nombre: String, password: String): Usuario? {
        val dao = GascDataBase.getDatabase(context).usuarioDao()
        val passwordHash = HashUtil.md5(password)
        return dao.getUsuarioByNombreApellido(nombre, passwordHash)
    }
}
