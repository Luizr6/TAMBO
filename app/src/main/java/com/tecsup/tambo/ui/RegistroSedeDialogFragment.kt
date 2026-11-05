package com.tecsup.tambo.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.tecsup.tambo.R
import com.tecsup.tambo.data.SedeEntity
import com.tecsup.tambo.databinding.DialogRegistroSedeBinding
import com.tecsup.tambo.viewmodel.SedeViewModel
import java.util.Locale

/**
 * Dialog flotante para registrar (o editar) una sede. Guarda en Room a través del ViewModel
 * compartido de la Activity. Se usa desde SedesActivity y desde MapsActivity.
 */
class RegistroSedeDialogFragment : AppCompatDialogFragment() {

    private var _binding: DialogRegistroSedeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SedeViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRegistroSedeBinding.inflate(LayoutInflater.from(requireContext()))
        viewModel = ViewModelProvider(requireActivity())[SedeViewModel::class.java]

        val args = requireArguments()
        val editId = args.getLong(ARG_ID, 0L)

        if (editId != 0L) {
            binding.tvTitulo.setText(R.string.editar_local)
            binding.btnRegistrar.setText(R.string.guardar)
            binding.etLocal.setText(args.getString(ARG_NOMBRE))
            binding.etGerente.setText(args.getString(ARG_GERENTE))
            binding.etDireccion.setText(args.getString(ARG_DIRECCION))
            binding.etHorario.setText(args.getString(ARG_HORARIO))
        }
        if (args.containsKey(ARG_LAT) && args.containsKey(ARG_LNG)) {
            binding.etLatitud.setText(formato(args.getDouble(ARG_LAT)))
            binding.etLongitud.setText(formato(args.getDouble(ARG_LNG)))
        }

        binding.btnRegistrar.setOnClickListener { guardar(editId) }
        binding.btnCancelar.setOnClickListener { dismiss() }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun guardar(editId: Long) {
        val local = binding.etLocal.text.toString().trim()
        val gerente = binding.etGerente.text.toString().trim()
        val direccion = binding.etDireccion.text.toString().trim()
        val horario = binding.etHorario.text.toString().trim()
        val lat = binding.etLatitud.text.toString().trim().replace(',', '.').toDoubleOrNull()
        val lng = binding.etLongitud.text.toString().trim().replace(',', '.').toDoubleOrNull()

        var valido = true
        valido = requerido(binding.tilLocal, local) && valido
        valido = requerido(binding.tilGerente, gerente) && valido
        valido = requerido(binding.tilDireccion, direccion) && valido
        valido = requerido(binding.tilHorario, horario) && valido

        val latOk = lat != null && lat in -90.0..90.0
        binding.tilLatitud.error = if (latOk) null else getString(R.string.error_latitud)
        val lngOk = lng != null && lng in -180.0..180.0
        binding.tilLongitud.error = if (lngOk) null else getString(R.string.error_longitud)

        if (!valido || !latOk || !lngOk || lat == null || lng == null) return

        val sede = SedeEntity(
            id = editId,
            nombreLocal = local,
            gerente = gerente,
            direccion = direccion,
            horarioAtencion = horario,
            latitud = lat,
            longitud = lng
        )
        if (editId == 0L) viewModel.insert(sede) else viewModel.update(sede)

        Toast.makeText(requireContext(), R.string.msg_sede_guardada, Toast.LENGTH_SHORT).show()
        dismiss()
    }

    private fun requerido(til: TextInputLayout, valor: String): Boolean {
        til.error = if (valor.isEmpty()) getString(R.string.error_requerido) else null
        return valor.isNotEmpty()
    }

    private fun formato(valor: Double): String = String.format(Locale.US, "%.6f", valor)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "RegistroSedeDialog"
        private const val ARG_ID = "id"
        private const val ARG_NOMBRE = "nombre"
        private const val ARG_GERENTE = "gerente"
        private const val ARG_DIRECCION = "direccion"
        private const val ARG_HORARIO = "horario"
        private const val ARG_LAT = "lat"
        private const val ARG_LNG = "lng"

        /** Nueva sede; si se pasan coordenadas, el formulario las trae precargadas. */
        fun nueva(lat: Double? = null, lng: Double? = null) =
            RegistroSedeDialogFragment().apply {
                arguments = Bundle().apply {
                    if (lat != null && lng != null) {
                        putDouble(ARG_LAT, lat)
                        putDouble(ARG_LNG, lng)
                    }
                }
            }

        fun editar(sede: SedeEntity) =
            RegistroSedeDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ID, sede.id)
                    putString(ARG_NOMBRE, sede.nombreLocal)
                    putString(ARG_GERENTE, sede.gerente)
                    putString(ARG_DIRECCION, sede.direccion)
                    putString(ARG_HORARIO, sede.horarioAtencion)
                    putDouble(ARG_LAT, sede.latitud)
                    putDouble(ARG_LNG, sede.longitud)
                }
            }
    }
}
