package uk.ac.tees.mad.w9642974.domain

import java.util.Date

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val assignedTo: List<Member> = emptyList(),
    val dueDate: Date = Date(),
    val status: String = "",
    val priority: String = "",
    val isCompleted: Boolean = false
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "assignedTo" to assignedTo,
            "dueDate" to dueDate,
            "status" to status,
            "priority" to priority,
            "isCompleted" to isCompleted
        )
    }
}