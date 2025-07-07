package com.example.gacs_wheel.View

import androidx.lifecycle.ViewModel
import com.example.gacs_wheel.Model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionViewModel : ViewModel() {
    // Usuario logueado o null si no hay sesión
    private val _usuarioLogueado = MutableStateFlow<Usuario?>(null)
    val usuarioLogueado: StateFlow<Usuario?> get() = _usuarioLogueado

    fun iniciarSesion(usuario: Usuario) {
        _usuarioLogueado.value = usuario
    }

    fun cerrarSesion() {
        _usuarioLogueado.value = null
    }
}
