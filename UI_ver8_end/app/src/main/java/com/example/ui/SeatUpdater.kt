import android.util.Log
import com.example.ui.ButtonColorUpdater
import com.example.ui.MainActivity
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
                    val temperatureKey = "temperature$seatID"
                    val weightKey = "Weight$seatID"

                    val tem_c = Math.abs(dataSnapshot.child(temperatureKey).getValue(Double::class.java)?.toFloat() ?: 0.0f)
                    val weight_c = Math.abs(dataSnapshot.child(weightKey).getValue(Double::class.java)?.toFloat() ?: 0.0f)

                    activity.runOnUiThread {
                        if (reserv_f != null) {
                            ButtonColorUpdater(activity).updateButtonColor(seatID, reserv_f, tem_c, weight_c)
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