package com.example.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 최초 데이터 로딩
        loadDataAndUpdateUI()

        // 5초마다 업데이트 작업 스케줄링
        handler.postDelayed(updateRunnable, TimeUnit.SECONDS.toMillis(5))
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
            handler.postDelayed(this, TimeUnit.SECONDS.toMillis(5))
        }
    }

    private fun loadDataAndUpdateUI() {
        val seatDataList = ArrayList<String>()
        val collectionName = "SEATDATA"

        GlobalScope.launch(Dispatchers.IO) {
            for (row in 1..6) {
                for (column in listOf("A", "B", "C", "D")) {
                    val seatID = "$column$row"
                    try {
                        val docRef = db.collection(collectionName).document(seatID)
                        val documentSnapshot = docRef.get().await()
                        if (documentSnapshot.exists()) {
                            val data = documentSnapshot.data
                            val seat_f = data?.get("seat_f")
                            val reserv_f = data?.get("reserv_f")


                            if (seat_f != null && reserv_f != null) {
                                Log.d(
                                    "FirestoreData",
                                    "$seatID: seat_f=$seat_f, reserv_f=$reserv_f"
                                )
                                seatDataList.add("$seatID: seat_f=$seat_f, reserv_f=$reserv_f")

                                // logic
                                // A1 logic
                                runOnUiThread() {
                                    if (seatID == "A1" && reserv_f == true) {
                                        val btnA1 = findViewById<Button>(R.id.btnA1)
                                        val colorGreen = resources.getColor(R.color.green)
                                        btnA1.backgroundTintList =
                                            ColorStateList.valueOf(colorGreen) // 배경 이미지 리소스로 설정
                                    } else if (seatID == "A1" && reserv_f == false) {
                                        val btnA1 = findViewById<Button>(R.id.btnA1)
                                        val colorRed = resources.getColor(R.color.red)
                                        btnA1.backgroundTintList =
                                            ColorStateList.valueOf(colorRed) // 배경 이미지 리소스로 설정
                                    }
                                }

                                // A2 logic
                                runOnUiThread() {
                                    if (seatID == "A2" && reserv_f == true) {
                                        val btnA2 = findViewById<Button>(R.id.btnA2)
                                        val colorGreen = resources.getColor(R.color.green)
                                        btnA2.backgroundTintList =
                                            ColorStateList.valueOf(colorGreen) // 배경 이미지 리소스로 설정
                                    } else if (seatID == "A2" && reserv_f == false) {
                                        val btnA2 = findViewById<Button>(R.id.btnA2)
                                        val colorRed = resources.getColor(R.color.red)
                                        btnA2.backgroundTintList =
                                            ColorStateList.valueOf(colorRed) // 배경 이미지 리소스로 설정
                                    }
                                }

                                // A3 logic
                                runOnUiThread() {
                                    if (seatID == "A3" && reserv_f == true) {
                                        val btnA3 = findViewById<Button>(R.id.btnA3)
                                        val colorGreen = resources.getColor(R.color.green)
                                        btnA3.backgroundTintList =
                                            ColorStateList.valueOf(colorGreen) // 배경 이미지 리소스로 설정
                                    } else if (seatID == "A3" && reserv_f == false) {
                                        val btnA3 = findViewById<Button>(R.id.btnA3)
                                        val colorRed = resources.getColor(R.color.red)
                                        btnA3.backgroundTintList =
                                            ColorStateList.valueOf(colorRed) // 배경 이미지 리소스로 설정
                                    }
                                }
                            } else {
                                seatDataList.add("$seatID: Data not found or null")
                            }


                        } else {
                            seatDataList.add("$seatID: Data not found")
                        }
                    } catch (e: Exception) {
                        seatDataList.add("$seatID: Error accessing data")
                    }
                }
            }
            // UI 업데이트는 메인(UI) 스레드에서 수행
            GlobalScope.launch(Dispatchers.Main) {
                updateUI(seatDataList)
            }
        }
    }

    private fun updateUI(dataList: List<String>) {
        // UI 업데이트 작업, 예를 들면 TextView에 데이터 출력
        dataList.forEach { seatData ->
            println(seatData)
        }
    }
}

