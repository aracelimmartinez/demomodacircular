package com.example.modacircularra.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.modacircularra.activities.CartActivity
import com.example.modacircularra.viewModel.PrendaViewModel
import com.example.modacircularra.databinding.FragmentCarritoDialogBinding

class CarritoDialogFragment : DialogFragment() {

    private lateinit var prenda: PrendaViewModel
    private var binding: FragmentCarritoDialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prenda = arguments?.getParcelable("prenda") ?: PrendaViewModel("", "", "", "")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCarritoDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.title?.text = prenda.titulo
        binding?.price?.text = "$${prenda.precio}"

        Glide.with(this)
            .load(prenda.foto)
            .into(binding!!.pic)

        binding?.btnCart?.setOnClickListener {
            val intent = Intent(activity, CartActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        fun newInstance(prenda: PrendaViewModel): CarritoDialogFragment {
            val fragment = CarritoDialogFragment()
            val args = Bundle()
            args.putParcelable("prenda", prenda)
            fragment.arguments = args
            return fragment
        }
    }
}
