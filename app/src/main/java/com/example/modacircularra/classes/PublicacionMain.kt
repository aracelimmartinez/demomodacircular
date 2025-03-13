package com.example.modacircularra.classes

class PublicacionMain {
    var id: String? = null
    var foto: String? = null
    var titulo: String? = null
    var precio: String? = null
    var fecha: Long? = null

    constructor()

    constructor(
        id: String?,
        foto: String?,
        titulo: String?,
        precio: String?,
        fecha: Long?
    ) {
        this.id = id
        this.foto = foto
        this.titulo = titulo
        this.precio = precio
        this.fecha = fecha
    }
}