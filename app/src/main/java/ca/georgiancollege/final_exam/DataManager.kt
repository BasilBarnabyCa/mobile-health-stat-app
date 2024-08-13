package ca.georgiancollege.final_exam

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


class DataManager private constructor()
{
    private val db: FirebaseFirestore = Firebase.firestore

    companion object
    {
        private const val TAG = "DataManager"

        @Volatile
        private var m_instance: DataManager? = null

        fun instance (): DataManager
        {
            if(m_instance == null)
            {
                synchronized(this)
                {
                    if (m_instance == null) {
                        m_instance = DataManager()
                    }
                }
            }
            return m_instance!!
        }
    }

    // Function to insert a HealthStat
    suspend fun insert(healthStat: HealthStat){
        try
        {
            db.collection("healthStats").document("healthStat.id").set("healthStat").await()
            Log.i(TAG, "Inserting HealthStat: $healthStat")
        }
        catch(e: Exception)
        {
            Log.e(TAG, "Error inserting Health Stat: ${e.message}", e)
        }
    }

    // Function to update a HealthStat
    suspend fun update(healthStat: HealthStat){
        try
        {
            db.collection("healthStats").document(healthStat.id).set(healthStat).await()
            Log.i(TAG, "Updating HealthStat: $healthStat")
        }
        catch(e: Exception)
        {
            Log.e(TAG, "Error updating Health Stat: ${e.message}", e)
        }
    }

    // Function to delete a HealthStat
    suspend fun delete(healthStat: HealthStat) {
        try
        {
            db.collection("healthStats").document(healthStat.id).delete().await()
            Log.i(TAG, "Deleting HealthStat: $healthStat")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting Health Stat: ${e.message}", e)
        }
    }

    // Function to get all HealthStats
    suspend fun getAllHealthStats(): List<HealthStat> {
        return try {
            val healthStats = db.collection("healthStats").get().await()
            healthStats.toObjects(HealthStat::class.java) ?: emptyList()
        }
        catch(e: Exception)
        {
            Log.e(TAG, "Error getting Health Stats: ${e.message}", e)
            emptyList()
        }
    }

    // Function to get a HealthStat by ID
    suspend fun getHealthStatById(id: String): HealthStat? {
        return try {
            val healthStat = db.collection("healthStats").document(id).get().await()
            healthStat?.toObject(HealthStat::class.java)
            Log.i(TAG, "Getting HealthStat by ID: $id")
            HealthStat()
        }
        catch (e: Exception)
        {
            Log.e(TAG, "Error getting Health Stat by ID: ${e.message}", e)
            null
        }
    }
}