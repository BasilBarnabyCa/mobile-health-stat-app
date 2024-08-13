package ca.georgiancollege.final_exam

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ca.georgiancollege.final_exam.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding
    private val viewModel: HealthStatViewModel by viewModels()

    private lateinit var dataManager: DataManager

    private val adapter = HealthStatListAdapter { healthStat: HealthStat ->
        val intent = Intent(this, DetailsActivity::class.java).apply {
            putExtra("healthStatId", healthStat.id)
            putExtra("metric", healthStat.metric)  // Pass the metric system selection
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // creates an alias for the DataManager instance
        FirebaseFirestore.setLoggingEnabled(true)
        dataManager = DataManager.instance()

        binding.healthStatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.healthStatRecyclerView.adapter = adapter

        // Observe the HealthStats LiveData to update the UI
        viewModel.healthStats.observe(this) { healthStats ->
            adapter.submitList(healthStats)
        }

        // TODO: uncomment after you implement your persistent data method
        viewModel.loadAllHealthStats()


        binding.addHealthStatFAB.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java).apply {
                putExtra("metric", intent.getBooleanExtra("metric", false))  // Pass the metric system selection
            }
            startActivity(intent)
        }
    }

    override fun onResume()
    {
        super.onResume()
        // TODO: uncomment after you implement your persistent data method
        viewModel.loadAllHealthStats()
    }
}