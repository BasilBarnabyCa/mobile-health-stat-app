package ca.georgiancollege.final_exam

import android.util.Log

class DataManager private constructor()
{

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
            Log.i(TAG, "Inserting HealthStat: $healthStat")
        }
        catch(e: Exception)
        {
            Log.e(TAG, "Error inserting TVShow: ${e.message}", e)
        }
    }

    // Function to update a HealthStat
    suspend fun update(healthStat: HealthStat){
        try
        {
            Log.i(TAG, "Updating HealthStat: $healthStat")
        }
        catch(e: Exception)
        {
            Log.e(TAG, "Error updating TVShow: ${e.message}", e)
        }
    }

    // Function to delete a HealthStat
    suspend fun delete(healthStat: HealthStat) {
        try
        {
            Log.i(TAG, "Deleting HealthStat: $healthStat")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting TVShow: ${e.message}", e)
        }
    }

    // Function to get all HealthStats
    suspend fun getAllHealthStats(): List<HealthStat> {
        return try {
            Log.i(TAG, "Getting all Health Stats")
            emptyList()
        }
        catch(e: Exception)
        {
            Log.e(TAG, "Error getting TVShows: ${e.message}", e)
            emptyList()
        }
    }

    // Function to get a HealthStat by ID
    suspend fun getHealthStatById(id: String): HealthStat? {
        return try {
            Log.i(TAG, "Getting HealthStat by ID: $id")
            HealthStat()
        }
        catch (e: Exception)
        {
            Log.e(TAG, "Error getting TVShow by ID: ${e.message}", e)
            null
        }
    }
}