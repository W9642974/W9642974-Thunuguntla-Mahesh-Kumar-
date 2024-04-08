package uk.ac.tees.mad.w9642974.domain

import java.util.Date
import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val startDate: Date,
    val endDate: Date,
    val members: List<String>,
    val createdBy: String,
    val isCompleted: Boolean = false
)
