package com.example.modacircularra.viewModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PrendaViewModel(
    var id: String? = null,
    var foto: String? = null,
    var titulo: String? = null,
    var precio: String? = null
) : Parcelable
