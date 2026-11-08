package com.example.car_rent

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoriteActivity : AppCompatActivity() {
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        emptyText = findViewById(R.id.tvNoFav)
        findViewById<ImageButton>(R.id.ibFBack).setOnClickListener { finish() }

        val cars = AppState.favoriteCars().toMutableList()

        val list = findViewById<RecyclerView>(R.id.rcFavList)
        list.layoutManager = GridLayoutManager(this, 2)
        list.adapter = FavoriteAdapter(cars) { removed ->
            AppState.toggleFavorite(removed.id)
            Toast.makeText(this, "${removed.name} removed from Favorite", Toast.LENGTH_SHORT).show()
            updateEmptyState(cars.isEmpty())
        }
        updateEmptyState(cars.isEmpty())
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        emptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
}