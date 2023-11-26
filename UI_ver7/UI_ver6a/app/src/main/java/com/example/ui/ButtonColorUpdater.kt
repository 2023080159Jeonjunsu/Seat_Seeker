package com.example.ui

import android.content.res.ColorStateList
import android.util.Log

class ButtonColorUpdater(private val activity: MainActivity) {

    fun updateButtonColor(seatID: String, reserv_f: Boolean, tem_c_A1: Float, weight_c_A1: Float) {
        // 리소스에서 색상 가져오기
        val colorGreen = activity.getColor(R.color.green)
        val colorYellow = activity.getColor(R.color.yellow)
        val colorRed = activity.getColor(R.color.red)
        val colorGray = activity.getColor(R.color.gray)

        try {
            val button = activity.getButtonBySeatID(seatID)
            if (seatID == "A1" && weight_c_A1 >= 5.0 && tem_c_A1 >= 25.0) {
                // when 식을 사용하여 코드 간결화
                button.backgroundTintList = when {
                    reserv_f -> ColorStateList.valueOf(colorGreen)
                    !reserv_f -> ColorStateList.valueOf(colorRed)
                    else -> ColorStateList.valueOf(colorYellow)
                }
            } else {
                button.backgroundTintList = ColorStateList.valueOf(colorGray)
            }
        } catch (e: Exception) {
            Log.e("Error", "The value is not measured: $e")
        }
    }
}