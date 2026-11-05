package com.tecsup.tambo.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.tecsup.tambo.databinding.ActivityMainBinding

/** Pantalla de Control de Sedes: acceso a Google Maps y al Mantenedor. */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.aplicarInsetsSistema()

        binding.btnGoogleMaps.setOnClickListener {
            startActivity(MapsActivity.crearIntent(this))
        }
        binding.btnMantenedor.setOnClickListener {
            startActivity(Intent(this, SedesActivity::class.java))
        }
    }
}
