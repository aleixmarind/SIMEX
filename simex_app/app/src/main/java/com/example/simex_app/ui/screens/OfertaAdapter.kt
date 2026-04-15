package com.example.simex_app.ui.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simex_app.data.models.Comanda
import com.example.simex_app.databinding.ItemOfertaBinding

interface OnOfertaDecisionListener {
    fun onAceptar(oferta: Comanda)
    fun onRechazar(oferta: Comanda)
}

class OfertaAdapter(
    private var lista: MutableList<Comanda>,
    private val listener: OnOfertaDecisionListener
) : RecyclerView.Adapter<OfertaAdapter.OfertaViewHolder>() {

    class OfertaViewHolder(val binding: ItemOfertaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertaViewHolder {
        val binding = ItemOfertaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfertaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OfertaViewHolder, position: Int) {
        val item = lista[position]
        with(holder.binding) {
            tvNombreOferta.text = item.nombreOferta ?: "Oferta #${item.id}"
            tvRuta.text = "${item.puertoOrigen ?: "?"} ➔ ${item.puertoDestino ?: "?"}"

            btnAceptar.setOnClickListener { listener.onAceptar(item) }
            btnRechazar.setOnClickListener { listener.onRechazar(item) }
        }
    }

    override fun getItemCount() = lista.size

    fun updateList(newList: List<Comanda>) {
        this.lista = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun removeItem(oferta: Comanda) {
        val index = lista.indexOfFirst { it.id == oferta.id }
        if (index != -1) {
            lista.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
