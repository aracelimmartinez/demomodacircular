package com.example.modacircularra.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CarritoViewModel : ViewModel() {
    val carrito = MutableLiveData<MutableList<PrendaViewModel>>()
    val totalPrecio = MutableLiveData<Double>()
    val carritoCount = MutableLiveData<Int>()

    init {
        carrito.value = mutableListOf()
        carritoCount.value = 0
    }

    fun agregarAlCarrito(id: String?, foto: String?, titulo: String?, precio: String?) {
        val prenda = PrendaViewModel(id, foto, titulo, precio)
        carrito.value?.add(prenda)
        precioTotal()
        actualizarContador()
    }

    fun removerDelCarrito(prenda: PrendaViewModel) {
        carrito.value?.remove(prenda)
        precioTotal()
        actualizarContador()
    }

    fun vaciarCarrito() {
        carrito.value?.clear()
        precioTotal()
        carritoCount.value = 0
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

    private fun actualizarContador() {
        carritoCount.value = carrito.value?.size
    }
}
