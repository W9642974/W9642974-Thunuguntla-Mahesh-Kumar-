package uk.ac.tees.mad.w9642974.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.w9642974.domain.Member
import uk.ac.tees.mad.w9642974.domain.Project
import uk.ac.tees.mad.w9642974.domain.ProjectFile
import uk.ac.tees.mad.w9642974.domain.Resource
import uk.ac.tees.mad.w9642974.domain.Task
import uk.ac.tees.mad.w9642974.presentation.profile.UserResponse
import uk.ac.tees.mad.w9642974.presentation.projectdetail.viewmodels.ProjectUiState
import java.util.Date
import javax.inject.Inject

interface FirestoreRepository {
    fun getCurrentUser(): Flow<Resource<UserResponse>>
    fun getAllMembers(): Flow<Resource<List<Member>>>

    fun getAllProjectMembers(
        projectId: String
    ): Flow<Resource<List<Member>>>

    fun createNewProject(project: Project): Flow<Resource<String>>

    fun getMyProjects(): Flow<Resource<List<Project>>>

    fun getProjectById(projectId: String): Flow<Resource<ProjectUiState>>

    suspend fun addTaskToProject(projectId: String, task: Task): Flow<Resource<String>>

    suspend fun updateTaskInProject(
        projectId: String,
        task: Task
    ): Flow<Resource<String>>

    suspend fun uploadFileAndStoreInfo(
        projectId: String,
        fileName: String,
        fileDescription: String,
        fileBytes: ByteArray
    ): Flow<Resource<String>>

    suspend fun removeMembersFromProject(
        projectId: String,
        memberIdToRemove: String
    ): Flow<Resource<String>>

    fun addMemberToProject(projectId: String, memberIds: List<String>): Flow<Resource<String>>

    suspend fun deleteTaskFromProject(projectId: String, taskId: String): Flow<Resource<String>>

    suspend fun deleteProject(projectId: String): Flow<Resource<String>>

}

class FirestoreRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : FirestoreRepository {

