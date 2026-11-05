package com.tecsup.tambo.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.tecsup.tambo.R
import com.tecsup.tambo.data.SedeEntity
import com.tecsup.tambo.databinding.ActivityMapsBinding
import com.tecsup.tambo.viewmodel.SedeViewModel

/**
 * Mapa con un marcador por cada sede guardada en Room.
 * - Sin extras: muestra todas las sedes.
 * - Con EXTRA_SEDE_ID: se centra en esa sede y muestra su detalle.
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private val viewModel: SedeViewModel by viewModels()

    private var mapa: GoogleMap? = null
    private var sedes: List<SedeEntity> = emptyList()
    private var idsConocidos: Set<Long>? = null
    private var camaraAjustada = false
    private var mapaCargado = false

    private val sedeIdFoco: Long by lazy { intent.getLongExtra(EXTRA_SEDE_ID, 0L) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.overlay.aplicarInsetsSistema()

        val fragmento = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fragmento.getMapAsync(this)

        binding.fabAgregar.setOnClickListener {
            abrirRegistro(mapa?.cameraPosition?.target)
        }

        viewModel.sedes.observe(this) { lista ->
            sedes = lista
            dibujarMarcadores()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isZoomControlsEnabled = false

        googleMap.setOnMapLoadedCallback {
            mapaCargado = true
            ajustarCamara()
        }
        googleMap.setOnMarkerClickListener { marker ->
            val id = marker.tag as? Long
            sedes.firstOrNull { it.id == id }?.let { sede ->
                mostrarDetalle(sede)
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
            }
            true
        }
        googleMap.setOnMapClickListener { binding.cardSede.isVisible = false }
        googleMap.setOnMapLongClickListener { latLng ->
            Toast.makeText(this, R.string.msg_ubicacion_elegida, Toast.LENGTH_SHORT).show()
            abrirRegistro(latLng)
        }

        dibujarMarcadores()
    }

    private fun dibujarMarcadores() {
        val m = mapa ?: return
        m.clear()
        sedes.forEach { sede ->
            val marker = m.addMarker(
                MarkerOptions()
                    .position(LatLng(sede.latitud, sede.longitud))
                    .title(sede.nombreLocal)
                    .snippet(sede.direccion)
            )
            marker?.tag = sede.id
        }

        // Si aparece una sede nueva (recién registrada), el mapa se mueve hacia ella.
        val previos = idsConocidos
        if (previos != null) {
            sedes.firstOrNull { it.id !in previos }?.let { nueva ->
                m.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(nueva.latitud, nueva.longitud), 16f)
                )
                mostrarDetalle(nueva)
            }
        }
        idsConocidos = sedes.map { it.id }.toSet()

        ajustarCamara()
    }

    /** Primer encuadre: a la sede elegida o a todas las sedes. */
    private fun ajustarCamara() {
        val m = mapa ?: return
        if (camaraAjustada || sedes.isEmpty()) return

        val foco = sedes.firstOrNull { it.id == sedeIdFoco }
        if (foco != null) {
            m.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(foco.latitud, foco.longitud), 17f))
            binding.tvTitulo.text = getString(R.string.titulo_mapa_sede, foco.nombreLocal)
            mostrarDetalle(foco)
            camaraAjustada = true
            return
        }
        if (sedes.size == 1) {
            val s = sedes.first()
            m.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(s.latitud, s.longitud), 15f))
            camaraAjustada = true
            return
        }
        if (!mapaCargado) return

        val bounds = LatLngBounds.Builder()
        sedes.forEach { bounds.include(LatLng(it.latitud, it.longitud)) }
        m.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 150))
        camaraAjustada = true
    }

    private fun mostrarDetalle(sede: SedeEntity) {
        binding.tvCardNombre.text = sede.nombreLocal.uppercase()
        binding.tvCardDireccion.text = sede.direccion
        binding.tvCardHorario.text = sede.horarioAtencion
        binding.tvCardGerente.text = getString(R.string.gerente_fmt, sede.gerente)
        binding.cardSede.isVisible = true
    }

    private fun abrirRegistro(punto: LatLng?) {
        RegistroSedeDialogFragment.nueva(punto?.latitude, punto?.longitude)
            .show(supportFragmentManager, RegistroSedeDialogFragment.TAG)
    }

    companion object {
        private const val EXTRA_SEDE_ID = "sede_id"

        fun crearIntent(context: Context, sedeId: Long = 0L): Intent =
            Intent(context, MapsActivity::class.java).putExtra(EXTRA_SEDE_ID, sedeId)
    }
}
