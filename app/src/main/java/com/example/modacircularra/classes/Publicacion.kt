package com.example.modacircularra.classes

class Publicacion {
    var id: String? = null
    var usuario: String? = null
    var prenda: String? = null
    var titulo: String? = null
    var descripcion: String? = null
    var estado: String? = null
    var precio: String? = null
    var fecha: Long = System.currentTimeMillis()

    constructor()

    constructor(
        id: String?,
        usuario: String?,
        prenda: String?,
        titulo: String?,
        descripcion: String?,
        estado: String?,
        precio: String?,
        fecha: Long
    ) {
        this.id = id
        this.usuario = usuario
        this.prenda = prenda
        this.titulo = titulo
        this.descripcion = descripcion
        this.estado = estado
        this.precio = precio
        this.fecha = fecha
    }
}