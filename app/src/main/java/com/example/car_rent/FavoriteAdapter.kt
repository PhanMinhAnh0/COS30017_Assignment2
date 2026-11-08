package com.example.car_rent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class FavoriteAdapter (
    private val cars: MutableList<Car>,
    private val onRemoved: (Car) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivFavIMG)
        val heart: ImageButton = view.findViewById(R.id.ibRemFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false))

    override fun getItemCount(): Int = cars.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val car = cars[position]
        holder.image.setImageResource(car.imageRes)
        holder.heart.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            val removed = cars.removeAt(pos)
            notifyItemRemoved(pos)
            onRemoved(removed)
        }
    }
}