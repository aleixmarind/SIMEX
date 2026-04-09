package com.example.simex_app.ui.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simex_app.data.models.Comanda
import com.example.simex_app.databinding.ItemComandaBinding

class ComandaAdapter(private val lista: List<Comanda>) :
    RecyclerView.Adapter<ComandaAdapter.ComandaViewHolder>() {

    class ComandaViewHolder(val binding: ItemComandaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComandaViewHolder {
        val binding = ItemComandaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComandaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComandaViewHolder, position: Int) {
        val item = lista[position]
        with(holder.binding) {
            tvNumPedido.text = item.numPedido
            tvNombreOferta.text = item.nombreOferta
            tvRuta.text = "${item.puertoOrigen} ➔ ${item.puertoDestino}"
            tvEstado.text = item.estado.uppercase()
            tvFecha.text = "Entrega estimada: ${item.fechaEntrega}"
        }
    }

    override fun getItemCount() = lista.size
}