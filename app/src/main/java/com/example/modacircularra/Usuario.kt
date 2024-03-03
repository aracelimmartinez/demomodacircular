package com.example.modacircularra

class Usuario {
    var uid: String? = null
    var nombre: String? = null
    var apellido: String? = null
    var email: String? = null

    constructor() {}

    constructor(uid: String?, name: String?, lastname: String?, email: String?) {
        this.uid = uid
        this.nombre = name
        this.apellido = lastname
        this.email = email
    }
}