package ca.georgiancollege.final_exam

import androidx.recyclerview.widget.RecyclerView
import ca.georgiancollege.final_exam.databinding.TextRowItemBinding

// HealthStatViewHolder is a ViewHolder class that holds a reference to the TextRowItemBinding
// and displays the HealthStat data in the UI for each row
class HealthStatViewHolder (val binding: TextRowItemBinding):
    RecyclerView.ViewHolder(binding.root)
{
        // This method binds the HealthStat data to the UI elements in the ViewHolder
        fun bind(healthStat: HealthStat)
        {
            // Sets the text for the title, genre, and rating TextView
            binding.weightTextView.text = healthStat.weight.toString()
            binding.statTextView.text = healthStat.stat
            binding.dateTextView.text = healthStat.date.toString()
        }
}