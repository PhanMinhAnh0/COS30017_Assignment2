package com.example.car_rent

import android.app.Application

class CarRentApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeOption.init(this)
        AppState.init(this)
    }
}