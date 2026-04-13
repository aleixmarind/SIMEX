package com.example.simex_app.ui.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simex_app.data.models.Comanda
import com.example.simex_app.databinding.ItemOfertaBinding

class OfertaAdapter(
    private var lista: List<Comanda>,
    private val onItemClick: (Comanda) -> Unit
) : RecyclerView.Adapter<OfertaAdapter.OfertaViewHolder>() {

    class OfertaViewHolder(val binding: ItemOfertaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertaViewHolder {
        val binding = ItemOfertaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfertaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OfertaViewHolder, position: Int) {
        val item = lista[position]
        with(holder.binding) {
            tvIdOferta.text = "Oferta #${item.id}"
            tvNombreOferta.text = item.nombreOferta ?: "Sin nombre"
            tvRutaOferta.text = "${item.puertoOrigen ?: "?"} ➔ ${item.puertoDestino ?: "?"}"
            tvEstadoOferta.text = item.estado?.uppercase() ?: "PENDIENTE"
            tvFechaOferta.text = item.fechaEntrega ?: "TBD"
            
            root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun getItemCount() = lista.size

    fun updateList(newList: List<Comanda>) {
        this.lista = newList
        notifyDataSetChanged()
    }
}