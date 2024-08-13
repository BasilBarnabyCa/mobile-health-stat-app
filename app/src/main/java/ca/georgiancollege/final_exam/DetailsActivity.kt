package ca.georgiancollege.final_exam

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ca.georgiancollege.final_exam.databinding.ActivityDetailsBinding
import ca.georgiancollege.final_exam.databinding.CalendarDialogBinding
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID

class DetailsActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityDetailsBinding
    private val viewModel: HealthStatViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var dataManager: DataManager

    private var healthStatId: String? = null
    private var isMetric: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the DataManager
        dataManager = DataManager.instance()

        // Get the HealthStat ID and metric system from the intent
        healthStatId = intent.getStringExtra("healthStatId")
        isMetric = intent.getBooleanExtra("metric", false)

        // Update the unit text based on the metric system selection
        binding.unitsTextView.text = if (isMetric) getString(R.string.kg) else getString(R.string.pounds)

        binding.dateEditText.isFocusable = false
        binding.dateEditText.isClickable = true

        binding.dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Set up the increment and decrement buttons for the weight
        setUpButtonListeners()

        // a conditional check to confirm that we are either adding or updating
        if (healthStatId != null)
        {
            viewModel.loadHealthStatById(healthStatId!!)
        }
        else
        {
            // we don't need the delete button when we are adding a new HealthStat
            binding.deleteButton.visibility = View.GONE
        }

        // Observe the HealthStat LiveData to update the UI
        viewModel.healthStat.observe(this) { tvShow ->
            tvShow?.let {
                binding.weightEditText.setText(it.weight.toString())
                binding.statEditText.setText(it.stat)
                binding.dateEditText.setText(it.date.toString())
            }
        }

        binding.saveButton.setOnClickListener {
            saveHealthStat()
        }

        binding.deleteButton.setOnClickListener {
            deleteHealthStat()
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Sets up the onTouchListeners for the increment and decrement buttons for weight.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setUpButtonListeners()
    {
        // Increment button
        binding.incrementButton.setOnTouchListener { _, event ->
            handleTouch(event, increment = true)
            true
        }

        // Decrement button
        binding.decrementButton.setOnTouchListener { _, event ->
            handleTouch(event, increment = false)
            true
        }
    }

    /**
     * Handles touch events for increment and decrement buttons.
     *
     * @param event The motion event triggering the touch.
     * @param increment Whether the value should be incremented or decremented.
     */
    private fun handleTouch(event: MotionEvent, increment: Boolean)
    {
        when (event.action)
        {
            MotionEvent.ACTION_DOWN -> {
                startAdjusting(increment)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                stopAdjusting()
            }
        }
    }

    /**
     * Starts continuously adjusting the weight value.
     *
     * @param increment Whether to increment or decrement the value.
     */
    private fun startAdjusting(increment: Boolean)
    {
        val runnable = object : Runnable
        {
            override fun run()
            {
                adjustWeight(increment)
                handler.postDelayed(this, 100)  // Adjust every 100ms
            }
        }
        handler.post(runnable)
    }

    /**
     * Stops the continuous adjustment of the weight value.
     */
    private fun stopAdjusting()
    {
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * Adjusts the weight value in the EditText.
     *
     * @param increment Whether to increment or decrement the weight.
     */
    private fun adjustWeight(increment: Boolean)
    {
        val currentWeight = binding.weightEditText.text.toString().toDoubleOrNull() ?: 0.0
        val adjustmentFactor = if (isMetric) 0.1 else 1.0  // Adjust by 0.1 for metric, 1.0 for imperial
        val newWeight = if (increment) currentWeight + adjustmentFactor else currentWeight - adjustmentFactor

        // Ensure the weight does not drop below 0.0
        if (newWeight >= 0.0)
        {
            val roundedWeight = if (isMetric) {
                String.format(Locale.US, "%.1f", newWeight) // Round to 1 decimal place for metric
            } else {
                String.format(Locale.US, "%.0f", newWeight) // Round to 0 decimal places for imperial
            }
            binding.weightEditText.setText(roundedWeight)
        }
    }

    private fun showDatePickerDialog()
    {
        // Use ViewBinding to inflate the dialog view
        val dialogBinding = CalendarDialogBinding.inflate(LayoutInflater.from(this))

        // Get the current date
        val currentDate = LocalDate.now()

        // Set the calendar view date to the current date
        val currentTimeInMillis = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()).time
        dialogBinding.dialogCalendarView.date = currentTimeInMillis

        var selectedDate: LocalDate = currentDate  // Set the default selected date to the current date

        // Set the listener for date selection
        dialogBinding.dialogCalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
        }

        // Create and show the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        // Handle the select button click
        dialogBinding.selectDateButton.setOnClickListener {
            selectedDate.let {
                // Format the date and set it to the EditText
                val formatter = DateTimeFormatter.ofPattern("MM/dd/yy")
                binding.dateEditText.setText(it.format(formatter))
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    // Function to save or update a HealthStat
    private fun saveHealthStat()
    {
        val weight = binding.weightEditText.text.toString().toDouble()
        val stat = binding.statEditText.text.toString()
        val dateString = binding.dateEditText.text.toString()

        val formatter = DateTimeFormatter.ofPattern("MM/dd/yy")
        val date = LocalDate.parse(dateString, formatter)

        if (weight > 0.0 && stat.isNotEmpty() && date != null)
        {
            val healthStat = HealthStat(
                id = healthStatId ?: UUID.randomUUID().toString(),
                weight = weight,
                stat = stat,
                date = date,
                metric = isMetric
            )
            viewModel.saveHealthStat(healthStat)
            Toast.makeText(this, "Health Stat Saved", Toast.LENGTH_SHORT).show()
            finish()
        }
        else
        {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to delete a HealthStat
    private fun deleteHealthStat()
    {
        healthStatId?.let { _ ->
            AlertDialog.Builder(this)
                .setTitle("Delete Health Stat")
                .setMessage("Are you sure you want to delete this Health Stat?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.healthStat.value?.let {
                        viewModel.deleteHealthStat(it)
                        Toast.makeText(this, "Health Stat Deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}
