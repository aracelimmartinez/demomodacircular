package com.example.modacircularra.classes

class Mensaje {
    var id: String? = null
    var texto: String? = null
    var remitente: String? = null
    var destinatario: String? = null
    var fecha: Long = System.currentTimeMillis()

    constructor()

    constructor(
        id: String?,
        texto: String?,
        remitente: String?,
        destinatario: String?,
        fecha: Long
    ) {
        this.id = id
        this.texto = texto
        this.remitente = remitente
        this.destinatario = destinatario
        this.fecha = fecha

    }
}