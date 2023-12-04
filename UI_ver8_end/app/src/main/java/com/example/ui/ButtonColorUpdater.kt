package com.example.ui

import android.content.res.ColorStateList
import android.util.Log

class ButtonColorUpdater(private val activity: MainActivity) {
    fun updateButtonColor(seatID: String, reserv_f: Boolean, tem_c: Float, weight_c: Float) {
        val colorGreen = activity.resources.getColor(R.color.green, activity.theme)
        val colorYellow = activity.resources.getColor(R.color.yellow, activity.theme)
        val colorRed = activity.resources.getColor(R.color.red, activity.theme)
        val colorGray = activity.resources.getColor(R.color.gray, activity.theme)

        try {
            val button = activity.getButtonBySeatID(seatID)
            when (seatID) {
                "A1" -> {
                    if (weight_c >= 5.0 && tem_c >= 10.0) {
                        when {
                            reserv_f -> button.backgroundTintList = ColorStateList.valueOf(colorGreen)
                            !reserv_f -> button.backgroundTintList = ColorStateList.valueOf(colorRed)
                            else -> button.backgroundTintList = ColorStateList.valueOf(colorYellow)
                        }
                    } else {
                        button.backgroundTintList = ColorStateList.valueOf(colorGray)
                    }
                }
                "A3" -> {
                    if (weight_c >= 5.0 && tem_c >= 10.0) {
                        when {
                            reserv_f -> button.backgroundTintList =
                                ColorStateList.valueOf(colorGreen)

                            !reserv_f -> button.backgroundTintList =
                                ColorStateList.valueOf(colorRed)

                            else -> button.backgroundTintList = ColorStateList.valueOf(colorYellow)
                        }
                    } else {
                        button.backgroundTintList = ColorStateList.valueOf(colorGray)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Error", "The value is not measured : $e")
        }
    }
}

