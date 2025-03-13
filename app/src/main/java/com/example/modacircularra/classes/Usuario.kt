package com.example.modacircularra.classes

class Usuario {
    var id: String? = null
    var nombre: String? = null
    var apellido: String? = null
    var email: String? = null
    var foto: String? = null

    constructor()

    constructor(id: String?, name: String?, lastname: String?, email: String?, foto: String?) {
        this.id = id
        this.nombre = name
        this.apellido = lastname
        this.email = email
        this.foto = foto
    }
}
