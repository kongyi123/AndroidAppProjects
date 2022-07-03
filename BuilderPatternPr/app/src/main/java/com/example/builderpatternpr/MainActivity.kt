package com.example.builderpatternpr

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.builderpatternpr.Computer.ComputerBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val comp = ComputerBuilder("500 GB", "2 GB")
            .setBluetoothEnabled(true)
            .setGraphicsCardEnabled(true)
            .build()
    }
}