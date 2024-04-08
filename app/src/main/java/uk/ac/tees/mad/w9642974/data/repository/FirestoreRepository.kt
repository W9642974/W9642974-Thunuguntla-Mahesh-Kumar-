package uk.ac.tees.mad.w9642974.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.w9642974.domain.Member
import uk.ac.tees.mad.w9642974.domain.Project
import uk.ac.tees.mad.w9642974.domain.Resource
import javax.inject.Inject

interface FirestoreRepository {
    fun getAllMembers(): Flow<Resource<List<Member>>>
    fun createNewProject(project: Project): Flow<Resource<String>>
    fun getMyProjects(): Flow<Resource<List<Project>>>
}

class FirestoreRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FirestoreRepository {
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
                    name = data["username"] as String,
                    email = data["email"] as String
                )
            }
            trySend(Resource.Success(items))
        } catch (ex: Exception) {
            trySend(Resource.Error("Error fetching Members"))
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
        val userId = auth.currentUser?.uid!!

        try {
            val projects = mutableListOf<Project>()
            val querySnapshot = firestore.collection("projects")
                .whereArrayContains("members", userId)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                val data = document.data

                val item = Project(
                    id = data?.get("id") as String,
                    name = data["name"] as String,
                    description = data["description"] as String,
                    startDate = (data["startDate"] as Timestamp).toDate(),
                    endDate = (data["endDate"] as Timestamp).toDate(),
                    members = data["members"] as List<String>,
                    createdBy = data["createdBy"] as String,
                    isCompleted = data["completed"] as Boolean
                )
                projects.add(item)
            }

            trySend(Resource.Success(projects))
        } catch (e: Exception) {
            trySend(Resource.Error("Error fetching projects: ${e.message}"))
        }

        awaitClose { close() }

    }

}