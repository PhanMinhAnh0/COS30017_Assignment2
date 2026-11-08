package com.example.car_rent

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RentDetailActivity : AppCompatActivity() {
    private lateinit var container : LinearLayout
    private lateinit var emptyText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rent_details)

        container = findViewById(R.id.llYRContainer)
        emptyText = findViewById(R.id.tvYRNoRent)
        findViewById<ImageButton>(R.id.ibYRBack).setOnClickListener { finish() }
        render()
    }

    private fun render() {
        container.removeAllViews()
        emptyText.visibility = if (AppState.rentals.isEmpty()) View.VISIBLE else View.GONE

        for (rental in AppState.rentals.toList()) {
            val row = layoutInflater.inflate(R.layout.item_rent, container, false)
            row.findViewById<ImageView>(R.id.imgRentImg).setImageResource(rental.car.imageRes)
            row.findViewById<TextView>(R.id.tvYRDetails).text =
                "${rental.car.name} (${rental.car.year})\n" +
                        "${rental.days} day(s) • ${rental.total} C\n" +
                        "Receiver: ${rental.receiverName}"
            row.findViewById<Button>(R.id.btnYRCancel).setOnClickListener {
                AppState.cancel(rental)
                Toast.makeText(this, "Rent Canceled", Toast.LENGTH_SHORT).show()
                render()
            }
            container.addView(row)
        }
    }
}