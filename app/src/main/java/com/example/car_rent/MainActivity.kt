package com.example.car_rent

import android.content.Intent
import android.os.Bundle
import android.telephony.ims.SipDetails
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.materialswitch.MaterialSwitch
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged

class MainActivity : AppCompatActivity() {

    private lateinit var carImage: ImageView
    private lateinit var carName: TextView
    private lateinit var carDetails: TextView
    private lateinit var balanceText: TextView
    private lateinit var favButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        carImage = findViewById(R.id.ivCar)
        carName = findViewById(R.id.tvCName)
        carDetails = findViewById(R.id.tvCDetail)
        balanceText = findViewById(R.id.tvBalance)
        favButton = findViewById(R.id.ibFav)

        //Dark Mode (set isChecked before listener)
        val darkSwitch = findViewById<MaterialSwitch>(R.id.swTheme)
        darkSwitch.isChecked = ThemeOption.isDarkMode()
        darkSwitch.setOnCheckedChangeListener { _, on ->
            if (on != ThemeOption.isDarkMode()) ThemeOption.setDarkMode(this, on)
        }

        //Search
        val searchInput = findViewById<EditText>(R.id.etSearch)
        searchInput.setText(AppState.query)
        searchInput.doOnTextChanged { text, _, _, _ ->
            val q = text?.toString().orEmpty()
            if (q != AppState.query) {
                AppState.query = q
                AppState.currentIndex = 0
                showCar()
            }
        }

        //Sort
        val spinner = findViewById<Spinner>(R.id.spRate)
        spinner.adapter = ArrayAdapter (
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Rating", "Price", "Name")
        )
        spinner.setSelection(AppState.sortMode, false)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                if (pos != AppState.sortMode) {
                    AppState.sortMode = pos
                    AppState.currentIndex = 0
                    showCar()
                }
            }

            override fun onNothingSelected(p: AdapterView<*>?) {
            }

        }

        //Next
        findViewById<Button>(R.id.btnNext).setOnClickListener {
            val list = visibleCars()
            if (list.isNotEmpty()) {
                AppState.currentIndex = (AppState.currentIndex + 1) % list.size
                showCar()
            }
        }

        //Rent
        findViewById<Button>(R.id.btnRent).setOnClickListener {
            val car = currentCar()
            if (car == null) {
                Toast.makeText(this, "No Car To Rent", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(
                    Intent(this, RentActivity::class.java)
                        .putExtra(RentActivity.EXTRA_CAR, car)
                )
            }
        }

        //Add / Remove Favorite
        favButton.setOnClickListener {
            val car = currentCar() ?: return@setOnClickListener
            val added = AppState.toggleFavorite(car.id)
            updateHeart(car)
            Toast.makeText(this, if (added) "${car.name} added to your Favorite" else "${car.name} removed from Favorite",
                Toast.LENGTH_SHORT).show()
        }

        //Rows
        findViewById<Button>(R.id.btnFavorite).setOnClickListener {
            startActivity(Intent(this, FavoriteActivity::class.java))
        }
        findViewById<Button>(R.id.btnYRent).setOnClickListener {
            startActivity(Intent(this, RentDetailActivity::class.java))
        }
    }

    //Refresh After Renting / Cancelling / Changing Favorite
    override fun onResume() {
        super.onResume()
        showCar()
    }

    private fun visibleCars(): List<Car> {
        val filtered = AppState.availableCars.filter {
            it.name.contains(AppState.query, ignoreCase = true)
        }
        return when (AppState.sortMode) {
            0 -> filtered.sortedByDescending { it.rating }
            1 -> filtered.sortedBy { it.cost }
            else -> filtered.sortedBy { it.name }
        }
    }

    private fun currentCar(): Car? = visibleCars().getOrNull(AppState.currentIndex)

    private fun showCar() {
        balanceText.text = "Balance: ${AppState.balance} C"
        val list = visibleCars()
        if (AppState.currentIndex >= list.size) AppState.currentIndex = 0

        val car = list.getOrNull(AppState.currentIndex)
        if (car == null) {
            carImage.setImageDrawable(null)
            carName.text = "No Car Available"
            carDetails.text = ""
            favButton.visibility = View.GONE
            return
        }
        favButton.visibility = View.VISIBLE
        carImage.setImageResource(car.imageRes)
        carName.text = car.name
        carDetails.text = "Year: ${car.year} • Distance: ${car.kilometres} km\nRating: ${car.rating}★ • Cost: ${car.cost} C/day"
        updateHeart(car)
    }

    private fun updateHeart(car: Car) {
        favButton.setImageResource(
            if (AppState.isFavorite(car.id)) R.drawable.ic_fav
        else R.drawable.ic_fav_bord
        )
    }
}