package ca.georgiancollege.final_exam

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class HealthStatViewModel : ViewModel()
{

    // Alias for the DataManager instance
    private val dataManager = DataManager.instance()

    // LiveData to hold the list of HealthStats
    private val m_healthStats = MutableLiveData<List<HealthStat>>()
    val healthStats: LiveData<List<HealthStat>> get() = m_healthStats

    // LiveData to hold the selected HealthStat
    private val m_healthStat = MutableLiveData<HealthStat?>()
    val healthStat: LiveData<HealthStat?> get() = m_healthStat

    fun loadMockHealthStats()
    {
        // Mock data for testing
        val mockData = listOf(
            HealthStat(
                id = UUID.randomUUID().toString(),
                fullName = "John Doe",
                age = 30,
                height = 180.0,
                weight = 75.0,
                date = LocalDate.now(),
                stat = "Healthy",
                metric = true
            ),
            HealthStat(
                id = UUID.randomUUID().toString(),
                fullName = "Jane Smith",
                age = 25,
                height = 160.0,
                weight = 55.0,
                date = LocalDate.now().minusDays(1),
                stat = "Fit",
                metric = true
            ),
            HealthStat(
                id = UUID.randomUUID().toString(),
                fullName = "Tom Johnson",
                age = 40,
                height = 72.0,
                weight = 180.0,
                date = LocalDate.now().minusDays(2),
                stat = "Overweight",
                metric = false
            )
        )

        m_healthStats.value = mockData
    }


    // Function to load all HealthStats from the DataManager
    fun loadAllHealthStats() {
        viewModelScope.launch {
            m_healthStats.value = dataManager.getAllHealthStats()
        }
    }

    // Function to load a specific HealthStat by ID from the DataManager
    fun loadHealthStatById(id: String) {
        viewModelScope.launch {
            m_healthStat.value = dataManager.getHealthStatById(id)
        }
    }

    // Function to save or update a HealthStat in the DataManager
    fun saveHealthStat(healthStat: HealthStat) {
        viewModelScope.launch {
            if (healthStat.id.isEmpty()) {
                dataManager.insert(healthStat)
            } else {
                dataManager.update(healthStat)
            }
            loadAllHealthStats()
        }
    }

    // Function to delete a HealthStat from the DataManager
    fun deleteHealthStat(healthStat: HealthStat) {
        viewModelScope.launch {
            dataManager.delete(healthStat)
            loadAllHealthStats()
        }
    }
}