    override fun getCurrentUser(): Flow<Resource<UserResponse>> = callbackFlow {
        trySend(Resource.Loading())
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            firestore.collection("users").document(currentUserUid).get()
                .addOnSuccessListener { mySnapshot ->
                    if (mySnapshot.exists()) {
                        val data = mySnapshot.data

                        if (data != null) {
                            val userResponse = UserResponse(
                                key = currentUserUid,
                                item = UserResponse.CurrentUser(
                                    name = data["username"] as String? ?: "",
                                    email = data["email"] as String? ?: "",
                                    profileImage = data["profileImage"] as String? ?: ""
                                )
                            )

                            trySend(Resource.Success(userResponse))
                        } else {
                            trySend(Resource.Error(message = "No data found in Database"))

                            println("No data found in Database")
                        }
                    } else {
                        trySend(Resource.Error(message = "No data found in Database"))
                        println("No data found in Database")
                    }
                }.addOnFailureListener { e ->
                    trySend(Resource.Error(message = e.toString()))
                }
        } else {
            trySend(Resource.Error(message = "User not signed up"))
        }
        awaitClose {
            close()
        }
    }

    override fun getAllMembers(): Flow<Resource<List<Member>>> = callbackFlow {
        trySend(Resource.Loading())
        try {
            val documents = firestore.collection("users").get().await()
            val items = documents.filter {
                it.id != auth.currentUser?.uid
            }.mapNotNull { document ->
                val data = document.data
                Member(
                    id = document.id,
                    username = data["username"] as String? ?: "Alex adams",
                    email = data["email"] as String? ?: "alex.adams@email.com",
                    profileImage = data["profileImage"] as String?
                        ?: "https://t3.ftcdn.net/jpg/02/43/12/34/360_F_243123463_zTooub557xEWABDLk0jJklDyLSGl2jrr.jpg"
                )
            }
            trySend(Resource.Success(items))
        } catch (ex: Exception) {
            ex.printStackTrace()
            trySend(Resource.Error("Error fetching Members"))
        }

        awaitClose { close() }
    }

    override fun getAllProjectMembers(projectId: String): Flow<Resource<List<Member>>> =
        callbackFlow {
            trySend(Resource.Loading())
            try {
                // Fetch the project document to get the list of member IDs
                val projectSnapshot =
                    firestore.collection("projects").document(projectId).get().await()
                if (projectSnapshot.exists()) {
                    val memberIds = projectSnapshot["members"] as? List<String> ?: emptyList()
                    if (memberIds.isNotEmpty()) {
                        // Fetch user documents based on the list of member IDs
                        val documents =
                            firestore.collection("users").whereIn(FieldPath.documentId(), memberIds)
                                .get().await()
                        val items = documents.documents.mapNotNull { document ->
                            Member(
                                id = document.id,
                                username = document["username"] as String? ?: "",
                                email = document["email"] as String? ?: "",
                                profileImage = document["profileImage"] as String? ?: ""
                            )
                        }
                        trySend(Resource.Success(items))
                    } else {
                        trySend(Resource.Success(emptyList<Member>()))
                    }
                } else {
                    trySend(Resource.Error("Project not found"))
                }
            } catch (ex: Exception) {
                trySend(Resource.Error("Error fetching Members: ${ex.message}"))
            }
            awaitClose { close() }
        }

    override fun createNewProject(project: Project): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())

        try {
            val documentReference = firestore.collection("projects").add(project).await()
            val projectId = documentReference.id
            val updatedProject = project.copy(id = projectId)
            firestore.collection("projects").document(projectId).set(updatedProject).await()
            trySend(Resource.Success(documentReference.id))
        } catch (ex: Exception) {
            trySend(Resource.Error("Error creating project: ${ex.message}"))
        }

        awaitClose { close() }
    }

    override fun getMyProjects(): Flow<Resource<List<Project>>> = callbackFlow {
        trySend(Resource.Loading())
        val userId = auth.currentUser?.uid ?: trySend(Resource.Error("User not logged in"))

        try {
            val projects = mutableListOf<Project>()
            val querySnapshot = firestore.collection("projects")
                .whereArrayContains("members", userId)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                val data = document.data ?: continue
                // Fetch tasks for the project
                val tasksSnapshot =
                    firestore.collection("projects").document(document.id).collection("tasks").get()
                        .await()
                val tasks = tasksSnapshot.documents.mapNotNull { taskDoc ->
                    if (taskDoc.exists()) {
                        val assignedToMaps =
                            taskDoc["assignedTo"] as List<Map<String, *>>? ?: emptyList()
                        val assignedMembers = assignedToMaps.mapNotNull { memberId ->
                            val memberDoc =
                                firestore.collection("users").document(memberId["id"] as String)
                                    .get().await()
                            if (memberDoc.exists()) {
                                Member(
                                    id = memberDoc.id,
                                    username = memberDoc["username"] as String,
                                    email = memberDoc["email"] as String,
                                    profileImage = memberDoc["profileImage"] as String
                                )
                            } else null
                        }

                        Task(
                            id = taskDoc.id,
                            title = taskDoc["title"] as? String ?: "",
                            description = taskDoc["description"] as? String ?: "",
                            assignedTo = assignedMembers,
                            dueDate = (taskDoc["dueDate"] as? Timestamp)?.toDate() ?: Date(),
                            isCompleted = taskDoc["isCompleted"] as? Boolean ?: false,
                            status = taskDoc["status"] as? String ?: "",
                            priority = taskDoc["priority"] as? String ?: ""
                        )
                    } else null
                }

                val project = Project(
                    id = document.id,
                    name = data["name"] as? String ?: "",
                    description = data["description"] as? String ?: "",
                    startDate = (data["startDate"] as? Timestamp)?.toDate() ?: Date(),
                    endDate = (data["endDate"] as? Timestamp)?.toDate() ?: Date(),
                    members = data["members"] as? List<String> ?: emptyList(),
                    createdBy = data["createdBy"] as? String ?: "",
                    isCompleted = data["completed"] as? Boolean ?: false,
                    totalTasks = tasks.size,
                    completedTasks = tasks.count { it.isCompleted },
                    tasks = tasks
                )
                projects.add(project)
            }

            trySend(Resource.Success(projects))
        } catch (e: Exception) {
            trySend(Resource.Error("Error fetching projects: ${e.message}"))
            e.printStackTrace()
        }
        awaitClose { close() }
    }

    override fun getProjectById(projectId: String): Flow<Resource<ProjectUiState>> = callbackFlow {
        trySend(Resource.Loading())
        try {
            val documentSnapshot =
                firestore.collection("projects").document(projectId).get().await()
            if (documentSnapshot.exists()) {
                val memberIds = documentSnapshot["members"] as List<String> ?: emptyList()
                val members = memberIds.mapNotNull { memberId ->
                    val memberDoc = firestore.collection("users").document(memberId).get().await()
                    if (memberDoc.exists()) {
                        Member(
                            id = memberDoc.id,
                            username = memberDoc["username"] as String,
                            email = memberDoc["email"] as String,
                            profileImage = memberDoc["profileImage"] as String? ?: ""
                        )
                    } else null
                }

                // Fetch tasks for the project
                val tasksSnapshot =
                    firestore.collection("projects").document(projectId).collection("tasks").get()
                        .await()
                val tasks = tasksSnapshot.documents.mapNotNull { taskDoc ->
                    if (taskDoc.exists()) {
                        val assignedToMaps = taskDoc["assignedTo"] as List<Map<String, *>>
                        val assignedMembers = assignedToMaps.mapNotNull { memberMap ->
                            members.find { it.id == memberMap["id"] as String }
                        }

                        Task(
                            id = taskDoc.id,
                            title = taskDoc["title"] as String,
                            description = taskDoc["description"] as String,
                            assignedTo = assignedMembers,
                            dueDate = (taskDoc["dueDate"] as Timestamp).toDate(),
                            isCompleted = taskDoc["isCompleted"] as Boolean,
                            status = taskDoc["status"] as String,
                            priority = taskDoc["priority"] as String
                        )
                    } else null
                }

                // Fetch files for the project
                val filesSnapshot =
                    firestore.collection("projects").document(projectId).collection("files").get()
                        .await()
                val files = filesSnapshot.documents.mapNotNull { fileDoc ->
                    if (fileDoc.exists()) {
                        ProjectFile(
                            name = fileDoc["name"] as String,
                            url = fileDoc["url"] as String,
                            description = fileDoc["description"] as String
                        )
                    } else null
                }

                val projectUiState = ProjectUiState(
                    id = documentSnapshot.id,
                    members = members,
                    name = documentSnapshot["name"] as String,
                    description = documentSnapshot["description"] as String,
                    startDate = (documentSnapshot["startDate"] as Timestamp).toDate(),
                    endDate = (documentSnapshot["endDate"] as Timestamp).toDate(),
                    createdBy = documentSnapshot["createdBy"] as String,
                    isCompleted = documentSnapshot["completed"] as Boolean,
                    totalTasks = tasks.size,
                    completedTasks = tasks.count { it.isCompleted },
                    tasks = tasks,
                    files = files
                )
                trySend(Resource.Success(projectUiState))

            } else {
                trySend(Resource.Error("Project not found"))
            }

        } catch (e: Exception) {
            trySend(Resource.Error("Failed to fetch project with id($projectId): ${e.message}"))
            e.printStackTrace()
        }
        awaitClose { close() }
    }

    override suspend fun addTaskToProject(projectId: String, task: Task): Flow<Resource<String>> =
        callbackFlow {
            trySend(Resource.Loading())
            try {
                val taskMap = task.toMap()

                firestore.collection("projects")
                    .document(projectId)
                    .collection("tasks")
                    .add(taskMap)
                    .await()

                trySend(Resource.Success("Task added"))
            } catch (e: Exception) {
                // Return an error resource if an exception occurred
                trySend(Resource.Error(e.message ?: "An unknown error occurred"))
            }

            awaitClose {
                close()
            }
        }

    override suspend fun deleteTaskFromProject(
        projectId: String,
        taskId: String
    ): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        try {
            val projectDocumentRef = firestore.collection("projects").document(projectId)
            val taskDocumentRef = projectDocumentRef.collection("tasks").document(taskId)

            taskDocumentRef.delete().await()

            trySend(Resource.Success("Task deleted successfully"))
        } catch (e: Exception) {
            trySend(Resource.Error("Failed to delete task: ${e.message}"))
        }
        awaitClose { close() }
    }

    override suspend fun deleteProject(projectId: String): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        try {
            val projectDocumentRef = firestore.collection("projects").document(projectId)

            // Optional: Delete subcollections (e.g., tasks) associated with the project here

            projectDocumentRef.delete().await()

            trySend(Resource.Success("Project deleted successfully"))
        } catch (e: Exception) {
            trySend(Resource.Error("Failed to delete project: ${e.message}"))
        }
        awaitClose { close() }
    }

    override suspend fun updateTaskInProject(
        projectId: String,
        task: Task
    ): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        try {
            val projectDocumentRef = firestore.collection("projects").document(projectId)
            val taskDocumentRef = projectDocumentRef.collection("tasks").document(task.id)

            firestore.runTransaction { transaction ->
                val projectSnapshot = transaction.get(projectDocumentRef)
                val taskSnapshot = transaction.get(taskDocumentRef)

                val isCurrentlyCompleted = taskSnapshot.getBoolean("isCompleted") ?: false
                val completedTasks = projectSnapshot.getLong("completedTasks") ?: 0

                // Update task's completion status
                transaction.update(taskDocumentRef, task.toMap())

                // Update project's completedTasks count
                if (task.isCompleted && !isCurrentlyCompleted) {
                    transaction.update(projectDocumentRef, "completedTasks", completedTasks + 1)
                } else if (!task.isCompleted && isCurrentlyCompleted) {
                    transaction.update(projectDocumentRef, "completedTasks", completedTasks - 1)
                } else {

                }
            }.await()

            trySend(Resource.Success("Task updated successfully"))
        } catch (e: Exception) {
            trySend(Resource.Error("Failed to update task: ${e.message}"))
        }
        awaitClose { close() }
    }

    override suspend fun uploadFileAndStoreInfo(
        projectId: String,
        fileName: String,
        fileDescription: String,
        fileBytes: ByteArray
    ): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        try {
            val storageRef = storage.reference.child("projectFiles/$projectId/$fileName")
            val uploadTask = storageRef.putBytes(fileBytes).await()
            val fileUrl = uploadTask.storage.downloadUrl.await().toString()

            val projectFile = ProjectFile(fileName, fileUrl, fileDescription)
            firestore.collection("projects").document(projectId)
                .collection("files").add(projectFile).await()
            trySend(Resource.Success("File uploaded and info stored successfully."))
        } catch (e: Exception) {
            trySend(Resource.Error("Failed to upload file: ${e.message}"))
        }
        awaitClose { close() }
    }

    override suspend fun removeMembersFromProject(
        projectId: String,
        memberIdToRemove: String
    ): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        try {
            val projectDocumentRef = firestore.collection("projects").document(projectId)
            firestore.runTransaction { transaction ->
                val projectSnapshot = transaction.get(projectDocumentRef)
                val currentMembers = projectSnapshot["members"] as? List<String> ?: emptyList()
                val updatedMembers = currentMembers.filterNot { memberIdToRemove.contains(it) }

                transaction.update(projectDocumentRef, "members", updatedMembers)
            }.await()
            trySend(Resource.Success("Member removed."))

        } catch (e: Exception) {
            trySend(Resource.Error("Failed to remove members: ${e.message}"))
        }
        awaitClose { close() }
    }


    override fun addMemberToProject(
        projectId: String,
        memberIds: List<String>
    ): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())

        try {
            val projectDocumentRef = firestore.collection("projects").document(projectId)

            firestore.runTransaction { transaction ->
                val projectSnapshot = transaction.get(projectDocumentRef)
                val currentMembers = projectSnapshot["members"] as? List<String> ?: emptyList()

                // Filter out members already part of the project
                val newMembers = memberIds.filterNot { currentMembers.contains(it) }

                if (newMembers.isEmpty()) {
                    throw IllegalStateException("All members are already part of the project")
                }

                val updatedMembers = currentMembers + newMembers

                transaction.update(projectDocumentRef, "members", updatedMembers)
            }.await()

            trySend(Resource.Success("Member added"))
        } catch (e: Exception) {
            trySend(Resource.Error("Failed to add member: ${e.message}"))
        }

        awaitClose { close() }
    }

}