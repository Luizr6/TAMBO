package com.tecsup.tambo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.tecsup.tambo.R
import com.tecsup.tambo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.aplicarInsetsSistema()

        binding.btnIngresar.setOnClickListener { validarYEntrar() }
        binding.btnRecuperar.setOnClickListener {
            Toast.makeText(this, R.string.msg_recuperar, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarYEntrar() {
        val correo = binding.etCorreo.text.toString().trim()
        val clave = binding.etClave.text.toString()

        binding.tilCorreo.error = null
        binding.tilClave.error = null

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.tilCorreo.error = getString(R.string.error_correo)
            return
        }
        if (clave.length < 4) {
            binding.tilClave.error = getString(R.string.error_clave)
            return
        }

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
