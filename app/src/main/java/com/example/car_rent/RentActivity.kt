package com.example.car_rent

import android.annotation.SuppressLint
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.google.android.material.slider.Slider

class RentActivity : AppCompatActivity() {
    companion object { const val EXTRA_CAR = "CAR"}

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent)

        val car = IntentCompat.getParcelableExtra(intent, EXTRA_CAR, Car::class.java)
        if (car == null) { finish(); return }

        // Car may already be rented (e.g. opened from Favorite)
        if (AppState.availableCars.none { it.id == car.id }) {
            Toast.makeText(this, "${car.name} is already rented", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val slider = findViewById<SeekBar>(R.id.sbDuration)
        val durationLabel = findViewById<TextView>(R.id.tvRDuration)
        val costText = findViewById<TextView>(R.id.tvRCost)
        val nameInput = findViewById<EditText>(R.id.etRReceive)
        val rentButton = findViewById<Button>(R.id.btnRent)

        findViewById<ImageButton>(R.id.ibRBack).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.imgRCar).setImageResource(car.imageRes)
        findViewById<TextView>(R.id.tvRCTitle).text = "${car.name} (${car.year})"

        fun update(days: Int) {
            durationLabel.text = "Duration: $days day${if (days > 1) "s" else ""}"
            costText.text = "${days * car.cost} C (${car.cost} x $days)"
        }

        update(slider.progress + 1)
        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                update(progress + 1)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        fun updateRentButton() {
            val hasName = nameInput.text.toString().isNotBlank()
            rentButton.isEnabled = hasName
            rentButton.alpha = if (hasName) 1f else 0.5f
        }
        updateRentButton()
        nameInput.doOnTextChanged { _, _, _, _ -> updateRentButton() }

        rentButton.setOnClickListener {
            val receiver = nameInput.text.toString().trim()
            if (receiver.isEmpty()) {
                nameInput.error = "Please enter receiver name"
                return@setOnClickListener
            }

            val days = slider.progress + 1
            val total = days * car.cost

            when {
                total > AppState.balance ->
                    Toast.makeText(this, "Insufficient Credits", Toast.LENGTH_SHORT).show()

                AppState.totalRented + total > AppState.MAX_TOTAL ->
                    Toast.makeText(
                        this,
                        "Rental limit is ${AppState.MAX_TOTAL} C in total",
                        Toast.LENGTH_LONG
                    ).show()

                else -> {
                    AppState.addRental(Rental(car, days, total, receiver))
                    Toast.makeText(
                        this,
                        "Rented ${car.name} for $days day(s) to $receiver. Balance: ${AppState.balance} C",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }
}