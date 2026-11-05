package com.tecsup.tambo.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tecsup.tambo.R
import com.tecsup.tambo.data.SedeEntity
import com.tecsup.tambo.databinding.ActivitySedesBinding
import com.tecsup.tambo.viewmodel.SedeViewModel

/** Mantenedor: listado de sedes (RecyclerView + Room) y registro mediante dialog. */
class SedesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySedesBinding
    private val viewModel: SedeViewModel by viewModels()

    private val adapter = SedeAdapter(
        onClick = { sede -> startActivity(MapsActivity.crearIntent(this, sede.id)) },
        onLongClick = { sede -> mostrarOpciones(sede) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySedesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.aplicarInsetsSistema()

        binding.rvSedes.layoutManager = LinearLayoutManager(this)
        binding.rvSedes.adapter = adapter

        viewModel.sedes.observe(this) { lista ->
            adapter.submitList(lista)
            binding.tvVacio.isVisible = lista.isEmpty()
        }

        binding.fabAgregar.setOnClickListener {
            RegistroSedeDialogFragment.nueva()
                .show(supportFragmentManager, RegistroSedeDialogFragment.TAG)
        }
    }

    private fun mostrarOpciones(sede: SedeEntity) {
        val opciones = arrayOf(getString(R.string.editar), getString(R.string.eliminar))
        MaterialAlertDialogBuilder(this)
            .setTitle(sede.nombreLocal)
            .setItems(opciones) { _, which ->
                if (which == 0) {
                    RegistroSedeDialogFragment.editar(sede)
                        .show(supportFragmentManager, RegistroSedeDialogFragment.TAG)
                } else {
                    confirmarEliminar(sede)
                }
            }
            .show()
    }

    private fun confirmarEliminar(sede: SedeEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.eliminar)
            .setMessage(getString(R.string.confirmar_eliminar, sede.nombreLocal))
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.eliminar) { _, _ -> viewModel.delete(sede) }
            .show()
    }
}
