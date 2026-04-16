package com.example.simex_app.ui.screens

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simex_app.R
import com.example.simex_app.data.models.TrackingStep
import com.example.simex_app.databinding.ItemTrackingBinding

class TrackingAdapter(
    private val pasos: List<TrackingStep>,
    private val ordreActual: Int
) : RecyclerView.Adapter<TrackingAdapter.TrackingViewHolder>() {

    class TrackingViewHolder(val binding: ItemTrackingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingViewHolder {
        val binding = ItemTrackingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackingViewHolder, position: Int) {
        val paso = pasos[position]
        holder.binding.tvStepName.text = paso.nom

        // Configurar visibilidad de la línea (ocultar la última)
        holder.binding.viewLine.visibility = if (position == pasos.size - 1) View.GONE else View.VISIBLE

        // colores tracking
        when {
            paso.ordre < ordreActual -> {
                // Completado: Verde
                holder.binding.imgStatus.setImageResource(android.R.drawable.presence_online) // Círculo verde
                holder.binding.imgStatus.setColorFilter(Color.parseColor("#4CAF50"))
                holder.binding.viewLine.setBackgroundColor(Color.parseColor("#4CAF50"))
                holder.binding.tvStepName.setTextColor(Color.parseColor("#4CAF50"))
                holder.binding.tvStepName.setTypeface(null, Typeface.NORMAL)
            }
            paso.ordre == ordreActual -> {
                // Actual: Rojo (Color de marca)
                holder.binding.imgStatus.setImageResource(android.R.drawable.presence_online)
                holder.binding.imgStatus.setColorFilter(Color.parseColor("#E21D25"))
                holder.binding.viewLine.setBackgroundColor(Color.parseColor("#E5E7EB")) // gray_200
                holder.binding.tvStepName.setTextColor(Color.parseColor("#111827")) // gray_900
                holder.binding.tvStepName.setTypeface(null, Typeface.BOLD)
            }
            else -> {
                // Pendiente: Gris
                holder.binding.imgStatus.setImageResource(android.R.drawable.presence_invisible)
                holder.binding.imgStatus.setColorFilter(Color.parseColor("#9E9E9E"))
                holder.binding.viewLine.setBackgroundColor(Color.parseColor("#E5E7EB")) // gray_200
                holder.binding.tvStepName.setTextColor(Color.parseColor("#6B7280")) // gray_500
                holder.binding.tvStepName.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    override fun getItemCount() = pasos.size
}