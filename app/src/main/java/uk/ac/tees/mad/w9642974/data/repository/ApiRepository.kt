package uk.ac.tees.mad.w9642974.data.repository

import uk.ac.tees.mad.w9642974.data.apiService.ApiService
import uk.ac.tees.mad.w9642974.domain.Quotes
import javax.inject.Inject

interface ApiRepository {
    suspend fun getQuote(): Quotes
}

class ApiRepositoryImpl @Inject constructor (
    private val apiService: ApiService
): ApiRepository{
    override suspend fun getQuote(): Quotes = apiService.getQuotes()

}