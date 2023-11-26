package com.example.ui

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SeatUpdater(private val activity: MainActivity) {

    @OptIn(DelicateCoroutinesApi::class)
    fun updateAllSeats() {
        GlobalScope.launch(Dispatchers.IO) {
            for (row in 1..6) {
                for (column in listOf("A", "B", "C", "D")) {
                    val seatID = "$column$row"
                    updateSeatData(seatID)
                }
            }
        }
    }

    private val firestore
        get() = FirebaseFirestore.getInstance()

    private val realtimeDatabase
        get() = FirebaseDatabase.getInstance()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun updateSeatData(seatID: String) {
        try {
            val reserv_f = firestore.collection("SEATDATA").document(seatID).get().await().getBoolean("reserv_f")

            realtimeDatabase.getReference().addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val tem_c_A1 = Math.abs(dataSnapshot.child("temperatureA1").getValue(Double::class.java)?.toFloat() ?: 0.0f)
                    val weight_c_A1 = Math.abs(dataSnapshot.child("WeightA1").getValue(Double::class.java)?.toFloat() ?: 0.0f)

                    activity.runOnUiThread {
                        if (reserv_f != null) {
                            ButtonColorUpdater(activity).updateButtonColor(seatID, reserv_f, tem_c_A1, weight_c_A1)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Realtime Database Error: ${databaseError.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("Firebase", "Error accessing data: $e")
        }
    }
}