package com.example.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseReference = FirebaseDatabase.getInstance().reference
        // 최초 데이터 로딩
        loadDataAndUpdateUI()
        handler.postDelayed(updateRunnable, TimeUnit.SECONDS.toMillis(1))
        // 1초마다 업데이트 작업 스케줄링

        val btnExit = findViewById<Button>(R.id.btnExit)
        btnExit.setOnClickListener {
            finish() // 앱 종료
        }
    }

    // 5초마다 실행될 업데이트 작업
    private val updateRunnable: Runnable = object : Runnable {
        override fun run() {
            // 1초마다 데이터 로딩 및 로그 출력
            Log.d("DataUpdate", "Data loaded at ${System.currentTimeMillis()}")
            loadDataAndUpdateUI()
            // 다시 1초 뒤에 실행
            handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadDataAndUpdateUI() {
        GlobalScope.launch(Dispatchers.IO) {
            val firestore = FirebaseFirestore.getInstance()
            val realtimeDatabase = FirebaseDatabase.getInstance()

            for (row in 1..3) {

                val seatID = "A$row"

                try {
                    // Fetch data from Firestore (seat_f, reserv_f)
                    val firestoreDocRef = firestore.collection("SEATDATA").document(seatID)
                    val firestoreSnapshot = firestoreDocRef.get().await()
                    val reserv_f = firestoreSnapshot.getBoolean("reserv_f")

                    // Fetch data from Realtime Database (seat_c, tem_c, weight_c)
                    val realtimeDbRef = realtimeDatabase.getReference()
                    realtimeDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
s
                            // values (seat_c, tem_c, weight_c)
                            val tem_c_n_A1 = (dataSnapshot.child("temperatureA1").value as Double).toFloat()
                            // val weight_c_n_A1 = (dataSnapshot.child("Weight").value as Long).toInt()

                            // absolute value
                            val tem_c_A1 = Math.abs(tem_c_n_A1)
                            // val weight_c_A1 = Math.abs(weight_c_n_A1)

                            // Update UI based on conditions
                            runOnUiThread {
                                val button = getButtonBySeatID(seatID)
                                val colorGreen = resources.getColor(R.color.green, theme)
                                val colorYellow = resources.getColor(R.color.yellow, theme)
                                val colorRed = resources.getColor(R.color.red, theme)
                                val colorGray = resources.getColor(R.color.gray, theme)

                                // A1 logic (main)

                                if (seatID == "A1" && tem_c_A1 >= 30.0) {
                                    if (reserv_f == true) {
                                        button.backgroundTintList =
                                            ColorStateList.valueOf(colorGreen)
                                    } else if (reserv_f == false) {
                                        button.backgroundTintList =
                                            ColorStateList.valueOf(colorRed)
                                    } else {
                                        button.backgroundTintList =
                                            ColorStateList.valueOf(colorYellow)
                                    }
                                } else {
                                    button.backgroundTintList =
                                        ColorStateList.valueOf(colorGray)
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
    }

    // Helper function to get button by seat ID
    private fun getButtonBySeatID(seatID: String): Button {
        return when (seatID) {
            "A1" -> findViewById(R.id.btnA1)
            "A2" -> findViewById(R.id.btnA2)
            "A3" -> findViewById(R.id.btnA3)


            // ... (add cases for other seat IDs)
            else -> throw IllegalArgumentException("Invalid seat ID")
        }
    }
}
