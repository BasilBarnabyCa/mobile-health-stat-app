package ca.georgiancollege.final_exam

import java.time.LocalDate
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.Date

@IgnoreExtraProperties
data class HealthStat(
    @DocumentId val id: String = "",
    val fullName: String = "",
    val age: Int = 0,
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val date: String = "",
//    val date: LocalDate = LocalDate.now(),
    val stat: String = "",
    val metric: Boolean = false
)
{
    /**
     * No-argument constructor required by Firestore deserialization.
     * Initializes all properties with default values.
     */
    constructor() : this("", "", 0, 0.0, 0.0, "", "", false)
}



