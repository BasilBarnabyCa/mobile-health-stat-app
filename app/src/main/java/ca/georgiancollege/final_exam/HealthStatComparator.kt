package ca.georgiancollege.final_exam

import androidx.recyclerview.widget.DiffUtil

// This is a utility class that helps the RecyclerView Adapter determine
// how to efficiently update the list of HealthStats when the data changes
class HealthStatComparator: DiffUtil.ItemCallback<HealthStat>()
{
    // this method checks if two TVShows have the same ID
    override fun areItemsTheSame(oldItem: HealthStat, newItem: HealthStat): Boolean
    {
        return oldItem.id == newItem.id
    }

    // this method checks if two HealthStats have the same content
    override fun areContentsTheSame(oldItem: HealthStat, newItem: HealthStat): Boolean
    {
        return oldItem == newItem
    }
}