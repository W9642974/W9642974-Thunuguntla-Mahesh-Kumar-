package uk.ac.tees.mad.w9642974.domain

import java.util.Date
import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val members: List<String> = listOf(),
    val files: List<ProjectFile> = emptyList(),
    val createdBy: String = "",
    val tasks: List<Task> = emptyList(),
    val isCompleted: Boolean = false,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0
) {
    val progress: Float
        get() = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks.toFloat()) * 100 else 0f
}
