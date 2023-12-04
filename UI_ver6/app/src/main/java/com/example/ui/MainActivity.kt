package com.example.ui

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val handler = android.os.Handler(Looper.getMainLooper())
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseReference = FirebaseDatabase.getInstance().reference
        loadDataAndUpdateUI()
        handler.postDelayed(updateRunnable, TimeUnit.SECONDS.toMillis(1))

        val btnExit = findViewById<Button>(R.id.btnExit)
        btnExit.setOnClickListener {
            finish()
        }
    }

    private val updateRunnable: Runnable = object : Runnable {
        override fun run() {
            Log.d("DataUpdate", "Data loaded at ${System.currentTimeMillis()}")
            loadDataAndUpdateUI()
            handler.postDelayed(this, TimeUnit.SECONDS.toMillis(3))
        }
    }

    private fun loadDataAndUpdateUI() {
        SeatUpdater(this).updateAllSeats()
    }

    fun getButtonBySeatID(seatID: String): Button {
        return when (seatID) {
            "A1" -> findViewById(R.id.btnA1)
            "A2" -> findViewById(R.id.btnA2)
            "A3" -> findViewById(R.id.btnA3)
            else -> throw IllegalArgumentException("Invalid seat ID")
        }
    }
}


