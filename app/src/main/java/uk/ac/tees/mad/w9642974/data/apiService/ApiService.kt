package uk.ac.tees.mad.w9642974.data.apiService

import retrofit2.http.GET
import uk.ac.tees.mad.w9642974.domain.Quotes

interface ApiService {
    @GET("/random?tags=courage|creativity|ethics|wisdom|time&maxLength=70")
    suspend fun getQuotes(): Quotes
}