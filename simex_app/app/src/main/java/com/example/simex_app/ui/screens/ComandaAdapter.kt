package com.example.simex_app.ui.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simex_app.data.models.Comanda
import com.example.simex_app.databinding.ItemComandaBinding

class ComandaAdapter(
    private var lista: List<Comanda>,
    private val onItemClick: (Comanda) -> Unit
) : RecyclerView.Adapter<ComandaAdapter.ComandaViewHolder>() {

    class ComandaViewHolder(val binding: ItemComandaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComandaViewHolder {
        val binding = ItemComandaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComandaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComandaViewHolder, position: Int) {
        val item = lista[position]
        with(holder.binding) {
            tvNumPedido.text = item.numPedido ?: "N/A"
            tvNombreOferta.text = item.nombreOferta ?: "Sin nombre"
            tvRuta.text = "${item.puertoOrigen ?: "?"} ➔ ${item.puertoDestino ?: "?"}"
            tvEstado.text = item.estado?.uppercase() ?: "PENDIENTE"
            tvFecha.text = "Entrega estimada: ${item.fechaEntrega ?: "TBD"}"
            
            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount() = lista.size

    fun updateList(newList: List<Comanda>) {
        this.lista = newList
        notifyDataSetChanged()
    }
}