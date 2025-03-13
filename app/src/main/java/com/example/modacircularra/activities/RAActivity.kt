package com.example.modacircularra.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.modacircularra.databinding.ActivityRaactivityBinding
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

class RAActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRaactivityBinding
    private lateinit var modelNode: ArModelNode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        frontCamera()
        arFunction()

        binding.exitButton.setOnClickListener {
            finish()
        }
    }

    private fun frontCamera() {
        //binding.sceneView.cameraFacing = CameraFacing.FRONT
    }

    private fun arFunction() {
        modelNode = ArModelNode(binding.sceneView.engine, PlacementMode.INSTANT).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/shirt.glb",
                scaleToUnits = 1f,
                centerOrigin = Position(-0.5f)
            ) {
                binding.sceneView.planeRenderer.isVisible = true
                placeModel()  // Colocar el modelo después de cargarlo exitosamente
            }
        }
        binding.sceneView.addChild(modelNode)
    }

    private fun placeModel() {
        modelNode.anchor()
        binding.sceneView.planeRenderer.isVisible =
            false // Ajusta la visibilidad según sea necesario
    }

}
