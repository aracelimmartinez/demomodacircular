package com.example.modacircularra.classes

class Prenda {
    var id: String? = null
    var talle: String? = null
    var colores: String? = null
    var estado: String? = null
    var material: String? = null
    var modelo: String? = null
    var foto: String? = null

    constructor()

    constructor(
        id: String?,
        talle: String?,
        colores: String?,
        estado: String?,
        material: String?,
        modelo: String?,
        foto: String?
    ) {
        this.id = id
        this.talle = talle
        this.colores = colores
        this.estado = estado
        this.material = material
        this.modelo = modelo
        this.foto = foto
    }

}