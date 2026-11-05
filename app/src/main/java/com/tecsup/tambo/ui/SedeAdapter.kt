package com.tecsup.tambo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tecsup.tambo.R
import com.tecsup.tambo.data.SedeEntity
import com.tecsup.tambo.databinding.ItemSedeBinding

class SedeAdapter(
    private val onClick: (SedeEntity) -> Unit,
    private val onLongClick: (SedeEntity) -> Unit
) : ListAdapter<SedeEntity, SedeAdapter.SedeViewHolder>(SedeDiff) {

    class SedeViewHolder(private val binding: ItemSedeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            sede: SedeEntity,
            onClick: (SedeEntity) -> Unit,
            onLongClick: (SedeEntity) -> Unit
        ) {
            binding.tvNombre.text = sede.nombreLocal.uppercase()
            binding.tvDireccion.text = sede.direccion
            binding.tvHorario.text = sede.horarioAtencion
            binding.tvGerente.text =
                binding.root.context.getString(R.string.gerente_fmt, sede.gerente)

            binding.root.setOnClickListener { onClick(sede) }
            binding.root.setOnLongClickListener {
                onLongClick(sede)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SedeViewHolder {
        val binding = ItemSedeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SedeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SedeViewHolder, position: Int) {
        holder.bind(getItem(position), onClick, onLongClick)
    }
}

private object SedeDiff : DiffUtil.ItemCallback<SedeEntity>() {
    override fun areItemsTheSame(oldItem: SedeEntity, newItem: SedeEntity) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: SedeEntity, newItem: SedeEntity) =
        oldItem == newItem
}
