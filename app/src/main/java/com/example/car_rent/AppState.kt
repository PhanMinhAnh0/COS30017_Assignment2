package com.example.car_rent

import android.content.SharedPreferences
import android.content.Context
import android.net.Uri
object AppState {
    const val START_BALANCE = 500
    const val MAX_TOTAL = 400

    private const val PREF_NAME = "car_rent_state"
    private const val KEY_FAVORITES = "favorites"     // "1,3,4"
    private const val KEY_RENTALS = "rentals"         // "2:3,5:1" (carId:days)

    private var prefs: SharedPreferences? = null
    private var loaded = false

    var balance = START_BALANCE
        private set
    var availableCars = CarResp.getAllCars().toMutableList()
    val rentals = mutableListOf<Rental>()
    val favoriteIds = mutableSetOf<Int>()

    var currentIndex =0
    var query = ""
    var sortMode = 0

    val totalRented: Int get() = rentals.sumOf { it.total }

    fun init(context: Context) {
        if (loaded) return
        loaded = true
        val p = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs = p
        val allCars = CarResp.getAllCars()

        favoriteIds.clear()
        p.getString(KEY_FAVORITES, "").orEmpty().split(",")
            .mapNotNullTo(favoriteIds) { it.toIntOrNull() }

        rentals.clear()
        p.getString(KEY_RENTALS, "").orEmpty().split(",").forEach { entry ->
            val parts = entry.split(":")
            val car = allCars.find { it.id == parts.getOrNull(0)?.toIntOrNull() }
            val days = parts.getOrNull(1)?.toIntOrNull()
            val name = Uri.decode(parts.getOrNull(2) ?: "")
            if (car != null && days != null) {
                rentals.add(Rental(car, days, days * car.cost, name))
            }
        }

        availableCars.clear()
        availableCars.addAll(allCars.filter { c -> rentals.none { it.car.id == c.id } })
        balance = START_BALANCE - totalRented
    }

    private fun save() {
        prefs?.edit()
            ?.putString(KEY_FAVORITES, favoriteIds.joinToString(","))
            ?.putString(KEY_RENTALS, rentals.joinToString(",") {
                "${it.car.id}:${it.days}:${Uri.encode(it.receiverName)}"
            })
            ?.apply()
    }


    fun addRental(r: Rental) {
        rentals.add(r)
        availableCars.removeAll { it.id == r.car.id }
        balance -= r.total
        save()
    }

    fun cancel(r: Rental) {
        if (rentals.remove(r)) {
            balance += r.total
            availableCars.add(r.car)
            availableCars.sortBy { it.id }
            save()
        }
    }

    fun isFavorite(carId: Int) = carId in favoriteIds

    fun toggleFavorite(carId: Int): Boolean {
        val nowFavorite = if (favoriteIds.remove(carId)) false else { favoriteIds.add(carId); true }
        save()
        return nowFavorite
    }

    fun favoriteCars(): List<Car> =
        CarResp.getAllCars().filter { it.id in favoriteIds }
}