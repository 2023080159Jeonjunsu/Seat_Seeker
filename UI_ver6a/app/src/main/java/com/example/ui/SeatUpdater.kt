package com.example.ui

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SeatUpdater(private val activity: MainActivity) {

    // FirebaseFirestore 및 FirebaseDatabase 인스턴스를 프로퍼티로 가져오도록 변경
    private val firestore = FirebaseFirestore.getInstance()
    private val realtimeDatabase = FirebaseDatabase.getInstance()

    // 코루틴을 사용해 백그라운드에서 모든 좌석 업데이트
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

    // 좌석 데이터를 업데이트하고 UI를 변경하는 함수
    private suspend fun updateSeatData(seatID: String) {
        try {
            val reserv_f = firestore.collection("SEATDATA").document(seatID).get().await().getBoolean("reserv_f")

            // ValueEventListener 대신 코루틴을 사용하여 데이터 가져오기
            val dataSnapshot = realtimeDatabase.getReference().get().await()

            // 데이터에서 온도 및 무게 정보 가져오기
            val tem_c_A1 = Math.abs(dataSnapshot.child("temperatureA1").getValue(Double::class.java)?.toFloat() ?: 0.0f)
            val weight_c_A1 = Math.abs(dataSnapshot.child("WeightA1").getValue(Double::class.java)?.toFloat() ?: 0.0f)

            // UI 업데이트를 메인 스레드에서 진행
            activity.runOnUiThread {
                if (reserv_f != null) {
                    ButtonColorUpdater(activity).updateButtonColor(seatID, reserv_f, tem_c_A1, weight_c_A1)
                }
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error accessing data: $e")
        }
    }
}