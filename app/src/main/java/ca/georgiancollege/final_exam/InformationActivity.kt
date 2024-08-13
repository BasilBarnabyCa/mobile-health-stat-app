package ca.georgiancollege.final_exam

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import ca.georgiancollege.final_exam.databinding.ActivityInformationBinding
import java.util.Locale
import java.util.UUID

class InformationActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityInformationBinding
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var originalFullName: String
    private var originalAge: Int = 18
    private var originalHeight: Double = 72.0
    private var originalWeight: Double = 180.0
    private var originalMetric: Boolean = false

    /**
     * Called when the activity is first created.
     * Sets up the initial state of the form and the event listeners for the buttons.
     */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Save the original state of the form
        saveOriginalState()

        // Set up listeners for increment/decrement buttons
        setUpButtonListeners()

        // Handle the unit switch toggle with conversion
        binding.unitSwitch.setOnCheckedChangeListener { _, isChecked ->
            convertUnits(isChecked)
        }

        // Handle the Submit button click
        binding.submitButton.setOnClickListener {
            saveHealthStat()
            goToMainActivity()
        }

        // Handle the Cancel button click
        binding.cancelButton.setOnClickListener {
            resetForm()
        }
    }

    /**
     * Saves the original state of the form fields so they can be reset later if needed.
     */
    private fun saveOriginalState()
    {
        originalFullName = binding.fullNameEditText.text.toString()
        originalAge = binding.ageEditText.text.toString().toIntOrNull() ?: 18
        originalHeight = binding.heightEditText.text.toString().toDoubleOrNull() ?: 72.0
        originalWeight = binding.weightEditText.text.toString().toDoubleOrNull() ?: 180.0
        originalMetric = binding.unitSwitch.isChecked
    }

    /**
     * Sets up the onTouchListeners for the increment and decrement buttons for age, height, and weight.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setUpButtonListeners()
    {
        // Age
        binding.incrementAgeButton.setOnTouchListener { _, event -> handleTouch(event, binding.ageEditText, increment = true, isInteger = true) }
        binding.decrementAgeButton.setOnTouchListener { _, event -> handleTouch(event, binding.ageEditText, increment = false, isInteger = true) }

        // Height
        binding.incrementHeightButton.setOnTouchListener { _, event -> handleTouch(event, binding.heightEditText, increment = true, isInteger = false) }
        binding.decrementHeightButton.setOnTouchListener { _, event -> handleTouch(event, binding.heightEditText, increment = false, isInteger = false) }

        // Weight
        binding.incrementWeightButton.setOnTouchListener { _, event -> handleTouch(event, binding.weightEditText, increment = true, isInteger = false) }
        binding.decrementWeightButton.setOnTouchListener { _, event -> handleTouch(event, binding.weightEditText, increment = false, isInteger = false) }
    }

    /**
     * Handles touch events for increment and decrement buttons. Initiates or stops
     * the continuous adjustment of values based on user interaction.
     *
     * @param event The motion event triggering the touch.
     * @param editText The EditText being adjusted.
     * @param increment Whether the value should be incremented or decremented.
     * @param isInteger Whether the value is an integer (true) or a decimal (false).
     * @return Boolean indicating whether the event was handled.
     */
    private fun handleTouch(event: MotionEvent, editText: EditText, increment: Boolean, isInteger: Boolean): Boolean
    {
        when (event.action)
        {
            MotionEvent.ACTION_DOWN -> {
                startAdjusting(editText, increment, isInteger)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                stopAdjusting()
            }
        }
        return false
    }

    /**
     * Starts continuously adjusting the value in the given EditText.
     *
     * @param editText The EditText being adjusted.
     * @param increment Whether the value should be incremented or decremented.
     * @param isInteger Whether the value is an integer (true) or a decimal (false).
     */
    private fun startAdjusting(editText: EditText, increment: Boolean, isInteger: Boolean)
    {
        val runnable = object : Runnable
        {
            override fun run()
            {
                if (isInteger)
                {
                    adjustIntegerValue(editText, increment)
                }
                else
                {
                    adjustDecimalValue(editText, increment)
                }
                handler.postDelayed(this, 100)  // Adjust every 100ms
            }
        }
        handler.post(runnable)
    }

    /**
     * Stops the continuous adjustment of values.
     */
    private fun stopAdjusting()
    {
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * Adjusts the integer value in the given EditText by incrementing or decrementing it.
     *
     * @param editText The EditText containing the value.
     * @param increment Whether to increment or decrement the value.
     */
    private fun adjustIntegerValue(editText: EditText, increment: Boolean)
    {
        val currentValue = editText.text.toString().toIntOrNull() ?: 0
        val newValue = if (increment) currentValue + 1 else currentValue - 1
        if (newValue >= 0) // Ensure the value does not drop below 0
        {
            editText.setText(newValue.toString())
        }
    }

    /**
     * Adjusts the decimal value in the given EditText by incrementing or decrementing it by 0.1.
     *
     * @param editText The EditText containing the value.
     * @param increment Whether to increment or decrement the value.
     */
    private fun adjustDecimalValue(editText: EditText, increment: Boolean)
    {
        val currentValue = editText.text.toString().toDoubleOrNull() ?: 0.0
        val newValue = if (increment) currentValue + 0.1 else currentValue - 0.1
        if (newValue >= 0) // Ensure the value does not drop below 0
        {
            editText.setText(String.format(Locale.US, "%.1f", newValue))
        }
    }

    /**
     * Converts the units of height and weight based on the selected metric system.
     *
     * @param toMetric Whether to convert to the metric system (true) or imperial system (false).
     */
    private fun convertUnits(toMetric: Boolean)
    {
        val height = binding.heightEditText.text.toString().toDoubleOrNull() ?: 0.0
        val weight = binding.weightEditText.text.toString().toDoubleOrNull() ?: 0.0

        if (toMetric)
        {
            // Convert to metric
            val convertedHeight = height * 2.54 // inches to centimeters
            val convertedWeight = weight / 2.205 // pounds to kilograms

            binding.heightEditText.setText(String.format(Locale.US, "%.1f", convertedHeight))
            binding.weightEditText.setText(String.format(Locale.US, "%.1f", convertedWeight))

            binding.heightUnitTextView.text = getString(R.string.cm)
            binding.weightUnitTextView.text = getString(R.string.kg)
        }
        else
        {
            // Convert to imperial
            val convertedHeight = height / 2.54 // centimeters to inches
            val convertedWeight = weight * 2.205 // kilograms to pounds

            binding.heightEditText.setText(String.format(Locale.US, "%.1f", convertedHeight))
            binding.weightEditText.setText(String.format(Locale.US, "%.1f", convertedWeight))

            binding.heightUnitTextView.text = getString(R.string.inches)
            binding.weightUnitTextView.text = getString(R.string.pounds)
        }
    }

    /**
     * Saves the health statistics entered by the user and shows a confirmation message.
     */
    private fun saveHealthStat()
    {
        val fullName = binding.fullNameEditText.text.toString()
        val age = binding.ageEditText.text.toString().toIntOrNull() ?: 0
        val height = binding.heightEditText.text.toString().toDoubleOrNull() ?: 0.0
        val weight = binding.weightEditText.text.toString().toDoubleOrNull() ?: 0.0
        val metric = binding.unitSwitch.isChecked

        // Create a new HealthStat object
        val healthStat = HealthStat(
            id = UUID.randomUUID().toString(),
            fullName = fullName,
            age = age,
            height = height,
            weight = weight,
            metric = metric
        )

    }

    /**
     * Navigates to the MainActivity and passes the entered information.
     */
    private fun goToMainActivity()
    {
        // Create an Intent to start MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("fullName", binding.fullNameEditText.text.toString())
        intent.putExtra("age", binding.ageEditText.text.toString().toIntOrNull() ?: 0)
        intent.putExtra("height", binding.heightEditText.text.toString().toDoubleOrNull() ?: 0.0)
        intent.putExtra("weight", binding.weightEditText.text.toString().toDoubleOrNull() ?: 0.0)
        intent.putExtra("metric", binding.unitSwitch.isChecked)  // Pass the metric system selection
        startActivity(intent)
    }

    /**
     * Resets the form fields to their original state, including the unit system.
     */
    private fun resetForm()
    {
        // Reset the unit switch first
        binding.unitSwitch.isChecked = originalMetric

        // Set the correct unit labels
        if (originalMetric)
        {
            binding.heightUnitTextView.text = getString(R.string.cm)
            binding.weightUnitTextView.text = getString(R.string.kg)
        }
        else
        {
            binding.heightUnitTextView.text = getString(R.string.inches)
            binding.weightUnitTextView.text = getString(R.string.pounds)
        }

        // Directly reset the form fields to their original state without conversion
        binding.fullNameEditText.setText(originalFullName)
        binding.ageEditText.setText(originalAge.toString())
        binding.heightEditText.setText(String.format(Locale.US, "%.1f", originalHeight))
        binding.weightEditText.setText(String.format(Locale.US, "%.1f", originalWeight))
    }
}
