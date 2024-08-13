package ca.georgiancollege.final_exam

import java.time.LocalDate
import java.util.Date

data class HealthStat(
    val id: String = "",
    val fullName: String = "",
    val age: Int = 0,
    val height: Double = 0.0,
    val weight: Double = 0.0,
    val date: LocalDate = LocalDate.now(),
    val stat: String = "",
    val metric: Boolean = false
)



