package com.example.data.model

data class MotivationContent(
    val quote: String,
    val author: String,
    val story: String,
    val takeaway: String,
    val actionNudge: String,
    val category: String = "Mindset",
    val isIdleAlert: Boolean = false
)

enum class GoalCategory(val displayName: String, val iconName: String, val colorHex: Long) {
    CAREER("Career & Work", "work", 0xFF00E5FF),
    FITNESS("Fitness & Health", "fitness_center", 0xFF00E676),
    LEARNING("Learning & Skills", "school", 0xFFFFD54F),
    MINDSET("Mindset & Grit", "psychology", 0xFFFF9100),
    HABIT("Habits & Lifestyle", "auto_awesome", 0xFFE040FB),
    CREATIVE("Creative & Passion", "palette", 0xFFFF5252);

    companion object {
        fun fromString(name: String): GoalCategory {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) }
                ?: MINDSET
        }
    }
}
