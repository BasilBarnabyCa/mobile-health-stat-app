package ca.georgiancollege.final_exam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ca.georgiancollege.final_exam.databinding.TextRowItemBinding

class HealthStatListAdapter(private val onItemClicked: (HealthStat) -> Unit):
ListAdapter<HealthStat, HealthStatViewHolder>(HealthStatComparator())
{
    // Called when a new ViewHolder (Custom TableView Cell) is created
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthStatViewHolder {
        // Inflate the layout for each row
        val binding = TextRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Return a new ViewHolder instance with the binding object
        return HealthStatViewHolder(binding)
    }

    // Called by the RecyclerView to display the data at a specific position
    override fun onBindViewHolder(holder: HealthStatViewHolder, position: Int) {
        // Get the Health Stat at the current position
        val current = getItem(position)
        // Bind the HealthStat data to the ViewHolder's UI elements
        holder.bind(current)
        // Set a click listener for the row item to handle item clicks
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
    }
}