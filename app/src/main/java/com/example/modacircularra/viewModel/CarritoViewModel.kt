package com.example.modacircularra.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CarritoViewModel : ViewModel() {
    val carrito = MutableLiveData<MutableList<PrendaViewModel>>()
    val totalPrecio = MutableLiveData<Double>()

    init {
        carrito.value = mutableListOf()
    }

    fun agregarAlCarrito(id: String?, foto: String?, titulo: String?, precio: String?) {
        val prenda = PrendaViewModel(id, foto, titulo, precio)
        carrito.value?.add(prenda)
        precioTotal()
    }

    fun removerDelCarrito(prenda: PrendaViewModel) {
        carrito.value?.remove(prenda)
        precioTotal()
    }

    fun vaciarCarrito() {
        carrito.value?.clear()
        precioTotal()
    }

    fun obtenerUltimaPrenda(): PrendaViewModel? {
        return carrito.value?.lastOrNull()
    }

    fun precioTotal() {
        var total = 0.0
        for (prenda in carrito.value!!) {
            total += prenda.precio!!.toDouble()
        }
        totalPrecio.value = total
    }
}
